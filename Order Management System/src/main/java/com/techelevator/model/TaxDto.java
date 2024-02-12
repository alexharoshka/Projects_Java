package com.techelevator.model;

//   Response: {"salesTax":5.75,"lastUpdated":"Fri Jan 01 2021"}

import java.math.BigDecimal;

public class TaxDto {

    BigDecimal salesTax;
    String lastUpdated;

    public BigDecimal getSalesTax() {
        return salesTax;
    }
    public void setSalesTax(BigDecimal salesTax) {
        this.salesTax = salesTax;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public TaxDto() {}

    public TaxDto(BigDecimal salesTax, String lastUpdated) {
        this.salesTax = salesTax;
        this.lastUpdated = lastUpdated;
    }
}
