package br.com.fiap.mottu.service;

import br.com.fiap.mottu.dto.FuncionarioDTO;
import br.com.fiap.mottu.models.Funcionario;
import br.com.fiap.mottu.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private FuncionarioCachingService cachingService;

    @Transactional(readOnly = true)
    public Page<FuncionarioDTO> paginar(PageRequest req) {
        Page<Funcionario> funcionarios = cachingService.findAll(req);

        return funcionarios.map(i -> new FuncionarioDTO(i.getNome(), i.getCpf(), i.getSenha()));
    }

}
