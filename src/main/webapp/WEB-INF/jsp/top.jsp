<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="js/bootstrap-3.3.7-dist/css/bootstrap.min.css"/>
<link rel="stylesheet" href="css/navbar-static-top.css">
<link rel="stylesheet" href="css/docs.min.css">
<script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="js/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/spark-md5.min.js"></script>
<title>我的云盘</title>
<style type="text/css">
.long-text {width: 120px; overflow: hidden; text-overflow:ellipsis; white-space: nowrap;}
</style>
</head>
<body>

    <!-- Static navbar -->
    <nav class="navbar navbar-default navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/">大数据网盘</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="./home">主页</a></li>
            <li><a href="users" name="users">用户</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">文件管理 <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="http://localhost:9090/home?path=/recycleBin">回收站</a></li>
                <li><a href="http://localhost:9090/searchFiles?keyWord=">文件检索</a></li>
                <li><a href="stasticFiles">文件统计</a></li>
              </ul>
            </li>
            <li><a href="/">切换用户</a></li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li><a href="/">退出登录</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

<div class="container">