<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017-11-22
  Time: 13:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style type="text/css">
        .title {
            width: 120px;
            display: inline-block;
            height: 40px;
            line-height: 40px;
        }
        .item {
            height:60px;
        }
        .input {
            height: 40px;
            border-radius: 4px;
            width: 200px;
            border: 1px solid #00AAEE;
        }
        .button {
            height: 40px;
            width: 320px;
            background-color: #00AAEE;
            color: #FFFFFF;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
    </style>

    <script type="text/javascript">
        function download() {
            var startMuPrice = document.getElementById("startMuPrice").value;
            if (!startMuPrice) {
                alert("开始亩单价不能为空！")
                return;
            }

            var endMuPrice = document.getElementById("endMuPrice").value;
            if (!endMuPrice) {
                alert("结束亩单价不能为空！")
                return;
            }

            var landScale = document.getElementById("landScale").value;
            if (!landScale) {
                alert("土地规模不能为空！")
                return;
            }

            var originalTotalLandPrice = document.getElementById("originalTotalLandPrice").value;
            if (!originalTotalLandPrice) {
                alert("原土地总价不能为空！")
                return;
            }

            window.location.href = "../calculate/export?startMuPrice=" + startMuPrice + "&endMuPrice=" + endMuPrice + "&landScale=" + landScale + "&originalTotalLandPrice=" + originalTotalLandPrice;
        }
    </script>
</head>
<body>

<div style="width: 100%;height: 100%;text-align: center">
    <div class="item">
        <span class="title">开始亩单价：</span>
        <input class="input" type="text" name="startMuPrice" id="startMuPrice">
    </div>

    <div class="item">
        <span class="title">结束亩单价：</span>
        <input class="input" type="text" name="endMuPrice" id="endMuPrice">
    </div>

    <div class="item">
        <span class="title">土地规模：</span>
        <input class="input" type="text" name="landScale" id="landScale">
    </div>

    <div class="item">
        <span class="title">原土地总价：</span>
        <input class="input" type="text" name="originalTotalLandPrice" id="originalTotalLandPrice">
    </div>

    <button class="button" onclick="download();">下载</button>
</div>

</body>
</html>
