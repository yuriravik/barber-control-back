package br.com.ravikyu.barbercontrol.application.servico.mapper;

import br.com.ravikyu.barbercontrol.application.servico.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.servico.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.domain.model.Servico;

public class ServicoMapper {

    private ServicoMapper() {}

    public static Servico toDomain(CriarServicoRequest dto) {
        return new Servico(
                null,
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                dto.duracaoMinutos(),
                true
        );
    }

    public static ServicoResponse toResponse(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                servico.isAtivo()
        );
    }
}
