package com.github.bsferreira.shiro.poc.services;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @RequestMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "username", defaultValue = "password") String username,
            String password, HttpSession httpSession) {

        String sessionId = httpSession.getId();
        LOGGER.debug("http session: " + sessionId);

        Subject subject = new Subject.Builder().sessionId(sessionId).buildSubject();
        Session session = subject.getSession();
        LOGGER.debug("shiro session: " + session.getId());

        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            try {
                subject.login(token);
                LOGGER.info("User " + token.getPrincipal() + " logged in with session " + session.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (UnknownAccountException uae) {
                LOGGER.error("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                LOGGER.error("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
                LOGGER.error("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            } catch (AuthenticationException ae) {
                LOGGER.error("Authentication failed.");
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/admin")
    public ResponseEntity<String> admin(HttpSession httpSession) throws IOException {
        String sessionId = httpSession.getId();
        Subject subject = new Subject.Builder().sessionId(sessionId).buildSubject();

        if (subject.isAuthenticated() && subject.hasRole("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping("/read")
    public ResponseEntity<String> read(HttpSession httpSession) throws IOException {
        String sessionId = httpSession.getId();
        Subject subject = new Subject.Builder().sessionId(sessionId).buildSubject();

        if (subject.isAuthenticated() && subject.hasRole("READ") || subject.hasRole("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpSession httpSession, HttpServletResponse response) throws IOException {
        LOGGER.debug("logout http session: " + httpSession.getId());

        httpSession.invalidate();
    }
}

