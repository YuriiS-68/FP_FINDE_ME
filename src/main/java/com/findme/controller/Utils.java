package com.findme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utils<T> {
    private static final DateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private Class<T> type;

    @SuppressWarnings("unchecked")
    public Utils() {
        this.type = ((Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    T mappingObject(HttpServletRequest req)throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setDateFormat(FORMAT);
        String input = objectMapper.writeValueAsString(getString(req));
        return objectMapper.convertValue(input, type);
    }

    private static String getString(HttpServletRequest req)throws IOException{
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = req.getReader()){
            String line;

            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
