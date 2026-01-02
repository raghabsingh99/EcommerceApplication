package com.dailycodebuffer.ProductService.exception;

import com.dailycodebuffer.ProductService.ProductServiceApplication;
import com.dailycodebuffer.ProductService.model.ProductRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductServiceCustomException extends RuntimeException{
    private String errorCode;

    public ProductServiceCustomException(String message,String errorCode){
        super(message);
        this.errorCode=errorCode;
    }
}
