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
</head>
<body>
<div style="text-align: center">
    <h1>微信退款接口</h1>
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
    <input type="button" value="确定" class="common_button">
</div>
</body>
</html>
