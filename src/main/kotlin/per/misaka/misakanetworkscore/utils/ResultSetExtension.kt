package per.misaka.misakanetworkscore.utils

import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <T : Any> ResultSet.get(columnLabel: String, claze: KClass<T>): T? {
    return (when (claze) {
        String::class -> getString(columnLabel)
        Long::class -> getLong(columnLabel)
        Int::class -> getInt(columnLabel)
        Boolean::class -> getBoolean(columnLabel)
        Double::class -> getDouble(columnLabel)
        Float::class -> getFloat(columnLabel)
        LocalDateTime::class -> getTimestamp(columnLabel)?.toLocalDateTime()
        LocalDate::class -> getDate(columnLabel)?.toLocalDate()
        else -> null
    }) as T?
}