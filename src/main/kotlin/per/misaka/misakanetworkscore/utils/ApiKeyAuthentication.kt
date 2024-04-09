package per.misaka.misakanetworkscore.utils

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ApiKeyAuthentication(private val apiKey:String,authorities:Collection<GrantedAuthority?>):AbstractAuthenticationToken(authorities) {
    init {
        isAuthenticated=true
    }

    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): Any {
        TODO("Not yet implemented")
    }
}
