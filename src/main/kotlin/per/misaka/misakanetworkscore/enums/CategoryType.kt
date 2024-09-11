package per.misaka.misakanetworkscore.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class CategoryType {
    Article,
    Image,
    Album;

    @JsonValue
    fun getValue(): Int {
        return this.ordinal
    }
}