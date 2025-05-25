package br.com.fiap.mottu.repositories;

import br.com.fiap.mottu.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {



}
