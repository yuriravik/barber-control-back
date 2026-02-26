package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.domain.model.Servico;
import br.com.ravikyu.barbercontrol.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;

    public Servico criar(Servico servico) {
        servico.setId(UUID.randomUUID());
        servico.setAtivo(true);
        return repository.salvar(servico);
    }

    public List<Servico> listar() {
        return repository.listar();
    }

    public Servico buscar(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public void deletar(UUID id) {
        repository.deletar(id);
    }
}