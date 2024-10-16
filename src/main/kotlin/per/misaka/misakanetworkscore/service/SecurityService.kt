package per.misaka.misakanetworkscore.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

interface SecurityService{
    suspend fun findUserDetailByName(name:String): UserDetailInMine?
}

data class UserDetailInMine(
    val id:Int,
    val password:String,
    val username:String
):UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }

    override fun getPassword(): String {
        return this.password
    }


    override fun getUsername(): String {
        return this.username
    }
}