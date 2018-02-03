<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2018/1/26
  Time: 下午10:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="http://webapi.amap.com/maps?v=1.4.3&key=fa275ed9080306f5c9d48d98cbbe1091"></script>
    <script type="text/javascript">
        $(function() {
            var map = new AMap.Map("container", {
                resizeEnable: true,
                zoom: 20,
                center: [116.397428, 39.90923]
            });
        });
    </script>
</head>
<body>
<div id="container" style="width: 800px;height: 400px;margin-left: auto;margin-right: auto;border-radius: 10px;">
</div>
</body>
</html>
