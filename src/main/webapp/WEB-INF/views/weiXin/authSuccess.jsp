<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2018-12-07
  Time: 16:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="custom" uri="http://www.groovy.top" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<img src="${proxyUrl}?url=${custom:encode(baseUrl.concat('/images/weiXin/qrCode.png'))}">
</body>
</html>
