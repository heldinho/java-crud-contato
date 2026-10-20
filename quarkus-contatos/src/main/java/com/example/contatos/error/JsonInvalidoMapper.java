package com.example.contatos.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** JSON malformado no corpo da requisição vira HTTP 400 padronizado. */
@Provider
public class JsonInvalidoMapper implements ExceptionMapper<JsonProcessingException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(JsonProcessingException exception) {
        return ApiErrors.response(400, "corpo da requisição não é um JSON válido", caminho());
    }

    private String caminho() {
        return uriInfo == null ? "" : uriInfo.getPath();
    }
}
