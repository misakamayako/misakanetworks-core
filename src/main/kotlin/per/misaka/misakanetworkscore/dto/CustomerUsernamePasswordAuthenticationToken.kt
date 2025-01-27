package per.misaka.misakanetworkscore.dto

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomerUsernamePasswordAuthenticationToken(
    private val principal: Int?,
    private var credentials: String?,
    authorities: Collection<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    init {
        super.setAuthenticated(authorities != null)
    }

    companion object {
        @JvmStatic
        fun unauthenticated(principal: Int, credentials: String?): CustomerUsernamePasswordAuthenticationToken {
            return CustomerUsernamePasswordAuthenticationToken(principal, credentials)
        }

        @JvmStatic
        fun authenticated(
            principal: Int,
            credentials: String?,
            authorities: Collection<GrantedAuthority>?
        ): CustomerUsernamePasswordAuthenticationToken {
            return CustomerUsernamePasswordAuthenticationToken(principal, credentials, authorities)
        }
    }

    override fun getCredentials(): String? {
        return credentials
    }

    override fun getPrincipal(): Int? {
        return principal
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        credentials = null
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            throw IllegalArgumentException("无法将此令牌设置为受信任，只能使用构造函数，该构造函数接受 GrantedAuthority 列表。")
        }
//        this.isAuthenticated = false
    }
}
