package br.com.fiap.mottu.service;

import br.com.fiap.mottu.models.Usuario;
import br.com.fiap.mottu.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioCachingService {

    @Autowired
    UsuarioRepository repository;

    @Cacheable(value = "cacheFindAll")
    public List<Usuario> cacheFindAll() {
        return repository.findAll();
    }

    @Cacheable(value = "cacheFindById", key = "#cpf")
    public Optional<Usuario> findById(String cpf) {
        return repository.findById(cpf);
    }
}
