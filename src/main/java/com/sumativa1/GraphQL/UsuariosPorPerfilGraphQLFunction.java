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
import java.util.logging.Level;

public class UsuariosPorPerfilGraphQLFunction {

    @FunctionName("usuariosPorPerfil")
    public HttpResponseMessage run(
        @HttpTrigger(name="req", methods={HttpMethod.POST}, authLevel=AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext ctx) {

        try {
            String body = request.getBody().orElseThrow();
            Map<String,Object> payload = new ObjectMapper().readValue(body, Map.class);

            ExecutionInput input = ExecutionInput.newExecutionInput()
                .query((String)payload.get("query"))
                .variables((Map)payload.getOrDefault("variables", Map.of()))
                .build();

            Map<String,Object> result = GraphQLProvider.getGraphQL().execute(input).toSpecification();
            return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type","application/json")
                        .body(new ObjectMapper().writeValueAsString(result))
                        .build();

        } catch(Exception ex) {
            ctx.getLogger().log(Level.SEVERE, "GraphQL error", ex);
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\""+ex.getMessage()+"\"}").build();
        }
    }
}
