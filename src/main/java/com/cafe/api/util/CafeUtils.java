package com.cafe.api.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;


public class CafeUtils {

    private CafeUtils() {
        // Prevent object creation
    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<>("{\"message\":\"" + responseMessage + "\"}", httpStatus);

    }

    public static String getUID() {
        Date date= new Date();
        long time= date.getTime();
        return "BILL-"+ time;
    }
}