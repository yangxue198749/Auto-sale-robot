package com.kdx.vms.web.controller;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication REST API controller.
 * Provides session status and current user information for Vue frontend.
 */
@Controller
@RequestMapping("/vms/api/auth")
public class AuthApiController {

    private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);

    /**
     * Get current user information.
     * Returns user details if authenticated, 401 if not.
     * This endpoint is used by Vue frontend to restore session on page refresh.
     */
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> response = new HashMap<>();

        try {
            Subject subject = SecurityUtils.getSubject();

            // Check if user is authenticated
            if (!subject.isAuthenticated()) {
                response.put("authenticated", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Get SSOToken from Shiro principal
            Object principal = subject.getPrincipal();
            if (!(principal instanceof SSOToken)) {
                response.put("authenticated", false);
                response.put("message", "Invalid principal type");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            SSOToken token = (SSOToken) principal;

            // Get user from UserMap (stored in session)
            TSysUser user = token.getSysUser();

            if (user == null) {
                response.put("authenticated", false);
                response.put("message", "User not found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check if user account is locked
            if (user.getLockMark() != null && user.getLockMark() == 1) {
                response.put("authenticated", false);
                response.put("message", "Account is locked");
                // Invalidate session for locked account
                subject.logout();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Build user info response
            response.put("authenticated", true);
            response.put("userId", user.getId());
            response.put("userName", user.getUserName());
            response.put("userAccount", user.getUserAccount());

            // Add company info if available
            if (user.getCompany() != null) {
                Map<String, Object> company = new HashMap<>();
                company.put("id", user.getCompany().getId());
                company.put("name", user.getCompany().getCompanyName());
                company.put("level", user.getCompany().getCompanyLevel());
                response.put("company", company);
            }

            // Add roles from authInfo if available
            if (token.getAuthInfo() != null && token.getAuthInfo().getRoles() != null) {
                response.put("roles", token.getAuthInfo().getRoles().toArray(new String[0]));
            } else {
                response.put("roles", new String[]{});
            }

            // Add permissions from authInfo if available
            if (token.getAuthInfo() != null && token.getAuthInfo().getStringPermissions() != null) {
                response.put("permissions", token.getAuthInfo().getStringPermissions().toArray(new String[0]));
            } else {
                response.put("permissions", new String[]{});
            }

            logger.debug("Current user info retrieved: {}", user.getUserAccount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting current user info", e);
            response.put("authenticated", false);
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Logout current user.
     * Invalidates Shiro session and returns success.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();

        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();

            response.put("success", true);
            response.put("message", "Logout successful");

            logger.info("User logged out");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during logout", e);
            response.put("success", false);
            response.put("message", "Logout failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
