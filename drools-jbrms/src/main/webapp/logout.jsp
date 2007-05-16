<% 
	org.jboss.seam.Seam.invalidateSession();
    String redirectURL = "index.jsp";
    response.sendRedirect(redirectURL);
%>