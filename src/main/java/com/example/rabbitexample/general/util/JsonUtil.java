package com.example.rabbitexample.general.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JsonUtil {
    private JsonUtil() {
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            log.warn("Exception during converting object to JSON string", e);
            return "";
        }
    }
}
