package com.techelevator.dao;

import com.techelevator.model.Cart;
import org.springframework.stereotype.Component;

@Component
public class JdbcCartDao implements CartDao {

    private final JdbcCartItemDetailsDao jdbcCartItemDetailsDao;

    public JdbcCartDao(JdbcCartItemDetailsDao jdbcCartItemDetailsDao) {
        this.jdbcCartItemDetailsDao = jdbcCartItemDetailsDao;
    }

    @Override
    public Cart getCartByUserId(int userId) {
        Cart cart = new Cart();
        cart.setItemsInCart(jdbcCartItemDetailsDao.getCartItemDetailsByUserId(userId));
        cart.setSubtotal(jdbcCartItemDetailsDao.getSubtotal(userId));
        cart.setTaxAmount(jdbcCartItemDetailsDao.getTaxAmount(userId));
        cart.setCartTotal(jdbcCartItemDetailsDao.getCartTotal(userId));
        return cart;
    }
}
