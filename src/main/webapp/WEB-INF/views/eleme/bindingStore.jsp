<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017-11-29
  Time: 17:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="custom" uri="http://www.groovy.top" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title></title>
    <script type="text/javascript" src="${proxyUrl}?url=${custom:encode(baseUrl.concat('/libraries/jquery/jquery-3.2.1.min.js'))}"></script>
    <style type="text/css">body {
        margin: 0px;
        padding: 0px
    }

    .header {
        background: #20A0FF;
        padding: 15px 0;
        color: #FFFFFF;
        text-align: center;
    }

    #ok_button {
        -webkit-appearance: none;
        -webkit-border-radius: 0;
        background: #20A0FF;
        margin-top: 20px;
        width: 80%;
        height: 40px;
        border-radius: 3px;
        font-size: 16px;
        color: #FFFFFF;
        border: none;
        margin-right: auto;
        cursor: pointer;
    }

    #shopId {
        width: 80%;
        height: 40px;
        font-size: 14px;
        border: 1px solid #DFDFDF;
        border-radius: 3px;
        -webkit-appearance: none;
        padding-left: 10px;
        padding-right: 10px;
        box-sizing: border-box;
    }

    .am-dialog-mask {
        position: absolute;
        display: none;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, .6);
        z-index: 9998
    }

    .am-dialog-mask.show {
        display: block
    }

    .am-dialog {
        display: none;
        position: fixed;
        top: 50%;
        left: 50%;
        -webkit-transform: translateX(-50%) translateY(-50%);
        z-index: 9999;
        text-align: center;
        width: 100%
    }

    .am-dialog.show {
        display: block
    }

    .am-dialog .am-dialog-wrap {
        overflow: hidden;
        padding-top: 22px;
        max-width: 270px;
        margin: 0 auto;
        border-radius: 2px;
        -webkit-background-clip: padding-box;
        background-color: #fff;
        line-height: 21px
    }

    .am-dialog .am-dialog-header {
        -webkit-box-sizing: border-box
    }

    .am-dialog .am-dialog-header h3 {
        font-size: 18px;
        line-height: 21px;
        text-align: center;
        color: #333;
        font-weight: 600;
        padding: 4px 16px 8px
    }

    .am-dialog .am-dialog-header.am-dialog-single-header h3 {
        font-size: 15px;
        color: #333
    }

    .am-dialog .am-dialog-close {
        position: absolute;
        display: block;
        right: 0;
        top: 0;
        height: 48px;
        width: 48px;
        background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAMAAAAM7l6QAAAANlBMVEVMaXGIiIiJiYmJiYn///+IiIiIiIiRkZGJiYmJiYmIiIiZmZmIiIiIiIiLi4uJiYmIiIiIiIif033nAAAAEXRSTlMA3vWjAZv1FdRBcAq0vhZmK7msGmgAAABnSURBVHhe7dEpFoAwEATRTliysIS5/2VReS16xiEp+2XhuQqC2nngshp4262j1Omq2wq6q3Rf6arionRVdaq4qjpVneo71fOUqerJbAkVLRv/eBcW/nEvfPD3/4szPHhXGzj6VPWBF66vCbNK4YfYAAAAAElFTkSuQmCC") center no-repeat;
        background-size: 16px 16px
    }

    .am-dialog .am-dialog-close.hover, .am-dialog .am-dialog-close:active {
        background-color: rgba(37, 39, 40, .05)
    }

    .am-dialog .am-dialog-close.white {
        background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAr0lEQVR4AWL4//+/KBBfAdHuBT4MNMQguxyBeC4QMwJqqQMMCGIgiKKz58119ziDNdRSAF4ZhOiEp+FfQe+38aB3zsnGgCuajfMguKPPPB8Ed7RgwR1t2HFAG3Yc0IYdB7RhxwFt2HFAG2Zc0IYJV7RhxQltWHFCA09x4MisRfoqvqCfpXBzBhWf0AWfUcV3FPEdRXxHEd9RxHcU8R1FfEcRv4I5avj5D46igJ/n/gMWXkng+GnfxwAAAABJRU5ErkJggg==") center no-repeat;
        background-size: 16px 16px
    }

    .am-dialog .am-dialog-img {
        position: relative;
        margin: 0 auto
    }

    .am-dialog .am-dialog-img.fill {
        margin-top: -22px
    }

    .am-dialog .am-dialog-img img {
        display: block;
        margin: 0 auto
    }

    .am-dialog .am-dialog-header + .am-dialog-img {
        padding: 15px 0 0
    }

    .am-dialog .am-dialog-header + .am-dialog-img:before {
        content: " ";
        position: absolute;
        width: 200%;
        height: 200%;
        top: 0;
        left: 0;
        -webkit-transform: scale(.5);
        transform: scale(.5);
        -webkit-transform-origin: 0 0;
        transform-origin: 0 0;
        box-sizing: border-box;
        border-top: 1px solid #E5E5E5
    }

    .am-dialog .am-dialog-button, .am-dialog.image .am-dialog-wrap {
        position: relative
    }

    .am-dialog .am-dialog-body {
        -webkit-box-sizing: border-box;
        padding: 0 16px;
        line-height: 20px
    }

    .am-dialog .am-dialog-body:first-child .am-dialog-brief, .am-dialog .am-dialog-img + .am-dialog-body .am-dialog-brief {
        padding: 4px 6px 0;
        color: #333;
        font-size: 15px
    }

    .am-dialog .am-dialog-brief {
        padding: 0 6px;
        display: block;
        font-size: 14px;
        text-align: center;
        color: #333
    }

    .am-dialog .am-dialog-sline-content {
        min-height: 42px;
        line-height: 42px
    }

    .am-dialog .am-dialog-footer {
        margin-top: 16px;
        display: -webkit-box;
        display: -webkit-flex
    }

    .am-dialog .am-dialog-header + .am-dialog-footer {
        margin-top: 6px
    }

    .am-dialog .am-dialog, .am-dialog .am-dialog-button {
        -webkit-box-flex: 1;
        -webkit-flex: 1;
        display: block;
        width: 100%;
        height: 50px;
        line-height: 25px;
        padding: 12px 0 13px;
        font-size: 18px;
        background: 0 0;
        border: 0;
        outline: 0;
        -webkit-appearance: none;
        color: #999;
        text-align: center;
        box-sizing: border-box;
        border-radius: 0
    }

    .am-dialog .am-dialog-button:first-child, .am-dialog .am-dialog:first-child {
        border-left: 0 none;
        border-bottom-left-radius: 2px
    }

    .am-dialog .am-dialog-button:first-child:before, .am-dialog .am-dialog:first-child:before {
        display: none
    }

    .am-dialog .am-dialog-button:last-child, .am-dialog .am-dialog:last-child {
        border-bottom-right-radius: 2px;
        color: #108ee9
    }

    .am-dialog .am-dialog-button:disabled, .am-dialog .am-dialog:disabled {
        color: #c2c2c2
    }

    .am-dialog .am-dialog-button.hover, .am-dialog .am-dialog-button:active, .am-dialog .am-dialog.hover, .am-dialog .am-dialog:active {
        background-color: rgba(37, 39, 40, .05)
    }

    .am-dialog .selection {
        display: block
    }

    .am-dialog .selection .am-dialog-button {
        display: block;
        border-left: 0 none;
        color: #108ee9
    }

    .am-dialog .selection .am-dialog-button:first-child {
        border-radius: 0
    }

    .am-dialog .selection .am-dialog-button:first-child::before, .am-dialog .selection .am-dialog-button:first-child:before {
        display: none
    }

    .am-dialog .selection .am-dialog-button:last-child {
        border-bottom-left-radius: 7px
    }

    .am-dialog input.am-password-former, .am-dialog input.am-text-former {
        -webkit-box-sizing: border-box;
        display: block;
        width: 100%;
        height: 36px;
        overflow: hidden;
        border: 1px solid #ddd;
        border-radius: 2px;
        -webkit-background-clip: padding-box;
        margin-top: 14px;
        padding: 0 10px;
        -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .4);
        font-size: 14px
    }

    .am-dialog ::-webkit-input-placeholder {
        line-height: 18px
    }

    .am-dialog.simage .am-dialog-img, .am-dialog.simage .am-dialog-img img {
        width: 65px;
        height: 65px
    }

    .am-dialog.simage .am-dialog-brief {
        color: #333;
        font-size: 14px;
        line-height: 16px
    }

    .am-dialog.image .am-dialog-outlink {
        display: block;
        font-size: 15px;
        line-height: 16px;
        text-align: center;
        padding: 6px 12px 0
    }

    .am-dialog.image .am-dialog-img + .am-dialog-header h3, .am-dialog.simage .am-dialog-img + .am-dialog-header h3 {
        padding: 22px 0 8px
    }

    .am-dialog .am-dialog-button:before {
        content: ' ';
        display: block;
        position: absolute;
        left: 0;
        top: 0;
        width: 1px;
        height: 100%;
        background: -webkit-linear-gradient(to left, #ddd, #ddd 33%, transparent 33%) right bottom no-repeat;
        background: linear-gradient(to left, #ddd, #ddd 33%, transparent 33%) right bottom no-repeat
    }

    .am-dialog .am-dialog-footer, .am-dialog .selection .am-dialog-button {
        position: relative
    }

    .am-dialog .am-dialog-footer:before, .am-dialog .selection .am-dialog-button:before {
        content: ' ';
        display: block;
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 1px;
        background: -webkit-linear-gradient(to bottom, #ddd, #ddd 33%, transparent 33%) left top no-repeat;
        background: linear-gradient(to bottom, #ddd, #ddd 33%, transparent 33%) left top no-repeat
    }
    </style>
    <script type="text/javascript">
        var serviceName = "${serviceName}";
        var tenantId = "${tenantId}";
        var branchId = "${branchId}";
        var userId = "${userId}";
        var partitionCode = "${partitionCode}";
        var height = $(window).height();
        var width = $(window).width();

        function handleOkButtonOnClick() {
            $("#ok_button").attr("disabled", true);
            var shopId = $("#shopId").val();
            if (!shopId) {
                alertMessage("提示", "请输入饿了么门店号！", "确定", undefined);
                $("#ok_button").attr("disabled", false);
                return;
            }
            var doBindingStoreUrl = "${doBindingStoreUrl}";
            $.post(doBindingStoreUrl, {
                serviceName: serviceName,
                controllerName: "eleme",
                actionName: "doBindingStore",
                tenantId: tenantId,
                branchId: branchId,
                shopId: shopId,
                userId: userId,
                partitionCode: partitionCode
            }, function (data) {
                debugger
                if (data["successful"]) {
                    alertMessage("提示", data["message"], "确定", undefined);
                    $("#ok_button").attr("disabled", false);
                } else {
                    alertMessage("提示", data["error"]["message"], "确定", undefined);
                    $("#ok_button").attr("disabled", false);
                }
            }, "json");
        }

        function alertMessage(title, content, okValue, okCallback) {
            $("#title").text(title);
            $("#content").text(content);
            $("#ok-button").text(okValue);
            $("#am-dialog").show();
            $("#am-dialog-mask").removeClass("hide");
            $("#am-dialog-mask").addClass("show");
            $("#ok-button").click(function () {
                if (okCallback) {
                    okCallback();
                }
                hideDialog();
            });
        }

        function hideDialog() {
            $("#am-dialog").hide();
            $("#am-dialog-mask").removeClass("show");
            $("#am-dialog-mask").addClass("hide");
        }
    </script>
</head>
<body>
<div class="header">饿了么店铺绑定</div>
<div style="text-align: center;margin-top: 60px;">
    <input type="text" name="shopId" id="shopId" value="" placeholder="请输入饿了么店铺号">
    <input type="button" value="确定" onclick="handleOkButtonOnClick();" id="ok_button">
</div>
<div class="am-dialog-mask hide" id="am-dialog-mask"></div>
<!-- A11Y: 对话框隐藏时设置 aria-hidden="true"，显示时设置 aria-hidden="false" -->
<div id="am-dialog" class="am-dialog show" role="dialog" style="display: none;">
    <div class="am-dialog-wrap">
        <div class="am-dialog-header">
            <h3 id="title"></h3>
        </div>
        <div class="am-dialog-body">
            <p class="am-dialog-brief" id="content"></p>
        </div>
        <div class="am-dialog-footer">
            <button type="button" class="am-dialog-button" id="ok-button"></button>
        </div>
    </div>
</div>
</body>
</html>
