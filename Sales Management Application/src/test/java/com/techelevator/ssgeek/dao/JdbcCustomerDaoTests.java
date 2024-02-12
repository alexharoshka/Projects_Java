package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JdbcCustomerDaoTests extends BaseDaoTests {

    private static final Customer CUSTOMER_1 = new Customer(1, "Customer 1","Addr 1-1",
            "Addr 1-2", "City 1", "S1", "11111");
    private static final Customer CUSTOMER_2 = new Customer(2, "Customer 2", "Addr 2-1",
            "Addr 2-2", "City 2", "S2", "22222");
    private static final Customer CUSTOMER_3 = new Customer(3, "Customer 3", "Addr 3-1",
            null, "City 3", "S3", "33333");
    private static final Customer CUSTOMER_4 = new Customer(4, "Customer 4", "Addr 4-1",
            null, "City 4", "S4", "44444");

    private JdbcCustomerDao dao;

    private Customer testCustomer;

    @Before
    public void setup() {
        dao = new JdbcCustomerDao(dataSource);
        testCustomer = new Customer(0, "Test Customer", "Test StreetAddress1",
                "Test StreetAddress2", "Test City", "TS", "43210");
    }

    @Test
    public void getCustomerById_with_valid_id_returns_correct_customer() {
        Customer customer = dao.getCustomerById(1);
        assertCustomersMatch(CUSTOMER_1, customer);

        customer = dao.getCustomerById(2);
        assertCustomersMatch(CUSTOMER_2, customer);
    }

    @Test
    public void getCustomerById_with_invalid_id_returns_null() {
        Customer customer = dao.getCustomerById(99);
        Assert.assertNull(customer);
    }

    @Test
    public void getCustomers_returns_a_list_of_all_customers() {
        List<Customer> allCustomers = dao.getCustomers();
        Assert.assertEquals(4, allCustomers.size());
        assertCustomersMatch(CUSTOMER_1, allCustomers.get(0));
        assertCustomersMatch(CUSTOMER_2, allCustomers.get(1));
        assertCustomersMatch(CUSTOMER_3, allCustomers.get(2));
        assertCustomersMatch(CUSTOMER_4, allCustomers.get(3));
    }

    @Test
    public void createCustomer_creates_customer() {
        Customer createdCustomer = dao.createCustomer(testCustomer);

        int newId = createdCustomer.getCustomerId();
        Assert.assertTrue(newId > 0);

        Customer retrievedCustomer = dao.getCustomerById(newId);
        assertCustomersMatch(createdCustomer, retrievedCustomer);
    }

    @Test
    public void updateCustomer_updates_customer() {
        Customer customerToUpdate = dao.getCustomerById(1);

        customerToUpdate.setName("Updated Customer");
        customerToUpdate.setStreetAddress1("Updated StreetAddress1");
        customerToUpdate.setStreetAddress1("Updated StreetAddress2");
        customerToUpdate.setCity("Updated City");
        customerToUpdate.setState("NA");
        customerToUpdate.setZipCode("01234");

        dao.updateCustomer(customerToUpdate);

        Customer retrievedCustomer = dao.getCustomerById(1);
        assertCustomersMatch(customerToUpdate, retrievedCustomer);
    }

    private void assertCustomersMatch(Customer expected, Customer actual) {
        Assert.assertEquals(expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getStreetAddress1(), actual.getStreetAddress1());
        Assert.assertEquals(expected.getStreetAddress2(), actual.getStreetAddress2());
        Assert.assertEquals(expected.getCity(), actual.getCity());
        Assert.assertEquals(expected.getState(), actual.getState());
        Assert.assertEquals(expected.getZipCode(), actual.getZipCode());
    }

}
