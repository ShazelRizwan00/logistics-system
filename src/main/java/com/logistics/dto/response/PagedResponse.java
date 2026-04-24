package com.logistics.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic wrapper for paginated responses.
 * Avoids exposing Spring's Page object directly over the API.
 *
 * Usage:
 *   PagedResponse<OrderResponse> response =
 *       PagedResponse.of(orderPage, OrderResponse::from);
 */
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <E, T> PagedResponse<T> of(Page<E> page, Function<E, T> mapper) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content       = page.getContent().stream().map(mapper).collect(Collectors.toList());
        r.page          = page.getNumber();
        r.size          = page.getSize();
        r.totalElements = page.getTotalElements();
        r.totalPages    = page.getTotalPages();
        r.last          = page.isLast();
        return r;
    }

    /**
     * Convenience factory when the page content is already of the target type
     * (e.g. after calling page.map() in a service method).
     */
    public static <T> PagedResponse<T> from(Page<T> page) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content       = page.getContent();
        r.page          = page.getNumber();
        r.size          = page.getSize();
        r.totalElements = page.getTotalElements();
        r.totalPages    = page.getTotalPages();
        r.last          = page.isLast();
        return r;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
