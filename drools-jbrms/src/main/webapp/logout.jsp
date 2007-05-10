<% 
	org.jboss.seam.Seam.invalidateSession();
    String redirectURL = "org.drools.brms.JBRMS/JBRMS.jsp";
    response.sendRedirect(redirectURL);
%>