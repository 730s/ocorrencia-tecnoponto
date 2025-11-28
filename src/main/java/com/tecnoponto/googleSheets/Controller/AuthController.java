package com.tecnoponto.googleSheets.Controller;

import com.tecnoponto.googleSheets.Entities.Usuario;
import com.tecnoponto.googleSheets.Enums.Responsavel;
import com.tecnoponto.googleSheets.Service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(
        origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody Usuario usuario, HttpSession session) {

        if (!authService.autenticar(usuario.getNome(), usuario.getSenha())) {
            return "Usuário ou senha inválidos!";
        }

        Responsavel r = authService.getResponsavelPorUsuario(usuario.getNome());

        session.setAttribute("responsavelAutenticado", r);

        return "Login realizado com sucesso!";
    }

    @GetMapping("/usuario")
    public Responsavel getUsuario(HttpSession session) {
        return (Responsavel) session.getAttribute("responsavelAutenticado");
    }
}
