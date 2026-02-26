package br.com.ravikyu.barbercontrol.application.dto.mapper;

import br.com.ravikyu.barbercontrol.application.dto.ServicoDto;
import br.com.ravikyu.barbercontrol.domain.model.Servico;

public class ServicoMapper {

    public static ServicoDto toDto(Servico servico) {
        return new ServicoDto(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                servico.isAtivo()
        );
    }

    public static Servico toDomain(ServicoDto dto) {
        return new Servico(
                dto.id(),
                dto.nome(),
                dto.descricao(),
                dto.preco(),
                dto.duracaoMinutos(),
                dto.ativo()
        );
    }
}