package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.CartItemDetails;
import com.techelevator.model.TaxDto;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcCartItemDetailsDao implements CartItemDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    private static String TAX_URL = "https://teapi.netlify.app/api/statetax?state=";

    private RestTemplate restTemplate = new RestTemplate();

    public JdbcCartItemDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CartItemDetails> getCartItemDetailsByUserId(int userId) {
        List<CartItemDetails> cartItemDetails = new ArrayList<>();
        String sql = "SELECT p.name, ci.quantity, p.price FROM product p " +
                "JOIN cart_item ci ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                CartItemDetails cartItemDetail = mapRowToCartItemDetails(results);
                cartItemDetails.add(cartItemDetail);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return cartItemDetails;
    }

    @Override
    public BigDecimal getSubtotal(int userId) {
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CartItemDetails> cartItemDetails = getCartItemDetailsByUserId(userId);
        for (CartItemDetails cartItemDetail : cartItemDetails) {
            subtotal = subtotal.add(cartItemDetail.getPrice().multiply(BigDecimal.valueOf(cartItemDetail.getQuantity())));
        }
        return subtotal.setScale(2, RoundingMode.CEILING);
    }

    @Override
    public BigDecimal getTaxAmount(int userId) {
        BigDecimal taxAmount = BigDecimal.ZERO;
        String stateCode = "";
        String sql = "SELECT DISTINCT u.state_code FROM users u " +
                "JOIN cart_item ci ON ci.user_id = u.user_id " +
                "WHERE ci.user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if(results.next()) {
                stateCode = results.getString("state_code").toUpperCase();
            }
            TaxDto taxDto = restTemplate.getForObject(TAX_URL + stateCode, TaxDto.class);
            BigDecimal tax = taxDto.getSalesTax();
            taxAmount = tax.multiply(getSubtotal(userId));
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return taxAmount.setScale(2, RoundingMode.CEILING);
    }

    @Override
    public BigDecimal getCartTotal(int userId) {
        BigDecimal cartTotal = getSubtotal(userId).add(getTaxAmount(userId));
        return cartTotal.setScale(2, RoundingMode.CEILING);
    }

    private CartItemDetails mapRowToCartItemDetails(SqlRowSet results) {
        CartItemDetails cartItemDetails = new CartItemDetails();
        cartItemDetails.setProductName(results.getString("name"));
        cartItemDetails.setQuantity(results.getInt("quantity"));
        cartItemDetails.setPrice(results.getBigDecimal("price"));
        return cartItemDetails;
    }
}