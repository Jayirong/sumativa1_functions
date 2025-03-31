package com.sumativa1;

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
        @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<String> request,
        final ExecutionContext context) {

        String body = request.getBody();
        if (body == null || body.trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Cuerpo vacío, se espera un JSON con los detalles para el cambio de rol.")
                        .build();
        }
        context.getLogger().info("Cuerpo recibido: " + body);
        
        // Opcional: deserializar manualmente el JSON a un objeto
        // CambioRolRequest cambio = new ObjectMapper().readValue(body, CambioRolRequest.class);

        String responseMessage = "Notificación de cambio de rol enviada, email: " + body; // o extrae el campo email después de deserializar
        return request.createResponseBuilder(HttpStatus.OK)
                    .body(responseMessage)
                    .build();
    }
}
