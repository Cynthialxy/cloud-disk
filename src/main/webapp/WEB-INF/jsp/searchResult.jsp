<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.mypro.clouddisk.model.FileIndex,com.github.pagehelper.Page" %>
<jsp:include page="top.jsp"></jsp:include>

<div class="panel panel-warning">
  <div class="panel-heading">
    <form class="form-inline" action="searchFiles" method="post">
    	<label>查询条件：文件名称</label>
    	<input class="form-control" name="keyWord" type="text" value="${keyWord}">
    	<input type="hidden" name="pageNum">
    	<button type="submit" class="btn btn-default">搜索</button>
    </form>
  </div>
  <div class="panel-body">
    <table class="table table-bordered table-striped table-hover">
       <thead>
	   <tr>
	       <th>名称</th>
	       <th>路径</th>
	       <th>是否为文件</th>
		   <th>所有者</th>
	   </tr>
	   </thead>
<%
Page<FileIndex> result = (Page<FileIndex>)request.getAttribute("result");

if(result!=null){
	for(FileIndex r:result){
%>
		<tr>
			<td><%=r.getName() %></td>
			<td><%=r.getPath() %></td>
			<td><%=r.getIsFile() %></td>
			<td><%=r.getOwner() %></td>
		</tr>
	</table>
<%
	}
}
%>
	</table>
  </div>
</div>
<jsp:include page="bottom.jsp"></jsp:include>