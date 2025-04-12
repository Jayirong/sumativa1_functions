package com.sumativa1.GraphQL;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class UsuariosPorPerfilFetcher implements DataFetcher<List<Map<String, Object>>> {

    private static final String USUARIO_BACKEND_URL = "http://localhost:8081/api/usuarios/porPerfil?nombrePerfil=";

    @Override
    public List<Map<String, Object>> get(DataFetchingEnvironment environment) throws Exception {
        String perfil = environment.getArgument("nombrePerfil");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(USUARIO_BACKEND_URL + perfil))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
    }
}
