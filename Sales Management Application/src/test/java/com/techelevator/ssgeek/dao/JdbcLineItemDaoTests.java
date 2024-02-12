package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDaoTests extends BaseDaoTests {

    private static final LineItem LINE_ITEM_1 = new LineItem(1, 1,
            1, 1, "Product 1", new BigDecimal("9.99"));
    private static final LineItem LINE_ITEM_2 = new LineItem(2, 1,
            2, 1, "Product 2", new BigDecimal("19.00"));
    private static final LineItem LINE_ITEM_3 = new LineItem(3, 1,
            4, 1, "Product 4", new BigDecimal("0.99"));
    private static final LineItem LINE_ITEM_4 = new LineItem(4, 2,
            4, 10, "Product 4", new BigDecimal("0.99"));
    private static final LineItem LINE_ITEM_5 = new LineItem(5, 2,
            1, 10, "Product 1", new BigDecimal("9.99"));
    private static final LineItem LINE_ITEM_6 = new LineItem(6, 3,
            1, 100, "Product 1", new BigDecimal("9.99"));

    private JdbcLineItemDao dao;

    @Before
    public void setup() {
        dao = new JdbcLineItemDao(dataSource);
    }

    @Test
    public void getLineItemsBySaleId_with_valid_id_returns_a_list_of_correct_lineItems() {
        List<LineItem> lineItems = dao.getLineItemsBySaleId(1);
        Assert.assertEquals(3, lineItems.size());
        assertLineItemsMatch(LINE_ITEM_1, lineItems.get(0));
        assertLineItemsMatch(LINE_ITEM_2, lineItems.get(1));
        assertLineItemsMatch(LINE_ITEM_3, lineItems.get(2));

        lineItems = dao.getLineItemsBySaleId(3);
        Assert.assertEquals(1, lineItems.size());
        assertLineItemsMatch(LINE_ITEM_6, lineItems.get(0));
    }

    @Test
    public void getLineItemsBySaleId_with_invalid_id_returns_null() {
        List<LineItem> lineItems = dao.getLineItemsBySaleId(99);
        Assert.assertEquals(0, lineItems.size());
    }

    private void assertLineItemsMatch(LineItem expected, LineItem actual) {
        Assert.assertEquals(expected.getLineItemId(), actual.getLineItemId());
        Assert.assertEquals(expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(expected.getProductId(), actual.getProductId());
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(expected.getProductName(), actual.getProductName());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
    }

}
