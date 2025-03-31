package com.ems.employee_service.utils.pagination;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PaginationQueryDTO {

    @Positive(message = "perPage must be a positive number")
    private Integer perPage = 20;

    @Positive(message = "page must be a positive number")
    private Integer page = 1;

    public void setPerPage(String perPage) {
        if (perPage != null) {
            try {
                this.perPage = Integer.parseInt(perPage);
            } catch (NumberFormatException e) {
                this.perPage = 20;
            }
        }
    }

    public void setPage(String page) {
        if (page != null) {
            try {
                this.page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                this.page = 1;
            }
        }
    }
}