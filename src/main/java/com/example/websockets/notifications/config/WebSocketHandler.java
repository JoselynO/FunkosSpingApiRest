package com.example.websockets.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();


    public WebSocketHandler(String entity){
        this.entity = entity;
    }


    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        log.info("Conexion establecida con el servidor");
        log.info("Sesion: " + session);
        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket: " + entity + "- Tienda API Spring Boot");
        log.info("Servidor envia: {}", message);
        session.sendMessage(message);
    }

    public void sendMessage(String message) throws IOException{
        log.info("Enviar mensaje de cambios en la entidad: " + entity + ":" + message);
        for(WebSocketSession session : sessions){
            if(session.isOpen()){
                log.info("Servidor WS envia: " + message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void sendPeriodicMessages() throws IOException{
        for (WebSocketSession session : sessions){
            if(session.isOpen()){
                String broadcast = "server periodic message "+ LocalTime.now();
                log.info("Server sends: " + broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }


    public List<String> getSubProtocols() {
        return null;
    }
}
