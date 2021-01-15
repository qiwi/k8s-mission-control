package ru.qiwi.devops.mission.control.web.filters

import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import ru.qiwi.devops.mission.control.web.cookie.CookieFactory
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LogoutFilter(
    private val cookieFactory: CookieFactory
) : HttpFilter(), Ordered {
    override fun getOrder() = FiltersOrdering.LOGOUT

    override fun doFilter(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        if (req.method == "DELETE" && req.requestURI == "/api/sessions") {
            res.addCookie(cookieFactory.createEmptyCookie())
        }

        chain.doFilter(req, res)
    }
}