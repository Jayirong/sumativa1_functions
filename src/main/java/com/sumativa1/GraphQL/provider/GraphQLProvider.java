package com.sumativa1.GraphQL.provider;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;

import com.sumativa1.GraphQL.fetcher.UsuariosPorPerfilFetcher;

public class GraphQLProvider {

    private static GraphQL graphQL;

    public static GraphQL getGraphQL() {
        if (graphQL == null) {
            graphQL = buildGraphQL();
        }
        return graphQL;
    }

    private static GraphQL buildGraphQL() {
        InputStream schemaStream = GraphQLProvider.class.getClassLoader().getResourceAsStream("graphql/schema.graphqls");

        if (schemaStream == null) {
            throw new RuntimeException("No se encontró el archivo schema.graphqls");
        }

        Reader reader = new InputStreamReader(schemaStream);
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("usuariosPorPerfil", new UsuariosPorPerfilFetcher()))
                .build();

        GraphQLSchema schema = new SchemaGenerator()
                .makeExecutableSchema(typeRegistry, wiring);

        return GraphQL.newGraphQL(schema).build();
    }
}

