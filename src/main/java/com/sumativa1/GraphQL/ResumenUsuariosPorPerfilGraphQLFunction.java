package com.sumativa1.GraphQL;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.sumativa1.GraphQL.provider.GraphQLProvider;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResumenUsuariosPorPerfilGraphQLFunction {

    @FunctionName("resumenUsuariosPorPerfilGraphQL")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS
        ) HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context
    ) {
        try {
            // Obtener el body de la petición HTTP
            String body = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();

            // Se espera que el body tenga al menos la propiedad "query"
            Map<String, Object> jsonMap = mapper.readValue(body, Map.class);
            String query = (String) jsonMap.get("query");
            Map<String, Object> variables = (Map<String, Object>) jsonMap.getOrDefault("variables", new HashMap<>());

            // Obtener la instancia de GraphQL desde el provider
            GraphQL graphQL = GraphQLProvider.getGraphQL();

            // Construir el ExecutionInput
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables)
                    .build();

            // Ejecutar la consulta GraphQL
            ExecutionResult executionResult = graphQL.execute(executionInput);

            // Preparar la respuesta
            Map<String, Object> result = new HashMap<>();
            if (!executionResult.getErrors().isEmpty()) {
                result.put("errors", executionResult.getErrors());
            }
            result.put("data", executionResult.getData());

            // Devolver la respuesta como JSON
            return request.createResponseBuilder(HttpStatus.OK)
                          .header("Content-Type", "application/json")
                          .body(mapper.writeValueAsString(result))
                          .build();
        } catch (Exception e) {
            context.getLogger().severe("Error: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                          .body("{\"error\": \"" + e.getMessage() + "\"}")
                          .build();
        }
    }
}