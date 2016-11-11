package com.github.bsferreira.shiro.poc.realm;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRealm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRealm.class);

    private static final Map<String, Set<String>> idToRoles;

    static {
        idToRoles = new ConcurrentHashMap<>();
        Set<String> bruno = new HashSet<>();
        bruno.add("ADMIN");
        idToRoles.put("bruno", bruno);
        Set<String> ricardo = new HashSet<>();
        ricardo.add("READ");
        idToRoles.put("ricardo", ricardo);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        LOGGER.debug("Starting authorization...");
        final String username = (String) principalCollection.getPrimaryPrincipal();
        final Set<String> roles = Optional.ofNullable(idToRoles.get(username)).orElse(new HashSet<>());
        LOGGER.debug("Creating authorization for username: " + username + " - roles: " + roles.toString());
        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        if ("123456".equals(String.valueOf(token.getPassword()))) {
            return new SimpleAuthenticationInfo(authenticationToken.getPrincipal(),
                    authenticationToken.getCredentials(), this.getName());
        } else {
            throw new UnknownAccountException(token.getUsername());
        }
    }
}
