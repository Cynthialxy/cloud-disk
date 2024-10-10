<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.mypro.clouddisk.databasecon.SqlConn" %>
<%@ page import="com.mypro.clouddisk.databasecon.User" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My JSP 'add.jsp' starting page</title>
    <style>
        /*使表格在容器中水平居中显示*/
        table {
            margin: 0 auto;
        }
        /*设置单元格的内边距位10px*/
        td {
            padding: 10px;
        }
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            padding: 0;
            background-image: url('../../register.jpg'); /* 替换 'your-image-url.jpg' 为您想要使用的图像的URL */
            background-size: cover;
            background-repeat: no-repeat;
            background-attachment: fixed;
            background-position: center;
        }

        form {
            width: 300px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background-color: #fff;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        input[type="text"],
        input[type="password"],
        input[type="reset"],
        input[type="submit"] {
            width: 100%;
            padding: 10px;
            margin: 5px 0;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 16px;
        }
        .button-container {
            justify-content: center; /* 水平居中按钮 */
            display: flex;
            gap: 10px;
        }

        .btn-login,
        .btn-reset{
            background-color: #337ab7; /* 设置背景颜色 */
            color: white; /* 设置文字颜色 */
            padding: 10px 20px; /* 设置内边距 */
            border: none; /* 去除边框 */
            border-radius: 5px; /* 设置圆角 */
            display: inline-block; /* 将链接显示为块级元素 */
        }

        .btn-login:hover,
        .btn-reset:hover {
            background-color: #286090; /* 鼠标悬停时的背景颜色 */
        }

        .btn-login:focus,
        .btn-reset:focus {
            outline: none; /* 移除按钮聚焦时的默认边框 */

        }
        .custom-box {
            width: 25%; /* 设置元素宽度 */
            height: 300px; /* 设置元素高度 */
            background-color: #fff; /* 设置元素背景颜色 */
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
            padding: 20px;
        }

    </style>

    <script>
        function validateForm() {
            var user = document.forms["form"]["user"].value;
            var password = document.forms["form"]["password"].value;

            if (user.trim() === "" || password.trim() === "") {
                alert("用户名、密码不能为空");
                return false;
            }

            return true;
        }

        function submitAccount() {
            if (validateForm()) {
                document.forms["form"].submit();
            }
        }
    </script>
</head>
<body>
    <form method="post" name="form" action="registerto" class="custom-box">
        <table width="100%" border="0">
            <tbody>
            <tr>
                <td><div style="text-align:center;"><span>用户</span></div></td>
                <td><input type="text" name="user"></td>
            </tr>
            <tr>
                <td><div style="text-align:center;"><span>密码</span></div></td>
                <td><input type="password" name="password"></td>
            </tr>
            <br>
            <br>
            <tr>
                <td colspan="2">
                    <div class="button-container">
                        <button type="submit" class="btn-login">确认</button>
                        <button type="reset" class="btn-reset">重置</button>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
        <div>
            <a href="/">返回</a>
        </div>
    </form>
</body>
</html>