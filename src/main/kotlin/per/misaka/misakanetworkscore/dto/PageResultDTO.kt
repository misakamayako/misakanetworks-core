package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PageResultDTO<T>(
    val list: List<T>?,
    val totalElements: Int,
    val currentPage: Int,
    val currentPageSize: Int
) {
    @JsonProperty
    val totalPages: Int = (totalElements + currentPage - 1) / currentPageSize
}

