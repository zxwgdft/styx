package com.paladin.monitor.config.shiro;

import com.styx.common.config.GlobalConstants;
import com.styx.common.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.StoppedSessionException;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis实现共享session
 *
 * @author TontoZhou
 * @since 2018年3月16日
 */
@Slf4j
public class ShiroRedisSessionDAO implements SessionDAO {

    // session id generator
    private SessionIdGenerator sessionIdGenerator = new SessionIdGenerator() {
        @Override
        public Serializable generateId(Session session) {
            return UUIDUtil.createUUID();
        }
    };

    private static long updateSessionInterval;
    private int sessionTime;
    private int longSessionTime;
    private RedisTemplate<String, Object> redisTemplate;

    public ShiroRedisSessionDAO(ShiroProperties shiroProperties, RedisTemplate<String, Object> redisTemplate) {
        this.sessionTime = shiroProperties.getSessionTime();
        this.longSessionTime = shiroProperties.getLongSessionTime();
        this.updateSessionInterval = shiroProperties.getUpdateSessionInterval() * 60 * 1000L;
        this.redisTemplate = redisTemplate;

    }

    // ------------------------------------------
    //
    // REDIS 操作缓存
    //
    // ------------------------------------------

    private String getRedisKey(Serializable sessionId) {
        return GlobalConstants.WEB_SESSION_PREFIX + sessionId.toString();
    }

    /**
     * 缓存session，放入本地缓存和redis缓存
     *
     * @param sessionId
     * @param session
     */
    private void cacheSessioin(Serializable sessionId, Session session, boolean rememberMe) {
        redisTemplate.opsForValue().set(getRedisKey(sessionId), session, rememberMe ? longSessionTime : sessionTime, TimeUnit.MINUTES);
        if (log.isDebugEnabled()) {
            log.debug("添加session缓存：" + session);
        }
    }

    /**
     * 清楚缓存session，清楚本地和redis中的缓存
     *
     * @param session
     */
    private void uncacheSession(Session session) {
        redisTemplate.delete(getRedisKey(session.getId()));
        if (log.isDebugEnabled()) {
            log.debug("移除Redis中session缓存：" + session);
        }
    }

    /**
     * 获取缓存session，先从本地缓存获取，如果没有则去redis获取
     *
     * @param sessionId
     * @return
     */
    private Session getCacheSession(Serializable sessionId) {
        Session cached = (Session) redisTemplate.opsForValue().get(getRedisKey(sessionId));
        if (log.isDebugEnabled()) {
            log.debug("从Redis获取session缓存：" + cached);
        }
        return cached;
    }

    /**
     * 更新过期时间
     *
     * @param sessionId
     */
    private void updateCacheExpireTime(Serializable sessionId, boolean rememberMe) {
        redisTemplate.expire(getRedisKey(sessionId), rememberMe ? longSessionTime : sessionTime, TimeUnit.MINUTES);
        if (log.isDebugEnabled()) {
            log.debug("更新Redis中session缓存过期时间：" + sessionId);
        }
    }

    // ------------------------------------------
    //
    // SessionDao implements
    //
    // ------------------------------------------

    @Override
    public Serializable create(Session session) {
        Serializable sessionId = this.sessionIdGenerator.generateId(session);
        ((SimpleSession) session).setId(sessionId);
        cacheSessioin(sessionId, session, false);
        return sessionId;
    }

    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        Session session = getCacheSession(sessionId);
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session instanceof ControlledSession) {
            ControlledSession controlledSession = (ControlledSession) session;
            if (controlledSession.isValid()) {
                // TODO 需要考虑改变了session中object中值，而并没有调用ControlledSession中方法时isContentChanged值不会改变，所以不会更新问题
                if (controlledSession.needUpdate) {
                    cacheSessioin(session.getId(), session, controlledSession.rememberMe);
                    controlledSession.needUpdate = false;
                } else {
                    updateCacheExpireTime(session.getId(), controlledSession.rememberMe);
                }
            } else {
                uncacheSession(session);
            }
        } else {
            if (session instanceof ValidatingSession) {
                if (((ValidatingSession) session).isValid()) {
                    cacheSessioin(session.getId(), session, false);
                } else {
                    uncacheSession(session);
                }
            } else {
                cacheSessioin(session.getId(), session, false);
            }
        }
    }

    @Override
    public void delete(Session session) {
        uncacheSession(session);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }

    /**
     * 可控制的session，对session内容的修改进行了判断，用于判断是否只是更新了session的最后访问时间
     */
    public static class ControlledSession extends SimpleSession {

        private transient boolean needUpdate;

        private boolean rememberMe;

        public ControlledSession() {
            super();
            needUpdate = true;
        }

        public ControlledSession(String host) {
            super(host);
            needUpdate = true;
        }

        @Override
        public void setId(Serializable id) {
            super.setId(id);
            needUpdate = true;
        }

        @Override
        public void setStopTimestamp(Date stopTimestamp) {
            super.setStopTimestamp(stopTimestamp);
            needUpdate = true;
        }

        @Override
        public void setExpired(boolean expired) {
            super.setExpired(expired);
            needUpdate = true;
        }

        @Override
        public void setTimeout(long timeout) {
            super.setTimeout(timeout);
            needUpdate = true;
        }

        @Override
        public void setHost(String host) {
            super.setHost(host);
            needUpdate = true;
        }

        @Override
        public void setAttributes(Map<Object, Object> attributes) {
            super.setAttributes(attributes);
            needUpdate = true;
        }

        @Override
        public void setAttribute(Object key, Object value) {
            super.setAttribute(key, value);
            needUpdate = true;
        }

        @Override
        public Object removeAttribute(Object key) {
            needUpdate = true;
            return super.removeAttribute(key);
        }

        public void touch() {
            Date lastAccessTime = getLastAccessTime();
            // 通过延长redis中session过期时间来减少session持久化次数（在session无其他内容变化，仅仅只是更新时间的改变情况下）
            if (lastAccessTime != null && (System.currentTimeMillis() - lastAccessTime.getTime() > updateSessionInterval)) {
                needUpdate = true;
            }
            setLastAccessTime(new Date());
        }

        public void forceUpdate() {
            needUpdate = true;
        }

        public void validate() throws InvalidSessionException {
            if (isStopped()) {
                //timestamp is set, so the session is considered stopped:
                String msg = "Session with id [" + getId() + "] has been " +
                        "explicitly stopped.  No further interaction under this session is " +
                        "allowed.";
                throw new StoppedSessionException(msg);
            }
            // 去除原先session过期验证

            // 基于redis实现session的过期，所以这里不需要再次验证
        }


        public boolean isRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(boolean rememberMe) {
            needUpdate = true;
            this.rememberMe = rememberMe;
        }
    }


    @Slf4j
    public static class ControlledSessionFactory implements SessionFactory {

        /*
         * 使用 {@link com.paladin.configuration.ShiroRedisSessionDAO.ControlledSession}
         * 控制session update次数
         */
        @Override
        public Session createSession(SessionContext initData) {
            if (initData != null) {
                String host = initData.getHost();
                if (host != null) {

                    if (log.isDebugEnabled()) {
                        log.debug("创建ControlledSession[HOST:" + host + "]");
                    }

                    return new ShiroRedisSessionDAO.ControlledSession(host);
                }
            }

            if (log.isDebugEnabled()) {
                log.info("创建ControlledSession[HOST:无]");
            }
            return new ShiroRedisSessionDAO.ControlledSession();
        }

    }
}
