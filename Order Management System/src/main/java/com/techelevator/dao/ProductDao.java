package com.techelevator.dao;

import com.techelevator.model.Product;

import java.util.List;

public interface ProductDao {

    List<Product> getProducts();

    List<Product> getProductsBySku(String productSku);

    List<Product> getProductsByName(String name);

    Product getProductById(int id);

}
