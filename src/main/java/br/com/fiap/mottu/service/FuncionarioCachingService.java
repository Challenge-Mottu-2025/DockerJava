package br.com.fiap.mottu.service;

import br.com.fiap.mottu.models.Funcionario;
import br.com.fiap.mottu.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioCachingService {

    @Autowired
    FuncionarioRepository repository;

    @Cacheable(value = "cacheFindAll")
    public List<Funcionario> cacheFindAll() {
        return repository.findAll();
    }

    @Cacheable(value = "cacheFindById", key = "#id")
    public Optional<Funcionario> findById(Long id) {
        return repository.findById(id);
    }

    @Cacheable(value = "cacheFindByPage", key = "#req")
    public Page<Funcionario> findAll(PageRequest req) {
        return repository.findAll(req);
    }

    @CacheEvict(value = {"cacheFindAll", "cacheFindById"}, allEntries = true)
    public void limparCache() {
        System.out.println("Limpando o cache!");
    }

}