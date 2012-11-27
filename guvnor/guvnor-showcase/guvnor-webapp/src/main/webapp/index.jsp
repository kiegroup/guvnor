<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.kie.guvnor.GuvnorShowcase/Guvnor.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
