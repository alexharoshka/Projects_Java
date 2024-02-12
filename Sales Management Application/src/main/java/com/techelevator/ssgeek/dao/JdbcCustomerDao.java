package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Customer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCustomerDao implements CustomerDao {

    private final String CUSTOMER_SELECT = "SELECT c.customer_id, c.name, c.street_address1, c.street_address2, " +
            "c.city, c.state, c.zip_code FROM customer c";

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustomerDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Customer getCustomerById(int id) {
        Customer customer = null;
        String sql = CUSTOMER_SELECT + " WHERE c.customer_id = ? ORDER BY c.customer_id";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                customer = mapRowToCustomer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return customer;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> allCustomers = new ArrayList<>();
        String sql = CUSTOMER_SELECT;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Customer customer = mapRowToCustomer(results);
                allCustomers.add(customer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return allCustomers;
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        Customer customer = null;
        String sql = "INSERT INTO customer (name, street_address1, street_address2, city, state, zip_code) " +
                "VALUES (?, ?, ?, ?, ? ,?) RETURNING customer_id";
        try {
            int newCustomerId = jdbcTemplate.queryForObject(sql, int.class, newCustomer.getName(), newCustomer.getStreetAddress1(),
                    newCustomer.getStreetAddress2(), newCustomer.getCity(), newCustomer.getState(), newCustomer.getZipCode());
            customer = getCustomerById(newCustomerId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer updatedCustomer) {
        Customer customer = null;
        String sql = "UPDATE customer SET name = ?, street_address1 = ?, " +
                "street_address2 = ?, city = ?, state = ?, zip_code = ? WHERE customer_id = ?";
        try {
            int numberOfRows = jdbcTemplate.update(sql, updatedCustomer.getName(), updatedCustomer.getStreetAddress1(),
                    updatedCustomer.getStreetAddress2(), updatedCustomer.getCity(), updatedCustomer.getState(),
                    updatedCustomer.getZipCode(), updatedCustomer.getCustomerId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                customer = getCustomerById(updatedCustomer.getCustomerId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return customer;
    }

    private Customer mapRowToCustomer(SqlRowSet results) {
        Customer customer = new Customer();
        customer.setCustomerId(results.getInt("customer_id"));
        customer.setName(results.getString("name"));
        customer.setStreetAddress1(results.getString("street_address1"));
        if (results.getString("street_address2") != null) {
            customer.setStreetAddress2(results.getString("street_address2"));
        }
        customer.setCity(results.getString("city"));
        if (results.getString("state").length() == 2) {
            customer.setState(results.getString("state"));
        }
        if (results.getString("zip_code").length() == 5) {
            customer.setZipCode(results.getString("zip_code"));
        }
        return customer;
    }

}
