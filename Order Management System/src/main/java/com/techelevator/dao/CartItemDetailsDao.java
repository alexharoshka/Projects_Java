package com.techelevator.dao;

import com.techelevator.model.CartItemDetails;

import java.math.BigDecimal;
import java.util.List;

public interface CartItemDetailsDao {

    List<CartItemDetails> getCartItemDetailsByUserId(int id);

    BigDecimal getSubtotal(int id);

    BigDecimal getTaxAmount(int id);

    BigDecimal getCartTotal(int id);

}
