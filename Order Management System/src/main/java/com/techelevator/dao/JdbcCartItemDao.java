package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.CartItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcCartItemDao implements CartItemDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCartItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CartItem getCartItemByCartItemId(int cartItemId) {
        CartItem cartItem = null;
        String sql = "SELECT * FROM cart_item WHERE cart_item_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, cartItemId);
            if (results.next()) {
                cartItem = mapRowToCartItem(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return cartItem;
    }

    @Override
    public CartItem putItemInCart(int userId, CartItem newCartItem) {
        CartItem cartItem = null;
        int quantity = 0;
        String sql = "SELECT quantity FROM cart_item WHERE user_id = ? AND product_id = ?";
        int productId = newCartItem.getProductId();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, productId);
            if (results.next()) {
                quantity = results.getInt("product_id");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        if (quantity > 0) {
            quantity += newCartItem.getQuantity();
            String updateSql = "UPDATE cart_item SET quantity = ? " +
                    "WHERE user_id = ? AND product_id = ? RETURNING cart_item_id";
            try {
                int numberOfRows = jdbcTemplate.update(updateSql, quantity, userId, newCartItem.getProductId());
                if (numberOfRows == 0) {
                    throw new DaoException("Zero rows affected, expected at least one");
                } else {
                    cartItem = getCartItemByCartItemId(newCartItem.getCartItemId());
                }
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
            return cartItem;
        } else {
            String insertSql = "INSERT INTO cart_item (user_id, product_id, quantity) " +
                    "VALUES (?, ?, ?) RETURNING cart_item_id";
            try {
                int newCartItemId = jdbcTemplate.queryForObject(insertSql, int.class,
                        userId, newCartItem.getProductId(), newCartItem.getQuantity());
                cartItem = getCartItemByCartItemId(newCartItemId);
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }
        }
        return cartItem;
    }

    @Override
    public int deleteItemFromCartByProductId(int userId, int productId) {
        int numberOfRows = 0;
        String sql = "DELETE FROM cart_item WHERE user_id = ? AND product_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(sql, userId, productId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }

    @Override
    public int deleteCartByUserId(int userId) {
        int numberOfRows = 0;
        String sql = "DELETE FROM cart_item WHERE user_id = ?";
        try {
            numberOfRows = jdbcTemplate.update(sql, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;
    }

    private CartItem mapRowToCartItem(SqlRowSet results) {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(results.getInt("cart_item_id"));
        cartItem.setUserId(results.getInt("user_id"));
        cartItem.setProductId(results.getInt("product_id"));
        cartItem.setQuantity(results.getInt("quantity"));
        return cartItem;
    }

}
