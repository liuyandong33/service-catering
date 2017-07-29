<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017/7/28
  Time: 16:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link type="text/css" rel="stylesheet" href="../libraries/bootstrap-3.3.7/css/bootstrap.css">
    <link type="text/css" rel="stylesheet" href="../libraries/bootstrap-table/css/bootstrap-table.css">
    <script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="../libraries/bootstrap-3.3.7/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../libraries/bootstrap-table/js/bootstrap-table.js"></script>
    <script type="text/javascript" src="../libraries/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script type="text/javascript">
        $(function () {
            var $table = $("#table");
            $table.bootstrapTable({
                url: "../demo/list",
                dataType: "json",
                singleSelect: false,
                striped: true, //是否显示行间隔色
                cache: false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sortable: true, //是否启用排序
                pagination: true,   //显示分页按钮
                sortName: "starttime",
                sortOrder: "desc", //默认排序
                pageNumber: 1, //初始化加载第一页，默认第一页
                pageSize: 10,   //默认显示的每页个数
                pageList: [10, 25, 50, 100],    //可供选择的每页的行数（*）
                queryParamsType: '', //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort // 设置为 '' 在这种情况下传给服务器的参数为：pageSize,pageNumber
                queryParams: function (params) {
                    var temp = {
                        pageSize: params.pageSize,   //页面大小
                        pageNumber: params.pageNumber,  //页码
                        sortName: params.sortName,  //排序列
                        sortOrder: params.sortOrder    //排序方式
                    }
                    return temp;
                },
                responseHandler: function (res) {
                    //动态渲染表格之前获取有后台传递的数据时,用于获取出除去本身渲染所需的数据的额外参数
                    //详见此函数参数的api
                    return res;
                },
                //search: true, 显示搜索框（客户端搜索）
                sidePagination: "server", //服务端处理分页
                //showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
                //cardView: false,                    //是否显示详细视图
                detailView: false,                   //是否显示父子表
                columns: [{
                    //field: 'Number',//可不加
                    title: '序号',//标题  可不加
                    align: 'center',
                    valign: 'middle',
                    width: 60,
                    formatter: function (value, row, index) {
                        return index + 1;
                    }
                }, {
                    title: '备注',
                    field: 'id',
                    align: 'center',
                    width: 100,
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        if (value == "") {
                            value = '-'
                        }
                        var remark = value
                        return remark;
                    }
                }, {
                    title: '操作',
                    field: 'name',
                    align: 'center',
                    width: 120,
                    valign: 'middle'
                }],
                onLoadSuccess: function (data) {  //加载成功时执行
                    $("#theTable th").css("text-align", "center");  //设置表头内容居中
                },
                onLoadError: function () {  //加载失败时执行
                    alert("加载数据失败");
                },
                onAll: function (name, args) {
                },
                onPreBody: function (data) {
                }
            });
        });
    </script>
</head>
<body>
<table id="table"></table>
<thead>
</thead>
</body>
</html>
