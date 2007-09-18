<%
	org.jboss.seam.Seam.invalidateSession();
    String redirectURL = "JBRMS.html";
    response.sendRedirect(redirectURL);
%>