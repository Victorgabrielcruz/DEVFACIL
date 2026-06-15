package com.devfacil.api.service;

import com.devfacil.api.exception.RecursoNaoEncontradoException;
import com.devfacil.api.model.Usuario;
import com.devfacil.api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TokenService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final String secret;

    public TokenService(
            UsuarioRepository usuarioRepository,
            ObjectMapper objectMapper,
            @Value("${devfacil.auth.secret:devfacil-local-secret-change-me}") String secret
    ) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
        this.secret = secret;
    }

    public String gerar(Usuario usuario) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", usuario.getId());
            payload.put("email", usuario.getEmail());
            payload.put("perfil", usuario.getPerfil().name());
            payload.put("exp", Instant.now().plusSeconds(60 * 60 * 12).getEpochSecond());

            String body = base64Url(objectMapper.writeValueAsBytes(payload));
            String signature = assinar(body);
            return body + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("erro ao gerar token", exception);
        }
    }

    public Usuario validar(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2 || !assinar(parts[0]).equals(parts[1])) {
                throw new IllegalArgumentException("token invalido");
            }

            Map<?, ?> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[0]), Map.class);
            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("token expirado");
            }

            Number sub = (Number) payload.get("sub");
            return usuarioRepository.findById(sub.longValue())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("usuario nao encontrado"));
        } catch (Exception exception) {
            throw new IllegalArgumentException("token invalido");
        }
    }

    private String assinar(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("erro ao assinar token", exception);
        }
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
