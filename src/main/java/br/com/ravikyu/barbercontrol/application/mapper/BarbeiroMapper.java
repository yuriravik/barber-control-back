package br.com.ravikyu.barbercontrol.application.mapper;

import br.com.ravikyu.barbercontrol.application.dto.barbeiro.BarbeiroResponse;
import br.com.ravikyu.barbercontrol.application.dto.barbeiro.CriarBarbeiroRequest;
import br.com.ravikyu.barbercontrol.domain.model.Barbeiro;

public class BarbeiroMapper {

    private BarbeiroMapper() {}

    public static Barbeiro toDomain(CriarBarbeiroRequest request) {
        return new Barbeiro(
                null,
                request.nome(),
                request.especialidade(),
                request.percentualComissao(),
                request.ativo()
        );
    }

    public static BarbeiroResponse toResponse(Barbeiro barbeiro) {
        return new BarbeiroResponse(
                barbeiro.getId(),
                barbeiro.getNome(),
                barbeiro.getEspecialidade(),
                barbeiro.getPercentualComissao(),
                barbeiro.isAtivo()
        );
    }
}