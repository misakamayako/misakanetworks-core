package per.misaka.misakanetworkscore.utils

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import per.misaka.misakanetworkscore.dto.UserCredentials

class UserDetailAuthentication : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        logger.info("user login")
        val objectMapper = ObjectMapper()
        val jsonParser = objectMapper.factory.createParser(request.inputStream)
        val userCredentials = jsonParser.codec.readValues(jsonParser, UserCredentials::class.java).next()
        val authRequest =
            UsernamePasswordAuthenticationToken.unauthenticated(userCredentials.username, userCredentials.password)
        super.setDetails(request, authRequest)
        return super.getAuthenticationManager().authenticate(authRequest);
    }
}
