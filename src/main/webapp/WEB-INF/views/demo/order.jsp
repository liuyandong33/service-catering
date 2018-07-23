<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2018-07-20
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">
        $(document).on('WeixinJSBridgeReady', function () {
            doPay();
        });

        function doPay() {
            WeixinJSBridge.invoke("getBrandWCPayRequest", {
                    "appId": "${appId}",
                    "timeStamp": "${timeStamp}",
                    "nonceStr": "${nonceStr}",
                    "package": "${prepared}",
                    "signType": "${signType}",
                    "paySign": "${paySign}"
                },
                function (res) {
                    if (res["err_msg"] == "get_brand_wcpay_request:ok") {
                        wx.closeWindow();
                    }
                }
            );
        }
    </script>
</head>
<body>
aaaa
</body>
</html>
