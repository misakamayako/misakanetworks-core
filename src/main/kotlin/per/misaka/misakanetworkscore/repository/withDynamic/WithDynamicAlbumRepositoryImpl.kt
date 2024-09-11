package per.misaka.misakanetworkscore.repository.withDynamic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.dto.AlbumBrief
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class WithDynamicAlbumRepositoryImpl : WithDynamicAlbumRepository {
    @Autowired
    private lateinit var databaseClient: DatabaseClient

    override fun findAlbumBrief(
        pageable: Pageable,
        isPrivate: Boolean?,
        name: String?,
        cec: Boolean?,
        categories: List<Int>?
    ): Flux<AlbumBrief> {
        val sb = StringBuilder(
            """
            select a.*, i.oss_key as coverURL
            from albums a
                     left join images i on i.id = a.cover_id
                     left join albums_to_category atc on atc.album_id = a.id
            where a.has_delete = false
            
        """
        )
        val params = HashMap<String, Any>()
        isPrivate?.let {
            sb.append("\r\n   and a.is_private = :isPrivate")
            params["isPrivate"] = isPrivate
        }
        name?.let {
            sb.append("\r\n      and a.name like :name")
            params["name"] = "%$name%"
        }
        cec?.let {
            sb.append("\r\n and a.contains_explicit_content = :containsExplicitContent")
            params["containsExplicitContent"] = it
        }
        categories.takeIf { !it.isNullOrEmpty() }?.let {
            sb.append("\r\n and atc.category_id in (:categories)")
            params["categories"] = it
        }
        sb.append("\r\n limit :limit")
        params["limit"] = pageable.pageSize
        sb.append("\r\n offset :offset")
        params["offset"] = pageable.offset
        return databaseClient
            .sql(sb.toString())
            .bindValues(params)
            .map { row, _ ->
                AlbumBrief(
                    id = row.get("id", Int::class.java)!!,
                    name = row.get("name", String::class.java)!!,
                    description = row.get("description", String::class.java)!!,
                    coverURL = row.get("coverURL", String::class.java)!!,
                    createAt = row.get("createAt", LocalDateTime::class.java)!!,
                    updateAt = row.get("updateAt", LocalDateTime::class.java)!!,
                    contentExplicitContent = row.get("contains_explicit_content", Boolean::class.java)!!,
                    hasDelete = row.get("has_delete", Boolean::class.java)!!,
                    isPrivate = row.get("isPrivate", Boolean::class.java)!!
                )
            }.all()
    }

    override fun getCount(isPrivate: Boolean?, name: String?, cec: Boolean?, categories: List<Int>?): Mono<Int> {
        val sb = StringBuilder(
            """
            select count(a.id)
            from albums a
                     left join images i on i.id = a.cover_id
            where a.has_delete = false
        """
        )
        val params = HashMap<String, Any>()
        isPrivate?.let {
            sb.append("\r\n   and a.is_private = :isPrivate")
            params["isPrivate"] = isPrivate
        }
        name?.let {
            sb.append("\r\n      and a.name like :name")
            params["name"] = "%$name%"
        }
        cec?.let {
            sb.append("\r\n and a.contains_explicit_content = :containsExplicitContent")
            params["containsExplicitContent"] = it
        }
        categories.takeIf { !it.isNullOrEmpty() }?.let {
            sb.append("\r\n and atc.category_id in (:categories)")
            params["categories"] = it
        }
        return databaseClient
            .sql(sb.toString())
            .bindValues(params)
            .map{row,_->row.get(0,Int::class.java)}
            .first()
    }
}