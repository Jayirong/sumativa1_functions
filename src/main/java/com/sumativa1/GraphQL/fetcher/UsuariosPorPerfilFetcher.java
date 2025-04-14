package com.sumativa1.GraphQL.fetcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuariosPorPerfilFetcher implements DataFetcher<List<Map<String,Object>>> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl = System.getenv().getOrDefault("USUARIOS_BACKEND_URL",
                                           "http://localhost:8085/api");

    @Override
    public List<Map<String,Object>> get(DataFetchingEnvironment env) throws Exception {
        String perfil = env.getArgument("nombrePerfil");
        URI uri = URI.create(String.format("%s/usuarios?perfil=%s", baseUrl, perfil));
        HttpResponse<String> res = http.send(HttpRequest.newBuilder().uri(uri).GET().build(),
                                             HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("Backend responded " + res.statusCode());

        // Paso 1: deserializa a List<Map>
        List<Map<String,Object>> raw = mapper.readValue(res.body(),
                new TypeReference<List<Map<String,Object>>>(){});

        // Paso 2: adapta claves y aplana el objeto perfil
        List<Map<String,Object>> adaptado = new ArrayList<>();
        for (Map<String,Object> u : raw) {
            Map<String,Object> g = new HashMap<>();
            g.put("id", u.get("idUsuario"));
            g.put("nombre", u.get("nombre"));
            g.put("email", u.get("email"));

            Object perfilObj = u.get("perfil");
            if (perfilObj instanceof Map) {
                Map<?,?> p = (Map<?,?>) perfilObj;
                Object nombrePerfil = p.get("nombre");
                g.put("perfil", nombrePerfil != null ? nombrePerfil : null);
            } else {
                g.put("perfil", null);
            }
            adaptado.add(g);
        }
        return adaptado;
    }
}