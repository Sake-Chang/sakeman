package com.sakeman.factory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageRequestFactory {

    public Pageable createPageRequest(int start, int length, int orderColumn, String orderDir, String[] columnNames) {
        int page = start / length;
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(orderDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortColumn = getSortColumn(orderColumn, columnNames);
        return PageRequest.of(page, length, Sort.by(sortDirection, sortColumn));
    }

    private String getSortColumn(int columnIndex, String[] columnNames) {
        if (columnIndex >= 0 && columnIndex < columnNames.length) {
            return columnNames[columnIndex];
        }
        return "id"; // デフォルトでIDを返す
    }
}
