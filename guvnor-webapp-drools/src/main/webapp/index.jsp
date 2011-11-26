<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.GuvnorDrools/Guvnor.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
