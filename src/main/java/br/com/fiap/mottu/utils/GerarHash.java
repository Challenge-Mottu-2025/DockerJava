package br.com.fiap.mottu.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHash {
    public static void main(String[] args) {
        // Pode aceitar senha via argumento: java GerarHash MinhaSenha123
        String senha = (args.length > 0) ? args[0] : "NovaSenha123";
        System.out.println(new BCryptPasswordEncoder().encode(senha));
    }
}