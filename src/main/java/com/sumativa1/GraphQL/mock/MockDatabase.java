package com.sumativa1.GraphQL.mock;

import java.util.List;
import java.util.Map;

public class MockDatabase {

    public static List<Map<String, Object>> getUsuarios() {
        return List.of(
            Map.of("id", 1, "nombre", "Ada Lovelace", "email", "ada@correo.cl", "perfil", "ADMINISTRADOR"),
            Map.of("id", 2, "nombre", "Alan Turing", "email", "alan@correo.cl", "perfil", "USUARIO"),
            Map.of("id", 3, "nombre", "Grace Hopper", "email", "grace@correo.cl", "perfil", "ADMINISTRADOR")
        );
    }
}
