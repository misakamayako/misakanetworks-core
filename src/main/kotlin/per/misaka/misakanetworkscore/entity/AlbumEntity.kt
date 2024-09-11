package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "albums")
data class AlbumEntity(
    @Id
    val id: Int? = null,

    val name: String,

    val description: String? = null,

    val cover: Int? = null,

    @Column("create_at")
    val createAt: Instant = Instant.now(),

    @Column("update_at")
    val updateAt: Instant = Instant.now(),

    @Column("is_private")
    val isPrivate: Boolean = false,

    @Column("contains_explicit_content")
    val containsExplicitContent: Boolean = false,

    @Column("has_delete")
    val hasDelete: Boolean = false
)
