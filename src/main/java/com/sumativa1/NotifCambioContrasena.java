package com.sumativa1;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.sumativa1.model.CambioContrasenaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Función Azure que simula el envío de un correo de notificación cuando se cambia la contraseña de un usuario.
 * Se espera recibir en el body un JSON con campos, por ejemplo:
 * {
 *   "email": "usuario@dominio.com",
 *   "userId": 123
 * }
 */

public class NotifCambioContrasena {
    
    @FunctionName("NotifCambioContrasena")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<String> request,
        final ExecutionContext context) {

        // Obtener el cuerpo como String
        String body = request.getBody();
        if (body == null || body.trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                          .body("Cuerpo vacío, se espera un JSON con los detalles para el cambio de contraseña.")
                          .build();
        }
        
        context.getLogger().info("Cuerpo recibido para cambio de contraseña: " + body);
        
        // (Opcional) Deserializar manualmente el JSON a un objeto
        try {
            ObjectMapper mapper = new ObjectMapper();
            CambioContrasenaRequest cambio = mapper.readValue(body, CambioContrasenaRequest.class);
            context.getLogger().info("Deserializado: email=" + cambio.getEmail() + ", userId=" + cambio.getUserId());
        } catch(Exception e) {
            context.getLogger().severe("Error de deserialización: " + e.getMessage());
        }
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            context.getLogger().severe("Error en el retardo: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        
        String responseMessage = "Notificación de cambio de contraseña enviada, body: " + body;
        return request.createResponseBuilder(HttpStatus.OK)
                      .body(responseMessage)
                      .build();
    }
}
