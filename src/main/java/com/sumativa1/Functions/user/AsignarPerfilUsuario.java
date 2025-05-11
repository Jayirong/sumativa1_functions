package com.sumativa1.Functions.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AsignarPerfilUsuario {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @FunctionName("AsignarPerfilUsuario")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "asignar-perfil"
        ) HttpRequestMessage<String> request,
        final ExecutionContext context
    ) {
        context.getLogger().info("AsignarPerfilUsuario → payload recibido");

        try {
            // 1) Parsear body a JsonNode
            JsonNode user = MAPPER.readTree(request.getBody());

            // 2) Forzar perfil por defecto { idPerfil:41, nombre:"Usuario" }
            ObjectNode perfil = (ObjectNode) user.get("perfil");
            perfil.put("idPerfil", 41);
            perfil.put("nombre", "Usuario");

            // 3) Construir y enviar PUT al backend
            int idUsuario = user.get("idUsuario").asInt();

            // Url base de tu backend en la VM
            String backendUrl = System.getenv().getOrDefault(
                "BACKEND_URL",
                "http://34.202.215.164:8085"
            );
            String target = String.format("%s/api/usuarios/%d", backendUrl, idUsuario);
            String payload = MAPPER.writeValueAsString(user);

            HttpRequest put = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .build();

            HttpResponse<String> resp = HTTP.send(put, HttpResponse.BodyHandlers.ofString());
            context.getLogger().info("PUT " + target + " → status " + resp.statusCode());

            return request.createResponseBuilder(HttpStatus.OK)
                          .body("Perfil asignado correctamente")
                          .build();

        } catch (Exception e) {
            context.getLogger().severe("Error en AsignarPerfilUsuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("Error asignando perfil")
                          .build();
        }
    }
}
