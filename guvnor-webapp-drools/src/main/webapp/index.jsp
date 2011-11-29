<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.GuvnorDrools/GuvnorDrools.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
