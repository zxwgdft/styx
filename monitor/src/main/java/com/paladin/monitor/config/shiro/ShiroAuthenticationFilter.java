package com.paladin.monitor.config.shiro;

import com.styx.common.api.HttpCode;
import com.styx.common.api.R;
import com.styx.common.utils.convert.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author TontoZhou
 * @since 2020/10/16
 */
@Slf4j
public class ShiroAuthenticationFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = this.getSubject(request, response);
        return subject.isAuthenticated() && subject.getPrincipal() != null;
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse rep) throws Exception {
        HttpServletResponse response = (HttpServletResponse) rep;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpCode.UNAUTHORIZED.getCode());
        JsonUtil.writeJson(response.getWriter(), R.fail(HttpCode.UNAUTHORIZED, "未登录或会话超时"));
        return false;
    }


}
