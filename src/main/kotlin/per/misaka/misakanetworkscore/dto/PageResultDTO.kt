package per.misaka.misakanetworkscore.dto

data class PageResultDTO<T>(
    val list: List<T>,
    val totalPages: Long,
    val totalElements: Long,
    val currentPage: Int,
    val currentPageSize: Int
)

