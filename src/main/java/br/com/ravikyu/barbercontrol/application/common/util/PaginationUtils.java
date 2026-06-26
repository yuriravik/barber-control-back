package br.com.ravikyu.barbercontrol.application.common.util;

import br.com.ravikyu.barbercontrol.application.common.dto.PageResponse;
import br.com.ravikyu.barbercontrol.infrastructure.web.exception.BusinessException;

import java.util.List;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T> PageResponse<T> paginate(List<T> items, int page, int size) {
        if (page < 0) {
            throw new BusinessException("Página deve ser maior ou igual a 0");
        }
        if (size <= 0) {
            throw new BusinessException("Tamanho da página deve ser maior que 0");
        }

        long totalElements = items.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(page * size, items.size());
        int toIndex = Math.min(fromIndex + size, items.size());

        return new PageResponse<>(items.subList(fromIndex, toIndex), page, size, totalElements, totalPages);
    }
}
