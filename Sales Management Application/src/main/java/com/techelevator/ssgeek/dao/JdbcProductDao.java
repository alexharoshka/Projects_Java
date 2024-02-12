package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {

    private final String PRODUCT_SELECT = "SELECT p.product_id, p.name, p.description, " +
            "p.price, p.image_name FROM product p";

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Product getProductById(int productId) {
        Product product = null;
        String sql = PRODUCT_SELECT + " WHERE p.product_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);
            if (results.next()) {
                product = mapRowToProduct(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return product;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> allProducts = new ArrayList<>();
        String sql = PRODUCT_SELECT + " ORDER BY product_id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Product product = mapRowToProduct(results);
                allProducts.add(product);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return allProducts;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> productsWithNoSales = new ArrayList<>();
        String sql = PRODUCT_SELECT + " WHERE product_id NOT IN (SELECT product_id FROM line_item)";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Product product = mapRowToProduct(results);
                productsWithNoSales.add(product);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return productsWithNoSales;
    }

    @Override
    public Product createProduct(Product newProduct) {
        Product product = null;
        String sql = "INSERT INTO product (name, description, price, image_name) " +
                "VALUES (?, ?, ?, ?) RETURNING product_id";
        try {
            int newProductId = jdbcTemplate.queryForObject(sql, int.class, newProduct.getName(), newProduct.getDescription(),
                    newProduct.getPrice(), newProduct.getImageName());
            product = getProductById(newProductId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return product;
    }

    @Override
    public Product updateProduct(Product updatedProduct) {
        Product product = null;
        String sql = "UPDATE product SET name = ?, description = ?, price = ?, image_name = ? WHERE product_id = ?";
        try {
            int numberOfRows = jdbcTemplate.update(sql, updatedProduct.getName(), updatedProduct.getDescription(),
                    updatedProduct.getPrice(), updatedProduct.getImageName(), updatedProduct.getProductId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                product = getProductById(updatedProduct.getProductId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return product;
    }

    @Override
    public int deleteProductById(int productId) {
        int numberOfRows = 0;
        String deleteLineItemSql = "DELETE FROM line_item WHERE product_id = ?";
        String deleteProductSql = "DELETE FROM product WHERE product_id = ?";
        try {
            jdbcTemplate.update(deleteLineItemSql, productId);
            numberOfRows = jdbcTemplate.update(deleteProductSql, productId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }

    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setName(results.getString("name"));
        product.setDescription(results.getString("description"));
        product.setPrice(results.getBigDecimal("price"));
        if (results.getString("image_name") != null) {
            product.setImageName(results.getString("image_name"));
        }
        return product;
    }

}
