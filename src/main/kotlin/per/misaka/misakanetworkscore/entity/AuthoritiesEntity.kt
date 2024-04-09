package per.misaka.misakanetworkscore.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("authorities")
data class AuthoritiesEntity (
    @Column("userId")
    val userId:Int,
    val authority:String
)
