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
            var key = 'normal1_1_1_1';
            var value = {
                goodsInfo: {goodsId: 1, goodsName: "黄焖鸡米饭"},
                goodsSpecificationInfo: {goodsSpecificationId: 1, goodsSpecificationName: "小份"},
                flavorInfos: [{flavorGroupId: 1, flavorGroupName: "辣度", flavorId: 1, flavorName: "微辣"}],
                quantity: 10
            };

            var shoppingCartInfo = {};
            var shoppingCartGoodsInfo = shoppingCartInfo[key];
            if (shoppingCartGoodsInfo) {
                shoppingCartGoodsInfo["quantity"] = shoppingCartGoodsInfo["quantity"] + 1;
            } else {
                shoppingCartInfo[key] = value;
            }
            console.log(JSON.stringify(shoppingCartInfo));
        });
    </script>
</head>
<body>
<div id="container" style="width: 800px;height: 400px;margin-left: auto;margin-right: auto;border-radius: 10px;">
</div>
</body>
</html>
