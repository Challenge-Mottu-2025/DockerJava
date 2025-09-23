package br.com.fiap.mottu.controllers;

import org.springframework.data.web.config.EnableSpringDataWebSupport;

import br.com.fiap.mottu.dto.FuncionarioDTO;
import br.com.fiap.mottu.dto.IntroDTO;
import br.com.fiap.mottu.dto.EnderecoDTO;
import br.com.fiap.mottu.models.Endereco;
import br.com.fiap.mottu.models.Funcionario;
import br.com.fiap.mottu.repositories.FuncionarioRepository;
import br.com.fiap.mottu.service.FuncionarioCachingService;
import br.com.fiap.mottu.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/funcionarios")
@EnableSpringDataWebSupport
public class FuncionarioController {

    @Autowired
    FuncionarioRepository repositorio;

    @Autowired
    FuncionarioService service;

    @Autowired
    FuncionarioCachingService cachingService;

    @GetMapping
    public ResponseEntity<EntityModel<IntroDTO>> intro() {
        IntroDTO dto = new IntroDTO("Setor de funcionarios da Mottu");
        EntityModel<IntroDTO> resource = EntityModel.of(dto);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).pegueTodos())
                .withRel("listar-funcionarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).peguePeloId(null))
                .withRel("buscar-funcionario"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).cadastro(null))
                .withRel("criar-funcionario"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).atualizar(null, null))
                .withRel("atualizar-funcionario"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).deletar(null))
                .withRel("deletar-funcionario"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/todos")
    public ResponseEntity pegueTodos() {
        List<Funcionario> listaDeFuncionarios = cachingService.cacheFindAll();
        return ResponseEntity.ok(listaDeFuncionarios);
    }

    @GetMapping("/paginados")
    public ResponseEntity<Page<FuncionarioDTO>> peguePorPagina(@RequestParam(value ="page", defaultValue = "0") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "2") Integer size)
    {
        PageRequest req = PageRequest.of(page, size);
        Page<FuncionarioDTO> funcionarios_paginadas = service.paginar(req);
        return ResponseEntity.ok(funcionarios_paginadas);
    }

    @GetMapping("/{id}")
    public ResponseEntity peguePeloId(@PathVariable(value = "id")  Long id) {
        Optional<Funcionario> funcionario = cachingService.findById(id);
        if (funcionario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado.");
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).body(funcionario.get());
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid FuncionarioDTO dto) {
        var funcionario = new Funcionario();
        BeanUtils.copyProperties(dto, funcionario, "endereco");

        if (dto.endereco() == null) {
            return ResponseEntity.badRequest().body("Endereço é obrigatório.");
        }
        Endereco endereco = new Endereco();
        BeanUtils.copyProperties(dto.endereco(), endereco);
        funcionario.setEndereco(endereco);

        return ResponseEntity.status(HttpStatus.CREATED).body(repositorio.save(funcionario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable(value = "id")  Long id) {
        Optional<Funcionario> funcionario = repositorio.findById(id);
        if (funcionario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado para deletar.");
        }
        repositorio.delete(funcionario.get());
        return ResponseEntity.status(HttpStatus.OK).body("Cadastro deletado com sucesso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable(value = "id")  Long id, @RequestBody @Valid FuncionarioDTO dto) {
        Optional<Funcionario> funcionarioOpt = repositorio.findById(id);
        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado para atualizar.");
        }
        var funcionario = funcionarioOpt.get();
        BeanUtils.copyProperties(dto, funcionario, "id", "endereco");

        if (dto.endereco() != null) {
            Endereco end = funcionario.getEndereco();
            if (end == null) end = new Endereco();
            BeanUtils.copyProperties(dto.endereco(), end);
            funcionario.setEndereco(end);
        }

        return ResponseEntity.status(HttpStatus.OK).body(repositorio.save(funcionario));
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