package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import per.misaka.misakanetworkscore.enums.CategoryType

@Table("category")
class CategoryEntity(
    @Id
    var id: Int? = null,
    var description: String,
    var type: CategoryType
)
