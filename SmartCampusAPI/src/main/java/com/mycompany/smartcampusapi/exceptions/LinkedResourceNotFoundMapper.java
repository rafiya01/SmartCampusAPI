//Author:Rafia Malik
//Student ID# w2069502
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
 * 
 * @author AAA
 */
@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // 422 Unprocessable Entity is used when the server understands the content type,
        // but the data is logically invalid (e.g., a Room ID that doesn't exist).
        
        ErrorDetails error = new ErrorDetails(exception.getMessage(), 422);

        return Response.status(422) // Using integer 422 for Unprocessable Entity
                .entity(error)
                .type("application/json")
                .build();
    }

    // Standard POJO for a clean JSON error response
    public static class ErrorDetails {
        private String errorMessage;
        private int errorCode;

        public ErrorDetails(String errorMessage, int errorCode) {
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }

        public String getErrorMessage() { return errorMessage; }
        public int getErrorCode() { return errorCode; }
    }
}