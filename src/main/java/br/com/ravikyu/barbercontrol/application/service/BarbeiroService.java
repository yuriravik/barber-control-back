package br.com.ravikyu.barbercontrol.application.service;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.application.mapper.BarbeiroMapper;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;
import br.com.ravikyu.barbercontrol.domain.repository.BarbeiroRepository;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));

        var atualizado = new Barbeiro(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getPercentualComissao(),
                false
        );

        repository.salvar(atualizado);
    }


}
