package com.techelevator.controller;

import com.techelevator.dao.CartDao;
import com.techelevator.dao.CartItemDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.Cart;
import com.techelevator.model.CartItem;
import com.techelevator.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartItemDao cartItemDao;
    private final UserDao userDao;
    private final CartDao cartDao;

    public CartController(CartItemDao cartItemDao, UserDao userDao, CartDao cartDao) {
        this.cartItemDao = cartItemDao;
        this.userDao = userDao;
        this.cartDao = cartDao;
    }

    // Get the user's cart (Use Case 4)
    @RequestMapping(method = RequestMethod.GET)
    public Cart getCart(Principal principal) {
        String userName = principal.getName();
        User user = userDao.getUserByUsername(userName);
        return cartDao.getCartByUserId(user.getId());
    }

    // Add an item to the user's cart (Use Case 5)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/items", method = RequestMethod.POST)
    public CartItem create(@Valid @RequestBody CartItem item, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        return cartItemDao.putItemInCart(user.getId(), item);
    }

    // Remove the item from the user's cart (Use Case 6)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/items/{productId}", method = RequestMethod.DELETE)
    public void deleteItemFromCart(@PathVariable int productId, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        cartItemDao.deleteItemFromCartByProductId(user.getId(), productId);
    }

    // Clear the user's cart (Use Case 7)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteCart(Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        cartItemDao.deleteCartByUserId(user.getId());
    }
}
