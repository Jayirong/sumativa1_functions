package com.sumativa1.GraphQL.provider;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.sumativa1.GraphQL.fetcher.ResumenUsuariosPorPerfilFetcher;
import com.sumativa1.GraphQL.fetcher.UsuariosPorPerfilFetcher;

public class GraphQLProvider {

    private static GraphQL graphQL;

    public static synchronized GraphQL getGraphQL() {
        if (graphQL == null) {
            graphQL = buildGraphQL();
        }
        return graphQL;
    }

    private static GraphQL buildGraphQL() {
        InputStream is = GraphQLProvider.class.getResourceAsStream("/schema.graphqls");
        if (is == null) throw new RuntimeException("schema.graphqls not found");
        TypeDefinitionRegistry registry = new SchemaParser().parse(new InputStreamReader(is));
    
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
            .type("Query", t -> t
                .dataFetcher("usuariosPorPerfil", new UsuariosPorPerfilFetcher())
                .dataFetcher("resumenUsuariosPorPerfil", new ResumenUsuariosPorPerfilFetcher()))
            .build();
    
        return GraphQL.newGraphQL(new SchemaGenerator().makeExecutableSchema(registry, wiring)).build();
    }
}

