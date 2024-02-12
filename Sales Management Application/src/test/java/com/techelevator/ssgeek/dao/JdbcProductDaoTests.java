package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class JdbcProductDaoTests extends BaseDaoTests {

    private static final Product PRODUCT_1 = new Product(1, "Product 1",
            "Description 1", new BigDecimal("9.99"), "product-1.png");
    private static final Product PRODUCT_2 = new Product(2, "Product 2",
            "Description 2", new BigDecimal("19.00"), "product-2.png");
    private static final Product PRODUCT_3 = new Product(3, "Product 3",
            "Description 3", new BigDecimal("123.45"), "product-3.png");
    private static final Product PRODUCT_4 = new Product(4, "Product 4",
            "Description 4", new BigDecimal("0.99"), "product-4.png");

    private JdbcProductDao dao;

    private Product testProduct;

    @Before
    public void setup() {
        dao = new JdbcProductDao(dataSource);
        testProduct = new Product(0, "test name", "test description",
                new BigDecimal("99.99"), "test image name");
    }

    @Test
    public void getProductById_with_valid_id_returns_correct_product() {
        Product product = dao.getProductById(1);
        assertProductsMatch(PRODUCT_1, product);

        product = dao.getProductById(2);
        assertProductsMatch(PRODUCT_2, product);
    }

    @Test
    public void getProductById_with_invalid_id_returns_null() {
        Product product = dao.getProductById(99);
        Assert.assertNull(product);
    }

    @Test
    public void getProducts_returns_a_list_of_all_products() {
        List<Product> allProducts = dao.getProducts();
        Assert.assertEquals(4, allProducts.size());
        assertProductsMatch(PRODUCT_1, allProducts.get(0));
        assertProductsMatch(PRODUCT_2, allProducts.get(1));
        assertProductsMatch(PRODUCT_3, allProducts.get(2));
        assertProductsMatch(PRODUCT_4, allProducts.get(3));
    }

    @Test
    public void getProductsWithNoSales_returns_a_list_of_products_where_sale_id_is_null() {
        List<Product> productsWithNoSales = dao.getProductsWithNoSales();
        Assert.assertEquals(1, productsWithNoSales.size());
        assertProductsMatch(PRODUCT_3, productsWithNoSales.get(0));
    }

    @Test
    public void createProduct_creates_product() {
        Product createdProduct = dao.createProduct(testProduct);

        int newId = createdProduct.getProductId();
        Assert.assertTrue(newId > 0);

        Product retrievedProduct = dao.getProductById(newId);
        assertProductsMatch(createdProduct, retrievedProduct);
    }

    @Test
    public void updateProduct_updates_product() {
        Product productToUpdate = dao.getProductById(1);

        productToUpdate.setName("Updated Product");
        productToUpdate.setDescription("Updated Description");
        productToUpdate.setPrice(new BigDecimal("99.99"));
        productToUpdate.setImageName("Updated Image Name");

        dao.updateProduct(productToUpdate);

        Product retrievedProduct = dao.getProductById(1);
        assertProductsMatch(productToUpdate, retrievedProduct);
    }

    @Test
    public void deleteProductById_deletes_product() {

        int rowsAffected = dao.deleteProductById(PRODUCT_1.getProductId());

        Assert.assertEquals("Product not deleted", 1, rowsAffected);
        Product retrievedProduct = dao.getProductById(PRODUCT_1.getProductId());
        Assert.assertNull("Deleted product can still be retrieved", retrievedProduct);
    }

    private void assertProductsMatch(Product expected, Product actual) {
        Assert.assertEquals(expected.getProductId(), actual.getProductId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
        Assert.assertEquals(expected.getImageName(), actual.getImageName());
    }

}
