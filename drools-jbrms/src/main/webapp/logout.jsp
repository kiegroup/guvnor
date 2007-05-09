<% 
	org.jboss.seam.Seam.invalidateSession();
    String redirectURL = "org.drools.brms.JBRMS/JBRMS.html";
    response.sendRedirect(redirectURL);
%>