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

public class ReasignarPerfilUsuario {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    // Ajusta este valor si tu perfil por defecto cambia
    private static final int DEFAULT_PROFILE_ID = 42;

    @FunctionName("ReasignarPerfilUsuario")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "reasignar-perfil"
        ) HttpRequestMessage<String> request,
        final ExecutionContext context
    ) {
        context.getLogger().info("ReasignarPerfilUsuario → payload recibido");

        try {
            // 1) Parsear JSON de entrada (EliminaPerfilDto: { "idPerfil": X })
            String body = request.getBody();
            if (body == null || body.isBlank()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                              .body("Se esperaba un JSON con { \"idPerfil\": <antiguoId> }")
                              .build();
            }
            JsonNode input = MAPPER.readTree(body);
            int oldId = input.path("idPerfil").asInt(-1);
            if (oldId < 0) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                              .body("idPerfil inválido")
                              .build();
            }

            // 2) Construir el DTO para el backend (ReasignarPerfilDto)
            ObjectNode payload = MAPPER.createObjectNode();
            payload.put("idPerfilOld", oldId);
            payload.put("idPerfilNew", DEFAULT_PROFILE_ID);

            // 3) Llamar al microservicio usuarios-backend
            String backendUrl = System.getenv().getOrDefault("BACKEND_URL", "http://34.202.215.164:8085");
            String target = String.format("%s/api/perfiles/reasignar-perfil", backendUrl);

            HttpRequest httpReq = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(payload)))
                .build();

            HttpResponse<String> resp = HTTP.send(httpReq, HttpResponse.BodyHandlers.ofString());
            context.getLogger().info("POST " + target + " → status " + resp.statusCode());

            // 4) Devolver al llamador el mismo body que retorne el backend
            return request.createResponseBuilder(HttpStatus.valueOf(resp.statusCode()))
                          .header("Content-Type", "application/json")
                          .body(resp.body())
                          .build();

        } catch (Exception e) {
            context.getLogger().severe("Error en ReasignarPerfilUsuario: " + e.getMessage());
            // Respuesta en caso de error interno
            ObjectNode err = MAPPER.createObjectNode()
                                   .put("status", false)
                                   .put("message", "Error en función: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .header("Content-Type", "application/json")
                          .body(err.toString())
                          .build();
        }
    }
}
