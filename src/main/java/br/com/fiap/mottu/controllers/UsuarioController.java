package br.com.fiap.mottu.controllers;

import br.com.fiap.mottu.dto.IntroDTO;
import br.com.fiap.mottu.dto.UsuarioDTO;
import br.com.fiap.mottu.models.Endereco;
import br.com.fiap.mottu.models.Usuario;
import br.com.fiap.mottu.repositories.UsuarioRepository;
import br.com.fiap.mottu.service.UsuarioCachingService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/usuarios")
@EnableSpringDataWebSupport
public class UsuarioController {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    UsuarioCachingService cachingService;

    @GetMapping
    public ResponseEntity<EntityModel<IntroDTO>> intro() {

        IntroDTO dto = new IntroDTO("Setor de usuários da Mottu");

        EntityModel<IntroDTO> resource = EntityModel.of(dto);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).pegueTodos())
                .withRel("listar-usuarios"));

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).pegarPeloCpf(null))
                .withRel("listar-usuarios-pelo-cpf"));

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).cadastro(null))
                .withRel("cadastrar-usuarios"));

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).atualizar(null, null))
                .withRel("atualizar-usuarios"));

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioController.class).deletar(null))
                .withRel("deletar-usuarios"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/todos")
    public ResponseEntity pegueTodos(){
        List<Usuario> listaDeUsuarios = cachingService.cacheFindAll();
        return ResponseEntity.ok(listaDeUsuarios);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity pegarPeloCpf(@PathVariable(value = "cpf")  String cpf) {
        Optional<Usuario> usuario = cachingService.findById(cpf);

        if(usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        } else {
            return ResponseEntity.ok(usuario.get());
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(dto, usuario);

        if (dto.endereco() != null) {
            Endereco endereco = new Endereco();
            BeanUtils.copyProperties(dto.endereco(), endereco);
            usuario.setEndereco(endereco);
        } else {
            return ResponseEntity.badRequest().body("Endereço é obrigatório.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(usuario));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deletar(@PathVariable String cpf) {
        return repository.findById(cpf).map(u -> {
            try {
                repository.delete(u);
                if (cachingService != null) cachingService.limparCache();
                return ResponseEntity.ok(Map.of("message","Usuário deletado"));
            } catch (DataIntegrityViolationException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error","Não é possível deletar: dependências ainda existem.",
                            "detail", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()
                        ));
            }
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error","Usuário não encontrado")));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity atualizar(@PathVariable(value = "cpf") String cpf, @RequestBody UsuarioDTO dto) {
        Optional<Usuario> usuario = repository.findById(cpf);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado para atualizar.");
        }
        var usuarioAtualizado = usuario.get();
        BeanUtils.copyProperties(dto, usuarioAtualizado);
        return ResponseEntity.ok(repository.save(usuarioAtualizado));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

}