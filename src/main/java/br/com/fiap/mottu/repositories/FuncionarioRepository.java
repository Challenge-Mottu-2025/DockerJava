package br.com.fiap.mottu.repositories;

import br.com.fiap.mottu.models.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional findById(UUID id);

}
