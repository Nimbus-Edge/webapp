package com.cloud.webapp.utils;

import com.cloud.webapp.DTO.UserRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class helpers {
    public static String getAuthenticatedUserEmailHelper() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public static String[] getNullPropertyNamesHelper(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(obj) == null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Field::getName)
                .toArray(String[]::new);
    }

    public static boolean isRequestBodyEmptyHelper(Object obj) {
        if (obj == null) {
            return true;
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(obj) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String getCurrentTimeUtil(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public static LocalDateTime convertTimeToLocalDateTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(time, formatter);
    }

    private static Set<String> initializeAllowedFields() {
        Set<String> fields = new HashSet<>();
        for (Field field : UserRequestDTO.class.getDeclaredFields()) {
            fields.add(field.getName());
        }
        return fields;
    }

    public static boolean hasUnexpectedFieldsUtil(UserRequestDTO userRequestDTO) {
        Set<String> requestFields = new HashSet<>();
         Set<String> allowedFields=initializeAllowedFields();
        if (userRequestDTO != null) {
            for (Field field : UserRequestDTO.class.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(userRequestDTO);
                    if (value != null) {
                        requestFields.add(field.getName());
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }

        // Check if there are any unexpected fields
        return !allowedFields.containsAll(requestFields);
    }

    public static String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}


