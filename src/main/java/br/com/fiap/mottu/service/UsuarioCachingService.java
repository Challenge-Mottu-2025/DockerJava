package br.com.fiap.mottu.service;

import br.com.fiap.mottu.models.Usuario;
import br.com.fiap.mottu.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioCachingService {

    @Autowired
    UsuarioRepository repository;

    @Cacheable(value = "usuariosFindAll")
    public List<Usuario> cacheFindAll() {
        return repository.findAll();
    }

    @Cacheable(value = "usuariosFindById", key = "#cpf")
    public Optional<Usuario> findById(String cpf) {
        return repository.findById(cpf);
    }

    @CacheEvict(value = {"usuariosFindAll", "usuariosFindById"}, allEntries = true)
    public void limparCache() {
        System.out.println("Limpando caches de usuários");
    }
}