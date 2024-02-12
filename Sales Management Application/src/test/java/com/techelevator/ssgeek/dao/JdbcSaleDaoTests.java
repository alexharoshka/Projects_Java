package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class JdbcSaleDaoTests extends BaseDaoTests {

    private static final Sale SALE_1 = new Sale(1, 1, LocalDate.parse("2022-01-01"),
            null, "Customer 1");
    private static final Sale SALE_2 = new Sale(2, 1, LocalDate.parse("2022-02-01"),
            LocalDate.parse("2022-02-02"), "Customer 1");
    private static final Sale SALE_3 = new Sale(3, 2, LocalDate.parse("2022-03-01"),
            null, "Customer 2");
    private static final Sale SALE_4 = new Sale(4, 2, LocalDate.parse("2022-01-01"),
            LocalDate.parse("2022-01-02"), "Customer 2");

    private JdbcSaleDao dao;

    private Sale testSale;

    @Before
    public void setup() {
        dao = new JdbcSaleDao(dataSource);
        testSale = new Sale(0, 1, LocalDate.parse("2020-01-01"),
                LocalDate.parse("2021-01-01"), "Test Customer Name");
    }

    @Test
    public void getSaleById_with_valid_id_returns_correct_sale() {
        Sale sale = dao.getSaleById(1);
        assertSalesMatch(SALE_1, sale);

        sale = dao.getSaleById(4);
        assertSalesMatch(SALE_4, sale);
    }

    @Test
    public void getSaleById_with_invalid_id_returns_null() {
        Sale sale = dao.getSaleById(99);
        Assert.assertNull(sale);
    }

    @Test
    public void getUnshippedSales_returns_list_of_sales_where_shipDate_is_null() {
        List<Sale> unshippedSales = dao.getUnshippedSales();
        Assert.assertEquals(2, unshippedSales.size());
        assertSalesMatch(SALE_1, unshippedSales.get(0));
        assertSalesMatch(SALE_3, unshippedSales.get(1));
    }

    @Test
    public void getSalesByCustomerId_returns_list_of_sales_for_particular_customer() {
        List<Sale> salesByCustomerId = dao.getSalesByCustomerId(1);
        Assert.assertEquals(2, salesByCustomerId.size());
        assertSalesMatch(SALE_1, salesByCustomerId.get(0));
        assertSalesMatch(SALE_2, salesByCustomerId.get(1));

        salesByCustomerId = dao.getSalesByCustomerId(2);
        Assert.assertEquals(2, salesByCustomerId.size());
        assertSalesMatch(SALE_3, salesByCustomerId.get(0));
        assertSalesMatch(SALE_4, salesByCustomerId.get(1));
    }

    @Test
    public void getSalesByProductId_returns_list_of_sales_of_particular_product() {
        List<Sale> salesByProductId = dao.getSalesByProductId(1);
        Assert.assertEquals(3, salesByProductId.size());
        assertSalesMatch(SALE_1, salesByProductId.get(0));
        assertSalesMatch(SALE_2, salesByProductId.get(1));
        assertSalesMatch(SALE_3, salesByProductId.get(2));

        salesByProductId = dao.getSalesByProductId(4);
        Assert.assertEquals(2, salesByProductId.size());
        assertSalesMatch(SALE_1, salesByProductId.get(0));
        assertSalesMatch(SALE_2, salesByProductId.get(1));
    }

    @Test
    public void createSale_creates_sale() {
        Sale createdSale = dao.createSale(testSale);

        int newId = createdSale.getSaleId();
        Assert.assertTrue(newId > 0);

        Sale retrievedSale = dao.getSaleById(newId);
        assertSalesMatch(createdSale, retrievedSale);
    }

    @Test
    public void updateSale_updates_Sale() {
        Sale saleToUpdate = dao.getSaleById(4);

        saleToUpdate.setSaleDate(LocalDate.parse("2024-01-01"));
        saleToUpdate.setShipDate(LocalDate.parse("2024-02-01"));
        saleToUpdate.setCustomerName("Updated Customer Name");

        dao.updateSale(saleToUpdate);

        Sale retrievedSale = dao.getSaleById(4);
        assertSalesMatch(saleToUpdate, retrievedSale);
    }

    @Test
    public void deleteSaleById_deletes_sale() {

        int rowsAffected = dao.deleteSaleById(SALE_1.getSaleId());

        Assert.assertEquals("Sale not deleted", 1, rowsAffected);
        Sale retrievedSale = dao.getSaleById(SALE_1.getSaleId());
        Assert.assertNull("Deleted sale can still be retrieved", retrievedSale);
    }

    private void assertSalesMatch(Sale expected, Sale actual) {
        Assert.assertEquals(expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(expected.getSaleDate(), actual.getSaleDate());
        Assert.assertEquals(expected.getShipDate(), actual.getShipDate());
        Assert.assertEquals(expected.getCustomerName(), actual.getCustomerName());
    }

}
