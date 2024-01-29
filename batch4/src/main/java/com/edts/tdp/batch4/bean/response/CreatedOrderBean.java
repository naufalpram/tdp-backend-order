package com.edts.tdp.batch4.bean.response;

import com.edts.tdp.batch4.bean.BaseResponseBean;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class CreatedOrderBean {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String orderNumber;
    private String totalPaid;
    private String status;

}
