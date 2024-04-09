package per.misaka.misakanetworkscore.component

import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.repository.AuthorityRepository

@Component
class AuthorityComponent(private val authorityDB: AuthorityRepository) {
    suspend fun getAuthorities(userId: Int) =
        authorityDB.getAllByUserId(userId).map { GrantedAuthority { it.authority } }

}
