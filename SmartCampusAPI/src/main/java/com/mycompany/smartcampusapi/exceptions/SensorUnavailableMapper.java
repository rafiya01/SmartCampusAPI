//Author : Rafia Malik
//Student ID # w2069502
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author AAA
 */
package com.mycompany.smartcampusapi.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps SensorUnavailableException to an HTTP 403 Forbidden response.
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        // Create a simple error object to return as JSON
        ErrorMessage error = new ErrorMessage(exception.getMessage(), 403);

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type("application/json")
                .build();
    }

    // Inner class to format the JSON response nicely
    public static class ErrorMessage {
        private String message;
        private int code;

        public ErrorMessage(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() { return message; }
        public int getCode() { return code; }
    }
}