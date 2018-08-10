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
        $(function () {
            changePaymentChannel("1");
        });
        function doRefund() {
            var paymentChannel = $("#payment_channel").val();
            if (paymentChannel == "1") {
                doWeiXinRefund();
            } else if (paymentChannel == "2") {
                doUmPayRefund();
            } else if (paymentChannel == "3") {
                doMiyaRefund();
            } else if (paymentChannel == "4") {
                doNewLandRefund();
            }
        }
        
        function doUmPayRefund() {
            
        }
        
        function doMiyaRefund() {
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

            var orderNumber = $("#order_number").val();
            if (!orderNumber) {
                alert("请输入订单号！");
                return;
            }

            var refundAmount = $("#refund_amount").val();
            if (!refundAmount) {
                alert("请输入退款金额！");
                return;
            }
            if (isNaN(refundAmount)) {
                alert("退款金额必须为数字！");
                return;
            }

            var requestParameters = {tenantId: tenantId, branchId: branchId, orderNumber: orderNumber, refundAmount: refundAmount, paymentChannel: "3"};
            $.post("../demo/doRefund", requestParameters, function (result) {
                if (result["successful"]) {
                    alert(result["message"]);
                } else {
                    alert(result["error"]);
                }
            }, "json");
        }
        
        function doNewLandRefund() {
            
        }

        function doWeiXinRefund() {
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

            var tradeType = $("#trade_type").val();

            var requestParameters = {tenantId: tenantId, branchId: branchId, totalFee: totalFee, refundFee: refundFee, tradeType: tradeType};
            if (transactionId) {
                requestParameters["transactionId"] = transactionId;
            }

            if (outTradeNo) {
                requestParameters["outTradeNo"] = outTradeNo;
            }

            requestParameters["paymentChannel"] = "1";
            $.post("../demo/doRefund", requestParameters, function (result) {
                if (result["successful"]) {
                    alert(result["message"]);
                } else {
                    alert(result["error"]);
                }
            }, "json");
        }

        function handlePaymentChannelChange(obj) {
            var paymentChannel = obj.value;
            changePaymentChannel(paymentChannel);
        }

        var weiXinNodeIds = ["transaction_id", "out_trade_no", "total_fee", "refund_fee", "trade_type"];
        var miyaNodeIds = ["order_number", "refund_amount"];
        var umPayNodeIds = [];
        var newLandNodeIds = [];
        function changePaymentChannel(paymentChannel) {
            if (paymentChannel == "1") {
                hideNodes(miyaNodeIds);
                hideNodes(umPayNodeIds);
                hideNodes(newLandNodeIds);
                showNodes(weiXinNodeIds);
            } else if (paymentChannel == "2") {
                hideNodes(weiXinNodeIds);
                hideNodes(miyaNodeIds);
                hideNodes(newLandNodeIds);
                showNodes(umPayNodeIds);
            } else if (paymentChannel == "3") {
                hideNodes(weiXinNodeIds);
                hideNodes(umPayNodeIds);
                hideNodes(newLandNodeIds);
                showNodes(miyaNodeIds);
            } else if (paymentChannel == "4") {
                hideNodes(weiXinNodeIds);
                hideNodes(miyaNodeIds);
                hideNodes(umPayNodeIds);
                showNodes(newLandNodeIds);
            }
        }
        
        function showNodes(nodeIds) {
            for (var index in nodeIds) {
                $("#" + nodeIds[index]).show();
                $("#" + nodeIds[index]).prev().show();
                $("#" + nodeIds[index]).next().show();
                $("#" + nodeIds[index]).next().next().show();
            }
        }
        
        function hideNodes(nodeIds) {
            for (var index in nodeIds) {
                $("#" + nodeIds[index]).hide();
                $("#" + nodeIds[index]).prev().hide();
                $("#" + nodeIds[index]).next().hide();
                $("#" + nodeIds[index]).next().next().hide();
            }
        }
    </script>
</head>
<body>
<div style="text-align: center">
    <h1>微信退款接口</h1>
    <span>交易类型:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
    <select style="height: 30px;width: 200px;border-radius: 4px;border: 1px solid #00AAEE;" id="payment_channel" onchange="handlePaymentChannelChange(this);">
        <option value="1">官方微信支付</option>
        <%--<option value="2">联动优势</option>--%>
        <option value="3">米雅</option>
        <%--<option value="4">新大陆</option>--%>
    </select>
    <br>
    <br>
    <span>商户ID：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" id="tenant_id" class="common_input">
    <br>
    <br>
    <span>门店ID：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" id="branch_id" class="common_input">
    <br>
    <br>
    <span>微信订单号：&nbsp;&nbsp;&nbsp;</span><input type="text" id="transaction_id" class="common_input">
    <br>
    <br>
    <span>商户订单号：&nbsp;&nbsp;&nbsp;</span><input type="text" id="out_trade_no" class="common_input">
    <br>
    <br>
    <span>订单金额：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" id="total_fee" class="common_input">
    <br>
    <br>
    <span>申请退款金额：</span><input type="text" id="refund_fee" class="common_input">
    <br>
    <br>
    <span>交易类型:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
    <select style="height: 30px;width: 200px;border-radius: 4px;border: 1px solid #00AAEE;" id="trade_type">
        <option value="JSAPI">JSAPI</option>
        <option value="NATIVE">NATIVE</option>
        <option value="APP">APP</option>
        <option value="MWEB">MWEB</option>
        <option value="MICROPAY">MICROPAY</option>
    </select>
    <br>
    <br>
    <span>订单号：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" id="order_number" class="common_input">
    <br>
    <br>
    <span>退款金额：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" id="refund_amount" class="common_input">
    <br>
    <br>
    <input type="button" value="确定" class="common_button" onclick="doRefund();">
</div>
</body>
</html>
