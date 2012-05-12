<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.Guvnor/Guvnor.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
