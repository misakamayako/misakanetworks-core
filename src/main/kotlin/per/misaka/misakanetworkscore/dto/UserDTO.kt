package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.Transient

data class UserDTO(
    @field:NotNull("用户名必填")
    @field:NotEmpty(message = "用户名不能为空")
    val username:String,

    @field:NotNull("密码必填")
    @field:NotEmpty(message = "密码不能为空")
    @field:JsonIgnore
    val password:String
){
    override fun toString(): String {
        return "UserDTO(username=$username, password=***)"
    }
}
