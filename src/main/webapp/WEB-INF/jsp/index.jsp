<%@ page language="java" pageEncoding="UTF-8" import="java.sql.*" %>
<%@ page import="com.mypro.clouddisk.databasecon.SqlConn" %>
<%@ page import="com.mypro.clouddisk.databasecon.User" %>

<%
  // 获取提交的用户名和密码
  String name = request.getParameter("name");
  String password = request.getParameter("password");

  if (name != null && password != null) {
// 在数据库中查找对应的用户信息
    Connection connection = SqlConn.getConnection();
    if (connection != null) {
      User user=new User();
      try {
        user = SqlConn.checkUser(name, password);
      } catch (SQLException e) {
        System.out.println("用户信息核对失败！");
      }

      // 检查用户名和密码是否正确
      if (user != null) {
        // 用户名和密码正确，可以进行跳转到目标页面
        session.setAttribute("name", name);
        response.sendRedirect("home?name="+name);
        return;
      } else {
        // 用户名或密码不正确，显示错误提示
        out.println("用户名或密码不正确");
      }

      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("数据库连接关闭失败！");
      }
    } else {
      out.println("数据库连接失败");
    }
  }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <title>My JSF 'login.jsp' starting page</title>
  <meta http-equiv="pragma" content="no-cache">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="expires" content="0">
  <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
  <meta http-equiv="description" content="This is my page">
  <!-- <link rel="stylesheet" type="text/css" href="styles.css"> -->
  <style type="">  </style>
  <script>
    function skipToUserCloudDisk() {
      var name = document.forms["form"]["name"].value;
      var password = document.forms["form"]["password"].value;
      window.location.href = "home.jsp?name=" + name + "&password=" + password;
    }
  </script>
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
      background-image: url('../../background.jpg'); /* 替换 'your-image-url.jpg' 为您想要使用的图像的URL */
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
</head>
<body>
<form name="form" method="post" class="custom-box">
  <table width="100%" border="0">
    <tbody>
    <tr>
      <td><div style="text-align:center;"><span>用户</span></div></td>
      <td><input type="text" name="name"></td>
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
          <button type="submit" class="btn-login">登录</button>
          <button type="reset" class="btn-reset">重置</button>
        </div>
      </td>
    </tr>
    </tbody>
  </table>
  <div>
    <a href="register">注册</a>
  </div>
</form>
</body>
</html>