package com.example.contatos.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/** Converte violações do Bean Validation (body, path e query params) em HTTP 400. */
@Provider
public class ValidacaoMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            fieldErrors.putIfAbsent(caminhoDoCampo(violation), violation.getMessage());
        }
        return ApiErrors.response(400, "dados inválidos", caminho(), fieldErrors);
    }

    private String caminhoDoCampo(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int indice = path.lastIndexOf('.');
        return indice >= 0 ? path.substring(indice + 1) : path;
    }

    private String caminho() {
        return uriInfo == null ? "" : uriInfo.getPath();
    }
}
