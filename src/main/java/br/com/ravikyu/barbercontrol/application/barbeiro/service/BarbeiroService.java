package br.com.ravikyu.barbercontrol.application.barbeiro.service;

import br.com.ravikyu.barbercontrol.application.barbeiro.dto.*;
import br.com.ravikyu.barbercontrol.application.barbeiro.mapper.BarbeiroMapper;
import br.com.ravikyu.barbercontrol.domain.barbeiro.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.barbeiro.repository.BarbeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BarbeiroService {

    private final BarbeiroRepository repository;

    public BarbeiroResponse criar(CriarBarbeiroRequest dto) {

        var barbeiro = BarbeiroMapper.toDomain(dto);
        var salvo = repository.salvar(barbeiro);

        return BarbeiroMapper.toResponse(salvo);
    }

    public List<BarbeiroResponse> listar() {
        return repository.listar()
                .stream()
                .map(BarbeiroMapper::toResponse)
                .toList();
    }

    public void desativar(UUID id) {
        var barbeiro = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));

        var atualizado = new Barbeiro(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getPercentualComissao(),
                barbeiro.isAtivo()
        );

        repository.salvar(atualizado);
    }


}
