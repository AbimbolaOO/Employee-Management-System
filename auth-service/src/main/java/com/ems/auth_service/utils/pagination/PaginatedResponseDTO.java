package com.ems.auth_service.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PaginatedResponseDTO<T> {
    private final List<T> records;
    private final long totalPages;
    private final long totalRecords;
    private final int page;
    private final int perPage;

    public PaginatedResponseDTO(Page<T> page) {
        this.records = page.getContent();
        this.page = page.getNumber() + 1;
        this.perPage = page.getSize();
        this.totalRecords = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public static <T> PaginatedResponseDTO<T> from(Page<T> data) {
        return new PaginatedResponseDTO<>(data);
    }
}