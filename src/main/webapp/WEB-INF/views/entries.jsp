<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ page session="false" %><!DOCTYPE html>
<html>
<head>
	<title>Home</title>
	<link type="text/css" rel="stylesheet" href="/hello-spring/resources/app.css"/>
</head>
<body>
	<h1>Hello world!</h1>
	<form action="/hello-spring/entries" method="get">
		<input type="search" name="q" value="${query}"/>
		<button>Search</button>
	</form>
	<ol>
	<c:forEach var="entry" items="${entries}">
		<li><c:out value="${entry.description}"/></li>
	</c:forEach>
	</ol>
</body>
</html>