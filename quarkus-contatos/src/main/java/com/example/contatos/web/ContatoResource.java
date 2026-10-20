package com.example.contatos.web;

import com.example.contatos.dto.ContatoRequest;
import com.example.contatos.dto.ContatoResponse;
import com.example.contatos.dto.PaginaResponse;
import com.example.contatos.service.ContatoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/api/contatos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ContatoResource {

    @Inject ContatoService service;

    @GET
    public PaginaResponse<ContatoResponse> listar(
            @QueryParam("q") String q,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return service.listar(q, page, size);
    }

    @GET
    @Path("/{id}")
    public ContatoResponse buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid ContatoRequest request) {
        ContatoResponse criado = service.criar(request);
        return Response.created(URI.create("/api/contatos/" + criado.id())).entity(criado).build();
    }

    @PUT
    @Path("/{id}")
    public ContatoResponse atualizar(@PathParam("id") Long id, @Valid ContatoRequest request) {
        return service.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }
}
