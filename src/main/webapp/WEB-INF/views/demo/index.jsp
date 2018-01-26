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
    <style type="text/css">
        td {text-align: center}
    </style>
    <script type="text/javascript">
        $(function () {
            $.get("../dietOrder/obtainDietOrderInfo", {tenantId: 1, branchId: 1, dietOrderId: 1}, function (result) {
                if (result["successful"]) {
                    var data = result["data"];
                    var groups = data["groups"];
                    var length = groups.length;

                    for (var index = 0; index < length; index++) {
                        var group = groups[index];
                        var details = group["details"];

                        var groupNode = $("#clone-here").clone(true);
                        groupNode.show();
                        groupNode.removeAttr("id");
                        groupNode.find("#group-name").text(group["name"]);

                        var detailsLength = details.length;
                        var dietOrderDetailsHtml = "";
                        for (var detailsIndex = 0; detailsIndex < detailsLength; detailsIndex++) {
                            var detail = details[detailsIndex];
                            var flavors = detail["flavors"];
                            var itemName = detail["goodsName"] + "-" + detail["goodsSpecificationName"];
                            if (flavors) {
                                var flavorNames = [];
                                flavors.forEach(function (item) {
                                    flavorNames.push(item["flavorName"]);
                                });
                                itemName = itemName + "【" + flavorNames.join("，") + "】";
                            }
                            dietOrderDetailsHtml += '<tr><td>' + itemName + '</td><td>' + detail["quantity"].toFixed(1) + '</td><td>' + detail["totalAmount"].toFixed(2) + '</td></tr>';
                        }
                        var dietOrderDetailsNode = groupNode.find("#diet-order-details");
                        dietOrderDetailsNode.html(dietOrderDetailsHtml);
                        dietOrderDetailsNode.removeAttr("id");
                        var bodyNode = $("body");
                        bodyNode.append(groupNode);
                    }
                } else {
                    alert(result["error"]);
                }
            }, "json");
        });
    </script>
</head>
<body>
<div id="clone-here" style="display: none;">
    <div id="group-name" style="background-color: orange;height: 40px;line-height: 40px;"></div>
    <table style="width: 100%;">
        <thead>
        <tr>
            <th>项目名称</th>
            <th>数量</th>
            <th>小计</th>
        </tr>
        </thead>
        <tbody id="diet-order-details">
        </tbody>
    </table>
</div>
</body>
</html>
