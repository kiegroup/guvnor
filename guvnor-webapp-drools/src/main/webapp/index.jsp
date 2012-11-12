<%
	String queryString = request.getQueryString();
    String redirectURL = "org.kie.guvnor.Guvnor/Guvnor.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
