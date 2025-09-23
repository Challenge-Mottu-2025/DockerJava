package br.com.fiap.mottu.controllers;

import br.com.fiap.mottu.dto.UsuarioForm;
import br.com.fiap.mottu.models.Endereco;
import br.com.fiap.mottu.models.Moto;
import br.com.fiap.mottu.models.Usuario;
import br.com.fiap.mottu.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/usuarios/ui")
public class UsuarioViewController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String lista(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/list";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        return "usuarios/form";
    }

    @PostMapping
    public String criar(@ModelAttribute("usuarioForm") @Valid UsuarioForm form,
                        BindingResult binding,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "usuarios/form";
        }

        Usuario usuario = new Usuario();

        // Usuario
        usuario.setCpf(form.getCpf());
        usuario.setNome(form.getNome());
        Date dt = Date.from(form.getDataNascimento()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        usuario.setDataNascimento(dt);

        // Endereco
        Endereco end = new Endereco();
        end.setCep(form.getCep());
        end.setPais(form.getPais());
        end.setEstado(form.getEstado());
        end.setCidade(form.getCidade());
        end.setBairro(form.getBairro());
        end.setNumero(form.getNumero());
        end.setLogradouro(form.getLogradouro());
        end.setComplemento(form.getComplemento());
        usuario.setEndereco(end);

        // Moto
        Moto moto = new Moto();
        moto.setPlaca(form.getPlaca());
        moto.setCpf(form.getCpfMoto() == null || form.getCpfMoto().isBlank() ? form.getCpf() : form.getCpfMoto());
        moto.setNiv(form.getNiv());
        moto.setMotor(form.getMotor());
        moto.setRenavam(form.getRenavam());
        moto.setFipe(form.getFipe());
        usuario.setPlaca(moto);

        usuarioRepository.save(usuario);
        ra.addFlashAttribute("msg", "Usuário cadastrado com sucesso!");
        return "redirect:/usuarios/ui";
    }
}