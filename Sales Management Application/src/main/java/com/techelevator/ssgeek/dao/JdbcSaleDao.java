package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Customer;
import com.techelevator.ssgeek.model.Sale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final String SALE_SELECT = "SELECT s.sale_id, s.customer_id, " +
            "s.sale_date, s.ship_date, c.name FROM sale s JOIN customer c " +
            "ON c.customer_id = s.customer_id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSaleById(int saleId) {
        Sale sale = null;
        String sql = SALE_SELECT + " WHERE sale_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, saleId);
            if (results.next()) {
                sale = mapRowToSale(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return sale;
    }

    @Override
    public List<Sale> getUnshippedSales() {
        List<Sale> unshippedSales = new ArrayList<>();
        String sql = SALE_SELECT + " WHERE ship_date IS NULL ORDER BY sale_id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Sale sale = mapRowToSale(results);
                unshippedSales.add(sale);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return unshippedSales;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> salesByCustomerId = new ArrayList<>();
        String sql = SALE_SELECT + " WHERE s.customer_id = ? ORDER BY sale_id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerId);
            while (results.next()) {
                Sale sale = mapRowToSale(results);
                salesByCustomerId.add(sale);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return salesByCustomerId;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> salesByProductId = new ArrayList<>();
        String sql = SALE_SELECT + " JOIN line_item l ON l.sale_id = s.sale_id " +
                "WHERE l.product_id = ? ORDER BY s.sale_id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);
            while (results.next()) {
                Sale sale = mapRowToSale(results);
                salesByProductId.add(sale);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return salesByProductId;
    }

    @Override
    public Sale createSale(Sale newSale) {
        Sale sale = null;
        String sql = "INSERT INTO sale (customer_id, sale_date, ship_date) " +
                "VALUES (?, ?, ?) RETURNING sale_id";
        try {
            int newSaleId = jdbcTemplate.queryForObject(sql, int.class, newSale.getCustomerId(),
                    newSale.getSaleDate(), newSale.getShipDate());
            sale = getSaleById(newSaleId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return sale;
    }

    @Override
    public Sale updateSale(Sale updatedSale) {
        Sale sale = null;
        String updateSaleSql = "UPDATE sale SET sale_date = ?, ship_date = ? WHERE sale_id = ?";
        String updateCustomerSql = "UPDATE customer SET name = ? WHERE customer_id = " +
                "(SELECT customer_id FROM sale WHERE sale_id = ?)";
        try {
            int numberOfRows = jdbcTemplate.update(updateCustomerSql, updatedSale.getCustomerName(),
                    updatedSale.getSaleId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                sale = getSaleById(updatedSale.getSaleId());
            }
            numberOfRows = jdbcTemplate.update(updateSaleSql, updatedSale.getSaleDate(),
                    updatedSale.getShipDate(), updatedSale.getSaleId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                sale = getSaleById(updatedSale.getSaleId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return sale;
    }

    @Override
    public int deleteSaleById(int saleId) {
        int numberOfRows = 0;
        String deleteLineItemSql = "DELETE FROM line_item WHERE sale_id = ?";
        String deleteSaleSql = "DELETE FROM sale WHERE sale_id = ?";
        try {
            jdbcTemplate.update(deleteLineItemSql, saleId);
            numberOfRows = jdbcTemplate.update(deleteSaleSql, saleId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }

    private Sale mapRowToSale(SqlRowSet results) {
        Sale sale = new Sale();
        sale.setSaleId(results.getInt("sale_id"));
        sale.setCustomerId(results.getInt("customer_id"));
        sale.setSaleDate(results.getDate("sale_date").toLocalDate());
        if (results.getDate("ship_date") != null) {
            sale.setShipDate(results.getDate("ship_date").toLocalDate());
        }
        sale.setCustomerName(results.getString("name"));
        return sale;
    }

}
