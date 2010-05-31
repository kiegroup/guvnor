<%
	org.jboss.seam.security.Identity.instance().logout();
    String redirectURL = "Guvnor.html";
    response.sendRedirect(redirectURL);
%>