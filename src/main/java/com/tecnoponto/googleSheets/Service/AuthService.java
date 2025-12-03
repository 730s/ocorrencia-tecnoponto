package com.tecnoponto.googleSheets.Service;

import com.tecnoponto.googleSheets.Enums.Responsavel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private static final String SENHA_PADRAO = "pass";

    private static final Map<String, Responsavel> USUARIO_MAP = new HashMap<>();

    static {
        USUARIO_MAP.put("leonardo.sade", Responsavel.LEONARDO_S);
        USUARIO_MAP.put("alysson.santos", Responsavel.ALYSSON);
        USUARIO_MAP.put("felipe.3317", Responsavel.FELIPE);
        USUARIO_MAP.put("gabriel.alves", Responsavel.GABRIEL_GUILHERME);
        USUARIO_MAP.put("gabriel.rosa", Responsavel.GABRIEL_R);
        USUARIO_MAP.put("jandeline.santos", Responsavel.JANDELINE);
        USUARIO_MAP.put("kauane.thaisa", Responsavel.KAUANE);
        USUARIO_MAP.put("leonardo.henrique", Responsavel.LEONARDO_H);
        USUARIO_MAP.put("lucas.fragoso", Responsavel.LUCAS_F);
        USUARIO_MAP.put("lucas.roberto", Responsavel.LUCAS_R);
        USUARIO_MAP.put("adilson.gabriel", Responsavel.ADILSON);
        USUARIO_MAP.put("lucas.vieira", Responsavel.LUCAS_V);
        USUARIO_MAP.put("larissa.sousa", Responsavel.LARISSA);
        USUARIO_MAP.put("murilo.clem", Responsavel.MURILO);
        USUARIO_MAP.put("thiago.silva", Responsavel.THIAGO);
        USUARIO_MAP.put("samuel.rodrigues", Responsavel.SAMUEL);
        USUARIO_MAP.put("nicoly.leoleo", Responsavel.NICOLY);
    }

    public boolean autenticar(String nome, String senha) {
        return USUARIO_MAP.containsKey(nome) && SENHA_PADRAO.equals(senha);
    }

    public Responsavel getResponsavelPorUsuario(String nome) {
        return USUARIO_MAP.get(nome);
    }
}
