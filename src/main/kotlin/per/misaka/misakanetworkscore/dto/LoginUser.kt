package per.misaka.misakanetworkscore.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class LoginUser(
    private val user: User,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {
    private lateinit var token: String;
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return authorities
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getUsername(): String? {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return user.isAccountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return !user.isAccountNonLocked
    }
//    @JSONField(serialize = false)
//    @Override
//    public boolean isCredentialsNonExpired()
//    {
//        return true;
//    }
//
//    @JSONField(serialize = false)
//    @Override
//    public boolean isEnabled()
//    {
//        return user.getIsActive();
//    }

}
