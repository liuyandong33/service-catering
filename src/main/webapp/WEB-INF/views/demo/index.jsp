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
        var fullReductionActivities = [];
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

            $.get("../activity/listEffectiveActivities", {tenantId: 1, branchId: 1}, function (result) {
                if (result["successful"]) {
                    var activities = result["data"];
                    var length = activities.length;
                    for (var index = 0; index < length; index++) {
                        var activity = activities[index];
                        var type = activity["type"];
                        if (type == 2) {
                            fullReductionActivities.push(activity);
                        }
                    }
                } else {
                    alert(result["error"]);
                }

                fullReductionActivities.sort(function (x, y) {
                    if (x["totalAmount"] > y["totalAmount"]) {
                        return 1;
                    } else if ((x["totalAmount"] < y["totalAmount"])) {
                        return -1;
                    } else {
                        return 0;
                    }
                });

                var totalAmount = 500;
                var effectiveReductionActivity = undefined;
                var length = fullReductionActivities.length;

                if (length > 0) {
                    if (totalAmount < fullReductionActivities[0]["totalAmount"]) {

                    } else if (totalAmount >= fullReductionActivities[length - 1]["totalAmount"]) {
                        effectiveReductionActivity = fullReductionActivities[length - 1];
                    } else {
                        for (var index = 0; index < length - 1; index++) {
                            if (totalAmount >= fullReductionActivities[index]["totalAmount"] && totalAmount < fullReductionActivities[index + 1]["totalAmount"]) {
                                effectiveReductionActivity = fullReductionActivities[index];
                            }
                        }
                    }
                }

                alert(JSON.stringify(effectiveReductionActivity));
            }, "json");
        });
    </script>
</head>
<body>
<div id="container" style="width: 800px;height: 400px;margin-left: auto;margin-right: auto;border-radius: 10px;">
</div>
</body>
</html>
