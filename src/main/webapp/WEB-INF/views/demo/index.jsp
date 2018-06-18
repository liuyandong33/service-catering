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
    <script type="text/javascript">
        var fullReductionActivities = [];
        var paymentActivities = [];
        var normalActivities = {};
        var categoryAndGoodsInfos = {};
        var goodsInfos = {};
        var categories = [];
        $(function() {
            $.get("../goods/listGoodses", {tenantId: 1, branchId: 1}, function (result) {
                if (result["successful"]) {
                    var data = result["data"];
                    for (var index in data) {
                        var goodsInfo = data[index];
                        goodsInfos[goodsInfo["id"]] = goodsInfo;

                        var categoryId = goodsInfo["categoryId"];
                        var aa = categoryAndGoodsInfos[categoryId];
                        if (aa) {
                            categoryAndGoodsInfos[categoryId].push(goodsInfo);
                        } else {
                            categoryAndGoodsInfos[categoryId] = [goodsInfo];
                        }
                        categories.push({id: categoryId, name: goodsInfo["categoryName"]});
                    }
                } else {
                    alert(result["error"]);
                }
            }, "json");
            $.get("../activity/listEffectiveActivities", {tenantId: 1, branchId: 1}, function (result) {
                if (result["successful"]) {
                    var activities = result["data"];
                    var length = activities.length;
                    for (var index = 0; index < length; index++) {
                        var activity = activities[index];
                        var type = activity["type"];
                        if (type == 2) {
                            fullReductionActivities.push(activity);
                        } else if (type == 4) {
                            paymentActivities.push(activity);
                        } else {
                            normalActivities[activity["goodsId"] + "_" + activity["goodsSpecificationId"]] = activity;
                        }
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

                    paymentActivities.sort(function (x, y) {
                        if (x["totalAmount"] > y["totalAmount"]) {
                            return 1;
                        } else if ((x["totalAmount"] < y["totalAmount"])) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                } else {
                    alert(result["error"]);
                }
            }, "json");
        });
        
        function effectiveReductionActivity(totalAmount) {
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
            return effectiveReductionActivity;
        }

        function addGoods(goodsId) {
            var goodsInfo = goodsInfos[goodsId];
            var goodsSpecifications = goodsInfo["goodsSpecifications"];
            var flavorGroups = goodsInfo["flavorGroups"];
            if (goodsSpecifications.length > 1 || (flavorGroups && flavorGroups.length > 0)) {
                doAddGoods(goodsInfo, goodsSpecifications[0], undefined);
            } else {
                doAddGoods(goodsInfo, goodsSpecifications[0], undefined);
            }
        }
        
        function doAddGoods(goodsInfo, goodsSpecificationInfo, flavors) {
            var pairs = [];
            for (var index in flavors) {
                var flavor = flavors[index];
                pairs.push(flavor["flavorGroupId"] + ":" + flavor["flavorId"]);
            }

            var key = 'normal_' + goodsInfo["id"] + "_" + goodsSpecificationInfo["id"] + "_" + pairs.join("_");

            var shoppingCartInfo = {};
            var shoppingCartGoodsInfo = shoppingCartInfo[key];
            if (shoppingCartGoodsInfo) {
                shoppingCartGoodsInfo["quantity"] = shoppingCartGoodsInfo["quantity"] + 1;
            } else {
                shoppingCartInfo[key] = {
                    goodsInfo: {goodsId: goodsInfo["id"], goodsName: goodsInfo["name"]},
                    goodsSpecificationInfo: {goodsSpecificationId: goodsSpecificationInfo["id"], goodsSpecificationName: goodsSpecificationInfo["name"]},
                    quantity: 1
                };
                if (flavors) {
                    shoppingCartInfo[key]["flavors"] = flavors;
                }
            }
            console.log(JSON.stringify(shoppingCartInfo));
        }
        
        function subtractGoods(goodsId, goodsSpecificationId, flavors) {
            
        }
    </script>
</head>
<body>
<button onclick="addGoods(1)">aa</button>
</body>
</html>
