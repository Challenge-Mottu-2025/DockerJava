package br.com.fiap.mottu.repositories;

import br.com.fiap.mottu.models.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    
    
}