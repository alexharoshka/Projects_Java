package com.techelevator.model;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    private List<CartItemDetails> itemsInCart;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal cartTotal;

    public List<CartItemDetails> getItemsInCart() {
        return itemsInCart;
    }
    public void setItemsInCart(List<CartItemDetails> itemsInCart) {
        this.itemsInCart = itemsInCart;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }

    public Cart() {}

    public Cart(List<CartItemDetails> itemsInCart, BigDecimal subtotal, BigDecimal taxAmount, BigDecimal cartTotal) {
        this.itemsInCart = itemsInCart;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.cartTotal = cartTotal;
    }
}
