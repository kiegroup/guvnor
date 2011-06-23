<%
    org.jboss.seam.security.Identity.instance().logout();
    String redirectURL = "Guvnor.jsp";
    response.sendRedirect(redirectURL);
%>
