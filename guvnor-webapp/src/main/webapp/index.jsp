<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.Guvnor/Guvnor.jsp?"+queryString;
    response.sendRedirect(redirectURL);
%>
