package com.example.contatos.error;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EmailDuplicadoMapper implements ExceptionMapper<EmailDuplicadoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(EmailDuplicadoException exception) {
        return ApiErrors.response(409, exception.getMessage(), caminho());
    }

    private String caminho() {
        return uriInfo == null ? "" : uriInfo.getPath();
    }
}
