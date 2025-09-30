package br.com.fiap.mottu.repositories;

import br.com.fiap.mottu.models.Funcionario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByCpf(String cpf);
}