package com.edts.tdp.batch4.bean;

import java.util.List;

public class RequestOrderBean {
    private List<RequestProductBean> listOfProductData;

    public List<RequestProductBean> getListOfProductData() {
        return listOfProductData;
    }

    public void setListOfProductData(List<RequestProductBean> listOfProductData) {
        this.listOfProductData = listOfProductData;
    }
}
