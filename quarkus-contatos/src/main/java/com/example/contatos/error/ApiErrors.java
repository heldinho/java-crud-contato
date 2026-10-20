package com.example.contatos.error;

import com.example.contatos.dto.ApiError;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;

/** Constrói o payload de erro padronizado da API. */
public final class ApiErrors {

    private ApiErrors() {
    }

    public static Response response(int status, String message, String path) {
        return response(status, message, path, Map.of());
    }

    public static Response response(int status, String message, String path, Map<String, String> fieldErrors) {
        ApiError body =
                new ApiError(
                        Instant.now(), status, reasonPhrase(status), message, path, fieldErrors);
        return Response.status(status).entity(body).build();
    }

    private static String reasonPhrase(int status) {
        Response.Status resolved = Response.Status.fromStatusCode(status);
        return resolved == null ? "Erro" : resolved.getReasonPhrase();
    }
}
