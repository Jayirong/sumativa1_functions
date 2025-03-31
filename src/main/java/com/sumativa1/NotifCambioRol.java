package com.sumativa1;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Función Azure que simula el envío de un correo de notificación cuando se cambia el rol de un usuario.
 * Se espera recibir en el body un JSON con campos, por ejemplo:
 * {
 *   "email": "usuario@dominio.com",
 *   "oldRole": "Usuario",
 *   "newRole": "Administrador",
 *   "userId": 123
 * }
 */

public class NotifCambioRol {
    
    @FunctionName("NotifCambioRol")
    public HttpResponseMessage run(
        @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("NotifCambioRol function processed a request.");

        //obtenemos el cuerpo de la request
        Optional<String> requestBody = request.getBody();
        if (!requestBody.isPresent() || requestBody.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                            .body("El cuerpo ta vacio. Se espera un JSON con los detalles para el cambio de rol.")
                            .build();
        }

        String body = requestBody.get();
        context.getLogger().info("Datos recibidos: " + body);

        // Simular retardo de 3 segundos para emular el envío de correo
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            context.getLogger().severe("Error durante la simulación de espera: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        //la simulacion espera que se envie correctamente el correo
        String responseMessage = "Notificacion de cambio de rol enviada, datos recibidos: " + body;

        return request.createResponseBuilder(HttpStatus.OK)
                        .body(responseMessage)
                        .build();
    }
    
}
