<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.mypro.clouddisk.model.FileIndex,com.github.pagehelper.Page" %>
<%@ page import="com.mypro.clouddisk.hdfs.FileSystemImpl" %>
<jsp:include page="top.jsp"></jsp:include>

<style>
    .user-container {
        display: flex;
        align-items: center;
    }

    .username {
        margin-right: 50px; /* 设置用户名和按钮之间的右边距 */
    }
</style>

<div class="panel panel-warning">
    <div class="panel-heading">
        <form class="form-inline" method="post">
            <label>用户</label>
        </form>
    </div>
    <div class="panel-body">
        <table class="table table-bordered table-striped table-hover">
            <thead>
            <tr>
                <th>名称</th>
            </tr>
            </thead>
            <%
                List<String> result = (List<String>)request.getAttribute("userFiles");
                if(result!=null){
                    for(String r:result){
            %>
            <tr>
                <td id="<%=r%>">
                    <div class="user-container">
                        <span class="username"><%=r%></span>
                        <%
                            String currentUser = session.getAttribute("name").toString();

                            if (currentUser.equals(r)) { %>
                        <button>当前用户</button>
                    </div>
                    <% } %>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </table>
    </div>
</div>
<%
    FileSystemImpl filesystemimpl=new FileSystemImpl();
%>
<jsp:include page="bottom.jsp"></jsp:include>