package com.paladin.monitor.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 基于前后端分离，使用自带token机制
 */
@Slf4j
public class ShiroWebSessionManager extends DefaultWebSessionManager {

    private String tokenField;

    public ShiroWebSessionManager(ShiroProperties shiroProperties) {
        super();
        tokenField = shiroProperties.getTokenField();
        // 关闭session全局校验
        setSessionValidationSchedulerEnabled(false);
    }

    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable id = key.getSessionId();
        if (id == null && WebUtils.isWeb(key)) {
            ServletRequest request = WebUtils.getRequest(key);
            String token = ((HttpServletRequest) request).getHeader(tokenField);
            // 优先考虑TOKEN机制
            if (token != null && token.length() != 0) {
                // 复制与父类代码
                //request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.URL_SESSION_ID_SOURCE);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
                request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());

                return token;
            }
        }

        return id;
    }

    /**
     * 重写检索session方法，在request上缓存session对象，从而减少session的读取
     */
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

        Serializable sessionId = getSessionId(sessionKey);
        if (sessionId == null) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a session could not be found.", sessionKey);
            }

            return null;
        }

        /*
         * 首先从request中获取session，否则从数据库中检索
         */

        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        if (request != null) {
            Object s = request.getAttribute("_sc_" + sessionId.toString());
            if (s != null) {
                return (Session) s;
            }
        }

        Session s = retrieveSessionFromDataSource(sessionId);
        if (s == null) {
            // session ID was provided, meaning one is expected to be found, but
            // we couldn't find one:
            String msg = "Could not find session with ID [" + sessionId + "]";
            throw new UnknownSessionException(msg);
        }

        // 保存session到request
        if (request != null) {
            request.setAttribute("_sc_" + sessionId.toString(), s);
        }

        return s;
    }

}
