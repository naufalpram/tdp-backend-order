package com.edts.tdp.batch4.bean.catalog;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private long id;
    private int quantity;
}
