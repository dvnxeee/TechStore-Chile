package cl.techstore.api.controller;

import cl.techstore.api.dto.LoginRequest;
import cl.techstore.api.dto.LoginResponse;
import cl.techstore.api.security.JwtUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Value("${app.security.username}")
    private String usernameConfigurado;

    @Value("${app.security.password}")
    private String passwordConfigurado;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        boolean credencialesValidas = usernameConfigurado.equals(request.getUsername())
                && passwordConfigurado.equals(request.getPassword());

        if (!credencialesValidas) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(request.getUsername());

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                String.valueOf(jwtUtil.getExpirationMs() / 1000));

        return ResponseEntity.ok(response);
    }
}