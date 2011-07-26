<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.jboss.seam.security.Identity" %>
<%@ page import="org.drools.guvnor.server.util.BeanManagerUtils" %>
<%
    Identity identity = BeanManagerUtils.getIdentityInstance();
    identity.logout();
    String redirectURL = "Guvnor.jsp";
    response.sendRedirect(redirectURL);
%>
