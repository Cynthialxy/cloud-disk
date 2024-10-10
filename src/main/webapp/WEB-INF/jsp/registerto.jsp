<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.mypro.clouddisk.databasecon.SqlConn" %>
<%@ page import="com.mypro.clouddisk.databasecon.User" %>
<%@ page import="java.util.List" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>My JSP 'addto.jsp' starting page</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            padding: 0;
            background-size: cover;
            background-repeat: no-repeat;
            background-attachment: fixed;
            background-position: center;
        }
    </style>
</head>
<body>
<%
    request.setCharacterEncoding("utf-8");
    String user=request.getParameter("user");
    String password=request.getParameter("password");
    SqlConn sqlconn=new SqlConn();
    Connection connection= SqlConn.getConnection();
    if (connection!=null){
        int i=sqlconn.insertIntoUser(user,password);
        if (i==1) {
            out.print("<h>成功添加用户</h>");
            out.print("<a href=/>登录" + "</a>");
        }
        else{
            out.print("<h>该用户名已存在</h>");
            out.print("<a href=/register>重新注册" + "</a>");
        }
    }else {
        out.print("<h1>数据库连接数据失败</h1>");
        out.print("<h1>添加失败</h1>"+"<li><a href=/register>返回" + "</a></li>");
    }
%>
</body>
</html>
