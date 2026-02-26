package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.CriarServicoRequest;
import br.com.ravikyu.barbercontrol.application.dto.ServicoResponse;
import br.com.ravikyu.barbercontrol.domain.model.Servico;

public class ServicoMapper {

    public static ServicoResponse toDomain(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                servico.isAtivo()
        );
    }

    public static Servico toRequest(CriarServicoRequest dto) {
        return new Servico(
                null,
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                dto.duracaoMinutos(),
                dto.ativo()
        );
    }
}