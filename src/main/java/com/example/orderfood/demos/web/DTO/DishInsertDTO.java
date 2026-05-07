package com.example.orderfood.demos.web.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class DishInsertDTO {
    private String dishName;
    private BigInteger categoryId;
    private BigDecimal price;
    private String ingredients;
    private String dishPhoto;
    private BigInteger dishPhotoId;
}