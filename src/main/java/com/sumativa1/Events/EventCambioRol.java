package com.sumativa1.Events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.sumativa1.model.CambioRolRequest;

public class EventCambioRol {
    @FunctionName("EventCambioRol")
    public void run(
        @EventGridTrigger(name = "event") String content,
        final ExecutionContext context) {

        context.getLogger().info("Evento recibido desde Event Grid (Cambio de Rol): " + content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            CambioRolRequest cambio = mapper.readValue(content, CambioRolRequest.class);

            context.getLogger().info("Notificando cambio de rol:");
            context.getLogger().info(" - Email: " + cambio.getEmail());
            context.getLogger().info(" - ID Usuario: " + cambio.getUserId());
            context.getLogger().info(" - Rol Anterior: " + cambio.getOldRole());
            context.getLogger().info(" - Rol Nuevo: " + cambio.getNewRole());

            //Simulación de lógica (enviar correo o registrar en sistema externo)
            Thread.sleep(3000);
            context.getLogger().info("Notificación de cambio de rol enviada correctamente.");

        } catch (Exception e) {
            context.getLogger().severe("Error al procesar evento: " + e.getMessage());
        }
    } 
}
