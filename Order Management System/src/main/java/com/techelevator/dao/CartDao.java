package com.techelevator.dao;

import com.techelevator.model.Cart;

public interface CartDao {

    Cart getCartByUserId(int userId);

}
