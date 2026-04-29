package com.medcare.service.generic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Cursor-style page of items returned from list endpoints.
 *
 * @param <T> element type of {@link #content}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /** Records for the current page. */
    private List<T> content;
    /** Total rows matching the query across all pages. */
    private long totalElements;
    /** Zero-based page index. */
    private int page;
    /** Page size requested. */
    private int size;
    /** Total number of pages. */
    private int totalPages;
    /** {@code true} if this is the last page. */
    private boolean last;
}
