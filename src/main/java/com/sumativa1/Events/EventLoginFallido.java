package com.sumativa1.Events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.sumativa1.model.LoginFallidoEvent;

public class EventLoginFallido {
    @FunctionName("EventLoginFallido")
    public void run(
        @EventGridTrigger(name = "event") String content,
        final ExecutionContext context) {

        context.getLogger().info("Evento recibido desde Event Grid (Login Fallido): " + content);

        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginFallidoEvent evento = mapper.readValue(content, LoginFallidoEvent.class);

            context.getLogger().warning("Usuario con múltiples intentos fallidos:");
            context.getLogger().warning(" - Email: " + evento.getEmailUsuario());
            context.getLogger().warning(" - Intentos: " + evento.getCantidadIntentos());
            context.getLogger().warning(" - Fecha: " + evento.getFecha());

            context.getLogger().info("Bloqueando temporalmente al usuario...");
            Thread.sleep(3000);

            context.getLogger().info("Se ha registrado el incidente para revisión del equipo de seguridad.");

        } catch (Exception e) {
            context.getLogger().severe("Error al procesar evento de login fallido: " + e.getMessage());
        }
    }
}
