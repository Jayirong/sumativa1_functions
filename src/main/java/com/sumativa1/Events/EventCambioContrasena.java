package com.sumativa1.Events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.sumativa1.model.CambioContrasenaRequest;

public class EventCambioContrasena {
    @FunctionName("EventCambioContrasena")
    public void run(
        @EventGridTrigger(name = "event") String content,
        final ExecutionContext context) {

        context.getLogger().info("Evento recibido desde Event Grid (Canbio Contraseña): " + content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            CambioContrasenaRequest cambio = mapper.readValue(content, CambioContrasenaRequest.class);

            context.getLogger().info("Notificando cambio de contraseña:");
            context.getLogger().info(" - Email: " + cambio.getEmail());
            context.getLogger().info(" - User ID: " + cambio.getUserId());

            //Simula el envío de correo u otra acción
            Thread.sleep(3000);
            context.getLogger().info("Notificación de cambio de contraseña enviada correctamente.");

        } catch (Exception e) {
            context.getLogger().severe("Error al procesar evento: " + e.getMessage());
        }
    }
}
