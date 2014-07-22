<%
  final String queryString = request.getQueryString();
  final String redirectURL = "org.guvnor.GuvnorWorkbench/GuvnorWorkbench.html" + (queryString == null ? "" : "?" + queryString);
  response.sendRedirect(redirectURL);
%>
