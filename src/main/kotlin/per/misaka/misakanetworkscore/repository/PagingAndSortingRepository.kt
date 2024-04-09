package per.misaka.misakanetworkscore.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface PagingAndSortingRepository<T, ID> {
    fun findAll(sort: Sort?): Iterable<T>?

    fun findAll(pageable: Pageable?): Page<T>?
}

