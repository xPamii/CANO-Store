/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pamii
 */
@WebFilter(urlPatterns = {"/admin-dashboard.html", "/manage-product.html", "/manage-orders.html", "/reports.html"})
public class adminSessionFilter {

    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional initialization
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Prevent browser caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // Check for valid session and user attribute
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            chain.doFilter(req, res);
        } else {
            System.out.println("Session check failed: redirecting to admin-login.html");
            response.sendRedirect("admin-login.html");
        }
    }

    public void destroy() {
        // Optional cleanup
    }
}
