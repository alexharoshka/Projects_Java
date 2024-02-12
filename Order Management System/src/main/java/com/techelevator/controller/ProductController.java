package com.techelevator.controller;

import com.techelevator.dao.ProductDao;
import com.techelevator.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
@PreAuthorize("permitAll")
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    // Get the list of products (Use Case 1)
    // Search for products (Use Case 2)
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Product> list(@RequestParam(defaultValue = "") String sku,
                              @RequestParam(defaultValue = "") String name) {
        if (!name.equals("")) {
            return productDao.getProductsByName(name);
        } else if (!sku.equals("")) {
            return productDao.getProductsBySku(sku);
        } else {
            return productDao.getProducts();
        }
    }

    // Get a single product (Use Case 3)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Product get(@PathVariable int id) {
        Product product = productDao.getProductById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found");
        } else {
            return product;
        }
    }
}
