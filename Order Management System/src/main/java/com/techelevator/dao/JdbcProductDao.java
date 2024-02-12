package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Product;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcProductDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY name";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Product product = mapRowToProduct(results);
                products.add(product);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return products;
    }

    @Override
    public List<Product> getProductsBySku(String sku) {
        List<Product> products = new ArrayList<>();
        sku = "%" + sku + "%";
        String sql = "SELECT * FROM product WHERE product_sku ILIKE ? ORDER BY name";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, sku);
            while (results.next()) {
                Product product = mapRowToProduct(results);
                products.add(product);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return products;
    }

    @Override
    public List<Product> getProductsByName(String name) {
        List<Product> products = new ArrayList<>();
        name = "%" + name + "%";
        String sql = "SELECT * FROM product WHERE name ILIKE ? ORDER BY name";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);
            while (results.next()) {
                Product product = mapRowToProduct(results);
                products.add(product);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return products;
    }

    @Override
    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                product = mapRowToProduct(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return product;
    }

    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setProductSku(results.getString("product_sku"));
        product.setName(results.getString("name"));
        if (results.getString("description") != null) {
            product.setDescription(results.getString("description"));
        }
        product.setPrice(results.getBigDecimal("price"));
        if (results.getString("image_name") != null) {
            product.setImageName(results.getString("image_name"));
        }
        return product;
    }
}