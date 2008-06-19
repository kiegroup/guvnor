<%
	org.jboss.seam.Seam.invalidateSession();
    String redirectURL = "Guvnor.html";
    response.sendRedirect(redirectURL);
%>