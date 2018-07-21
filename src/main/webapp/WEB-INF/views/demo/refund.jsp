<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2018/7/21
  Time: 下午11:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style type="text/css">
        .common_input {
            height: 30px;
            width: 200px;
            border-radius: 4px;
            border: 1px solid #00AAEE;
            font-family: Consolas;
            font-size: 16px;
        }
        .common_button {
            height: 30px;
            width: 300px;
            border: none;
            border-radius: 4px;
            background-color: #00AAEE;
            color: #FFFFFF;
            font-family: Consolas;
            font-size: 14px;
            cursor: pointer;
        }
    </style>
    <script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript">
        function doRefund() {
            var tenantId = $("#tenant_id").val();
            if (!tenantId) {
                alert("请输入商户ID！");
                return;
            }

            var branchId = $("#branch_id").val();
            if (!branchId) {
                alert("请输入门店ID！");
                return;
            }

            var transactionId = $("#transaction_id").val();
            var outTradeNo = $("#out_trade_no").val();
            if (!transactionId && !outTradeNo) {
                alert("微信订单号和商户订单号不能同时为空！");
                return;
            }

            var totalFee = $("#total_fee").val();
            if (!totalFee) {
                alert("请输入订单金额！");
                return;
            }
            if (isNaN(totalFee)) {
                alert("订单金额必须为数字！");
                return;
            }

            var refundFee = $("#refund_fee").val();
            if (!refundFee) {
                alert("请输入申请退款金额！");
                return;
            }
            if (isNaN(refundFee)) {
                alert("申请退款金额必须为数字！");
                return;
            }

            var requestParameters = {tenantId: tenantId, branchId: branchId, totalFee: totalFee, refundFee: refundFee};
            if (transactionId) {
                requestParameters["transactionId"] = transactionId;
            }

            if (outTradeNo) {
                requestParameters["outTradeNo"] = outTradeNo;
            }

            $.post("../demo/doRefund", requestParameters, function (result) {
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
<div style="text-align: center">
    <h1>微信退款接口</h1>
    商户ID：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="tenant_id" class="common_input">
    <br>
    <br>
    门店ID：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="branch_id" class="common_input">
    <br>
    <br>
    微信订单号：&nbsp;&nbsp;&nbsp;<input type="text" id="transaction_id" class="common_input">
    <br>
    <br>
    商户订单号：&nbsp;&nbsp;&nbsp;<input type="text" id="out_trade_no" class="common_input">
    <br>
    <br>
    订单金额：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="total_fee" class="common_input">
    <br>
    <br>
    申请退款金额：<input type="text" id="refund_fee" class="common_input">
    <br>
    <br>
    <input type="button" value="确定" class="common_button" onclick="doRefund();">
</div>
</body>
</html>
