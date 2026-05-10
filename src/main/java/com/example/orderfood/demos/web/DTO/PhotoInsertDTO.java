package com.example.orderfood.demos.web.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * 图片上传DTO
 * 用于接收图片上传请求的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoInsertDTO {
    
    private String type;

    private BigInteger commentId;

    private BigInteger shopId;

    private BigInteger dishId;

    private BigInteger userId;
}
