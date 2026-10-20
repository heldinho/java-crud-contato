package com.example.contatos.error;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.UriInfo;

@Provider
public class ContatoNaoEncontradoMapper implements ExceptionMapper<ContatoNaoEncontradoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(ContatoNaoEncontradoException exception) {
        return ApiErrors.response(404, exception.getMessage(), caminho());
    }

    private String caminho() {
        return uriInfo == null ? "" : uriInfo.getPath();
    }
}
