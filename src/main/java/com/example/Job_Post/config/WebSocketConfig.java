package com.example.Job_Post.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final JwtHandshakeInterceptor.JwtChannelInterceptor jwtChannelInterceptor;


  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/user", "/topic", "/queue"); 
    registry.setApplicationDestinationPrefixes("/app"); 
    registry.setUserDestinationPrefix("/user"); 
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
    .addInterceptors(jwtHandshakeInterceptor)
    .setAllowedOriginPatterns("http://localhost:5173", "https://kontral-frontend-1.onrender.com") // Allow specific origin for CORS
    .withSockJS(); // Enable SockJS fallback options
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON); // Set default MIME type to JSON

    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    converter.setContentTypeResolver(resolver);

    messageConverters.add(new StringMessageConverter());
    messageConverters.add(new ByteArrayMessageConverter());
    messageConverters.add(converter);
    
    return false; // Use default message converters
  }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
  
}