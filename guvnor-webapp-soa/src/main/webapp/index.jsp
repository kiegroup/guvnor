<%
	String queryString = request.getQueryString();
    String redirectURL = "org.drools.guvnor.GuvnorSOA/GuvnorSOA.jsp?"+(queryString==null?"":queryString);
    response.sendRedirect(redirectURL);
%>
