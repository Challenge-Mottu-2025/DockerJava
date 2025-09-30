package br.com.fiap.mottu.security;

import br.com.fiap.mottu.models.Funcionario;
import br.com.fiap.mottu.repositories.FuncionarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioUserDetailsService implements UserDetailsService {

    private final FuncionarioRepository repository;

    public FuncionarioUserDetailsService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Funcionario f = repository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("CPF não encontrado: " + cpf));
        return new FuncionarioUserDetails(f);
    }
}