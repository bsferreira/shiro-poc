package com.github.bsferreira.shiro.poc.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSessionDAO extends AbstractSessionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSessionDAO.class);

    private Map<Serializable, Session> sessionMap = new HashMap<>();

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        sessionMap.put(sessionId, session);
        LOGGER.debug("Created session: " + sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
        return sessionMap.get(serializable);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        sessionMap.get(session.getId());
        sessionMap.put(session.getId(), session);
        LOGGER.debug("Updated session: " + session.getId());
    }

    @Override
    public void delete(Session session) {
        sessionMap.remove(session.getId());
        LOGGER.debug("Deleted session: " + session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return sessionMap.values();
    }
}
