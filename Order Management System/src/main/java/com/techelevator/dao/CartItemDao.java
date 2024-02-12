package com.techelevator.dao;

import com.techelevator.model.CartItem;

public interface CartItemDao {

    CartItem getCartItemByCartItemId(int cartItemId);

    CartItem putItemInCart(int userId, CartItem newCartItem);

    int deleteItemFromCartByProductId(int userId, int productId);

    int deleteCartByUserId(int userId);

}
