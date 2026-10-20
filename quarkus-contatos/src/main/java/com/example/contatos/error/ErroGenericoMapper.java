package com.example.contatos.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/** Rede de segurança: qualquer RuntimeException não tratada vira HTTP 500 padronizado. */
@Provider
public class ErroGenericoMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger log = Logger.getLogger(ErroGenericoMapper.class);

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(RuntimeException exception) {
        log.errorf(exception, "erro inesperado em %s", caminho());

        if (exception instanceof WebApplicationException webException) {
            int status = webException.getResponse().getStatus();
            return ApiErrors.response(status, mensagem(webException, status), caminho());
        }
        return ApiErrors.response(500, "erro interno inesperado", caminho());
    }

    private String mensagem(WebApplicationException exception, int status) {
        if (status >= 500) {
            return "erro interno inesperado";
        }
        Response.Status resolved = Response.Status.fromStatusCode(status);
        return exception.getMessage() != null
                ? exception.getMessage()
                : (resolved == null ? "requisição inválida" : resolved.getReasonPhrase());
    }

    private String caminho() {
        return uriInfo == null ? "" : uriInfo.getPath();
    }
}
