package per.misaka.misakanetworkscore.component

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse

class ResponseFilter: Filter {
    override fun doFilter(request: ServletRequest?,response: ServletResponse?, chain: FilterChain?) {
        chain?.doFilter(request,response)
        if (response!=null){
            val res = response as HttpServletResponse
//            if(res.getHeader("charset")==null){
////                if
//            }
        }
    }

}
