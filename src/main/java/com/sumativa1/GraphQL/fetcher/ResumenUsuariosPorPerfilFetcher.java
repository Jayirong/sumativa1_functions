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
import java.util.stream.Collectors;

public class ResumenUsuariosPorPerfilFetcher implements DataFetcher<List<Map<String, Object>>>{
    
    //endpoint que retorna la lista de usuarios, hay que cambiarlo para apuntar bien
    private static final String USUARIOS_BACKEND_URL = "http://98.84.66.114:8085/api/usuarios";

    @Override
    public List<Map<String, Object>> get(DataFetchingEnvironment environment) throws Exception {
        //1 llamar al backend para obtener los users
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(USUARIOS_BACKEND_URL))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //2 deserializar la respuesta a una lista de mapas
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> usuarios = mapper.readValue(
            response.body(),
            new TypeReference<List<Map<String, Object>>>() {}
        );

        //3 agrupar los usuarios por perfil y contar la cantidad de usuario en cada grupo
        Map<String, Long> resumen = usuarios.stream()
        .collect(Collectors.groupingBy(
            usuario -> {
                Object perfilObj = usuario.get("perfil");
                if (perfilObj instanceof Map) {
                    // Extraemos el campo "nombre" del objeto perfil
                    return (String) ((Map<?, ?>) perfilObj).get("nombre");
                }
                return (String) perfilObj;
            },
            Collectors.counting()
        ));

        //4 convertir el mapa de resumen a una lista de mapas con las claves 'perfil' y 'totalUsuarios'
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entry : resumen.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("perfil", entry.getKey());
            item.put("totalUsuarios", entry.getValue().intValue());
            resultado.add(item);
        }
        return resultado;

    }

}
