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

    // @FunctionName("usuariosPorPerfilGraphQL")
    // public HttpResponseMessage run(
    //     @HttpTrigger(
    //         name = "req",
    //         methods = {HttpMethod.POST},
    //         authLevel = AuthorizationLevel.ANONYMOUS
    //     ) HttpRequestMessage<Optional<String>> request,
    //     final ExecutionContext context
    // ) {
    //     try {
    //         //Obtener body de la petición HTTP
    //         String body = request.getBody().orElse("");
    //         ObjectMapper mapper = new ObjectMapper();
    
    //         //Mapear body. Debe contener variable "query" con la consulta GraphQL
    //         Map<String, Object> jsonMap = mapper.readValue(body, Map.class);
    
    //         //Extraer el string con la consulta GraphQL
    //         String query = (String) jsonMap.get("query");
    
    //         //Obtener las variables. Si no existen usar un map vacío
    //         Map<String, Object> variables = (Map<String, Object>) jsonMap.getOrDefault("variables", new HashMap<>());
    
    //         //Obtener la instancia de GraphQL
    //         GraphQL graphQL = GraphQLProvider.getGraphQL();
    
    //         //Construir la consulta
    //         ExecutionInput executionInput = ExecutionInput.newExecutionInput()
    //                 .query(query)
    //                 .variables(variables)
    //                 .build();
    
    //         //Ejecutar la consulta contra el esquema GraphQL
    //         ExecutionResult executionResult = graphQL.execute(executionInput);
    
    //         //Respuesta de la consulta
    //         Map<String, Object> result = new HashMap<>();
    //         if (!executionResult.getErrors().isEmpty()) {
    //             result.put("errors", executionResult.getErrors());
    //         }
    //         result.put("data", executionResult.getData());
    
    //         return request.createResponseBuilder(HttpStatus.OK)
    //                         .header("Content-Type", "application/json")
    //                         .body(mapper.writeValueAsString(result))
    //                         .build();
    
    //     } catch (Exception e) {
    //         context.getLogger().severe("Error al ejecutar la consulta GraphQL: " + e.getMessage());
    //         return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
    //                     .body("{\"error\": \"" + e.getMessage() + "\"}")
    //                     .build();
    //     }
    // } 
    // ------------- Voy a comenzar de nuevo ------------------ //
    
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
