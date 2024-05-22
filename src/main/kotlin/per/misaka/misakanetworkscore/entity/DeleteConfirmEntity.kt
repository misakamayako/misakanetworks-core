package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("delete_confirm")
data class DeleteConfirmEntity(
    @Id
    val id: Int? = null,
    val uuid: String,
    val type: String
)

