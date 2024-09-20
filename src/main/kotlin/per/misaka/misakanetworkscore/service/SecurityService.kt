package per.misaka.misakanetworkscore.service

import org.springframework.security.core.userdetails.UserDetails

interface SecurityService{
    suspend fun findUserDetailByName(name:String): UserDetails?
}