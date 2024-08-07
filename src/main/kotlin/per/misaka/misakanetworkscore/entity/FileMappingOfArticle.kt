package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("file_mapping_of_article")
data class FileMappingOfArticle(
    val bucket:String,
    @Column("file_key")
    val fileKey:String,

    @Column("connect_file")
    var connectFile:Int?=null,

    @Column("delete_flag")
    val deleteFlag:Boolean=false,

    @Id
    var id:Int?=null,
)
