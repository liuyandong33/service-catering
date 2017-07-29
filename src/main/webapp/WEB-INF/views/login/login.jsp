<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017/7/22
  Time: 15:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="custom" uri="http://build.dream.com/custom" %>
<html>
<head>
    <title>登录页面</title>
    <custom:link type="text/css" rel="stylesheet" dir="libraries/artDialog/css" file="dialog.css" base=".."></custom:link>
    <custom:script type="text/javascript" dir="libraries/jquery" file="jquery-3.2.1.min.js" base=".."></custom:script>
    <custom:script type="text/javascript" dir="libraries/artDialog/dist" file="dialog.js" base=".."></custom:script>
    <script type="text/javascript">
        $(function () {
            showDialog("系统提示", "afafa", function () {
                alert(123);
            }, function () {
                alert(456);
            });
        });
        
        function showDialog(title, content, okButtonCallback, cancelButtonCallback, width, height) {
            if (!width) {
                width = 400;
            }
            if (!height) {
                height = 100;
            }
            var d = dialog({
                title: title,
                content: "<div style='height: 100px;width: 400px;overflow: auto'>" + content + "</div>",
                width: width,
                height: height,
                cancel:false,
                button: [
                    {
                        value: "同意",
                        callback: okButtonCallback
                    },
                    {
                        value: "不同意",
                        callback: cancelButtonCallback
                    }
                ]
            });
            d.showModal();
        }
    </script>
</head>
<body>
afafafa
</body>
</html>
