<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017/11/11
  Time: 22:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>盘点</title>
    <style type="text/css">
        .bar_code_textarea {width: 100%;height: 80%;font-family: Consolas;font-size: 18px;border-radius: 4px;}
        .button_area {text-align: left}
        .take_stock_button {width: 30%;height: 40px;background-color: #00AAEE;color: #FFFFFF;font-size: 18px;border: none;border-radius: 4px;margin-bottom: 20px;cursor: pointer;}
    </style>
    <script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">
        function uploadDistributionDetailedList() {
            $.post("../goods/uploadDistributionDetailedList", {}, function (result) {
                if (result["successful"]) {
                    alert(result["message"]);
                } else {
                    alert(result["error"]);
                }
            }, "json");
        }
        
        function saveActualDistributionDetailedList() {
            var barCodes = $("#barCodes").val();
            $.post("../goods/saveActualDistributionDetailedList", {barCodes: barCodes}, function (result) {
                if (result["successful"]) {
                    alert(result["message"]);
                } else {
                    alert(result["error"]);
                }
            }, "json");
        }
        
        function doTakeStock() {
            $.post("../goods/doTakeStock", {}, function (result) {
                if (result["successful"]) {
                    alert(result["message"]);
                } else {
                    alert(result["error"]);
                }
            }, "json");
        }
    </script>
</head>
<body>
<div class="button_area">
    <button class="take_stock_button" onclick="uploadDistributionDetailedList();">上传配送数量</button>
    <button class="take_stock_button" onclick="saveActualDistributionDetailedList();">保存</button>
    <button class="take_stock_button" onclick="doTakeStock();">盘点</button>
</div>
<textarea class="bar_code_textarea" id="barCodes"></textarea>
</body>
</html>
