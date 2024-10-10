<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.mypro.clouddisk.model.FileIndex" %>

<jsp:include page="top.jsp"></jsp:include>
<script type="text/javascript">
	function getName(){
		var urlParams = new URLSearchParams(window.location.search);
		var name = urlParams.get("name");
	}

	function calMD5(){
		var input = $("#file");
		if(!input[0].files.length){
			alert("请先选择文件。");
			return false;
		}
		var fileReader = new FileReader();
		var file = input[0].files[0];
		fileReader.onload=function(e){
			if(file.size!=e.target.result.byteLength){
				alert("无法读取文件");
			}else{
				var md5code = SparkMD5.ArrayBuffer.hash(e.target.result);
				var fileName = file.name;
				$("input[name='fileMD5']").val(md5code);
				$("input[name='fileName']").val(fileName);
				//启动ajax校验md5是否已存在
				$.ajax({
					url:"checkMD5",
					type:"post",
					data:{"md5code":md5code},
					success:doResult
				});
			}
		};
		fileReader.onerror=function(){
			alert("文件读取失败。");
		};
		fileReader.readAsArrayBuffer(file);
	}
	function doResult(result){
		if(result=="yes"){
			alert("文件已存在");
			$("form[action='uploadSecendly']").submit();
		}else if(result=="no"){
			$("#uploadFileForm").submit();
		}
	}
	function downLoadFile(file){
		window.open("download?file=" + file);
	}
	function setRenameForm(directName,path,isRoot){
		$("input[name='directName']").val(directName);
		$("input[name='path']").val(path);
		$("input[name='isRoot']").val(isRoot);
	}
	function moveFile() {
		var sourcePath = $("input[name='nowpath']").val();
		var targetPath = $("select[name='newPathId']").val();

		// Perform the file moving operation
		$.ajax({
			url: "transferForm",
			type: "post",
			data: { sourcePath: sourcePath, targetPath: targetPath },
			success: function() {
				// Display a confirmation message
				var alertMsg = "File or directory moved successfully!";
				alert(alertMsg);

				// Automatically close the alert and modal after 3 seconds
				setTimeout(function() {
					$('.modal').modal('hide'); // Close the modal
					$(".alert").alert('close'); // Close the alert
				}, 500);
			},
			error: function() {
				// Handle the error scenario
				alert("Failed to move file or directory!");
			}
		});
	}
	function setTransferForm(nowPath,nowFileIndexId){
		$("#nowpath").val(nowPath);
		$("input[name='nowPathId']").val(nowFileIndexId);
		//用ajax从后台获取当前文件或者文件夹 可选的转移目录选项
		$.ajax({
			url:"/getOptionalPath",
			type:"post",
			data:{"path":nowPath},
			success:function(result){
				var str = "";
				for(var i=0;i<result.length;i++){
					var getFileIndexId = result[i];
					var getPath = result[i];
					str += "<option value='"+getFileIndexId+"'>"+getPath+"</option>";
				}
				$("select[name='newPathId']").html(str);
			}
		});
	}
	<%--function searchFile(event){--%>
	<%--	var code = event.keyCode;--%>
	<%--	if(code==13){--%>
	<%--		// var keyWrod = $("#keyword").val();--%>
	<%--		//按照关键词查询文件并且展示--%>
	<%--		window.location="http://localhost:9090/home?path="+<% String filepath = (String) request.getAttribute("path");%>;--%>
	<%--	}--%>
	<%--}--%>
	function handleKeyPress(event) {
		if (event.key === 'Enter') {
			var keyword = document.getElementById('keyword').value;

			// 发送AJAX请求到后端进行文件路径搜索
			$.ajax({
				url: "locationFiles",
				type: "post",
				data: { keyWord: keyword },
				success: function(data) {
					var newPath = data;
					if (newPath !== null && newPath !== '') {
						window.location.href = "home?path=" + newPath;
					} else {
						alert("未找到对应文件，请重新输入文件名");
					}
				},
				error: function() {
					alert("搜索文件路径时出错，请重试");
				}
			});
		}
	}

</script>

<% FileIndex rootDir = (FileIndex)request.getAttribute("rootDir");%>
      <div class="alert alert-info" role="alert">
	    <ol class="breadcrumb col-sm-4" style="padding:0px 0px;" title="<%=rootDir.getPath() %>">
	    <i class="glyphicon glyphicon-home"></i>
<%
String nowPath = rootDir.getPath();
String[] paths = nowPath.split("/");
System.out.println(paths.length);
if(paths!=null && paths.length>0){
for(int i=0;i<paths.length;i++){
	String path = paths[i];
	String passPath = "/";
	for(int j=1;j<=i;j++){
		if(j==1){
			passPath+=paths[j];
		}else{
			passPath+="/" + paths[j];
		}
	}
%>
		  <li><a href="?path=<%=passPath %>"><%=(path==null||path.equals(""))?"根目录":path %></a></li>
<%}
}else{
%>
		  <li><a href="?path=/">根目录</a></li>
<%
}
%>
		</ol>
      	<button class="btn btn-primary btn-xs" data-toggle="modal" data-target="#directoryForm"><i class="glyphicon glyphicon-plus"></i>新增目录</button>
      	<div class="modal fade" id="directoryForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">新增目录</h4>
		      </div>
		      <div class="modal-body">
		        <form action="mkdir" id="saveDirectoryForm" method="post">
		        	<div class="form-group">
		        		<label>目录名称</label>
		        		<input type="text" name="directName" class="form-control">
		        		<input type="hidden" name="parentId" value="<%=rootDir.getFileIndexId() %>">
		        		<input type="hidden" name="parentPath" value="<%=rootDir.getPath() %>">
		        	</div>
		        </form>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		        <button type="button" class="btn btn-primary" onclick="$('#saveDirectoryForm').submit()">保存</button>
		      </div>
		    </div>
		  </div>
		</div>
      	<button class="btn btn-info btn-xs" data-toggle="modal" data-target="#fileUpLoadForm"><i class="glyphicon glyphicon-cloud-upload"></i>上传文件</button>
      	<div class="modal fade" id="fileUpLoadForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">上传文件</h4>
		      </div>
		      <div class="modal-body">
		        <form action="uploadFile" id="uploadFileForm" method="post" enctype="multipart/form-data">
		        	<div class="form-group">
		        		<label>选择文件</label>
		        		<input type="file" id="file" name="file">
		        		<input type="hidden" name="parentId" value="<%=rootDir.getFileIndexId() %>"/>
		        		<input type="hidden" name="parentPath" value="<%=rootDir.getPath() %>"/>
		        	</div>
		        </form>
		      </div>
		      <form action="uploadSecendly" method="post">
		      	<input type="hidden" name="fileMD5">
		      	<input type="hidden" name="fileName">
		      	<input type="hidden" name="parentId" value="<%=rootDir.getFileIndexId() %>">
		      	<input type="hidden" name="parentPath" value="<%=rootDir.getPath() %>">
		      </form>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		        <button type="button" class="btn btn-primary" onclick="calMD5()">上传</button>
		      </div>
		    </div>
		  </div>
		</div>

		  <input type="text" placeholder="输入关键字" id="keyword" onkeypress="handleKeyPress(event)" class="form-control" style="float:right;display:inline;width:20%;height:22px;font-size:12px;">
      	<div class="modal fade" id="renameForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		      </div>
		      <div class="modal-body">
		        <form action="renameForm" method="post" id="renameFormo">
		        	<div class="form-group">
		        		<label>目录名称</label>
		        		<input type="text" name="directName" class="form-control" value="<%=rootDir.getName() %>">
		        		<input type="hidden" name="path" value="<%=rootDir.getPath() %>">
		        		<input type="hidden" name="isRoot" value="yes">
		        	</div>
		        </form>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		        <button type="button" class="btn btn-primary" onclick="$('#renameFormo').submit()">保存</button>
		      </div>
		    </div>
		  </div>
		</div>
      </div>


      <div class="bs-glyphicons" style="padding-bottom:110px;">
	    <ul class="bs-glyphicons-list">
<%
List<FileIndex> fileRoots = (List<FileIndex>)request.getAttribute("rootFiles");
for(FileIndex file:fileRoots){
	if(session.getAttribute("name").equals("root")){
%>
			<li>
			  <a href="?path=<%=file.getPath() %>" title="<%=file.getName() %>">
		          <span style="color:#8a6d3b;" class="glyphicon <%=file.getIsFile().equals("1")?"glyphicon-file":"glyphicon-th-list" %>" aria-hidden="true"></span>
		          <span class="glyphicon-class long-text"><%=file.getName() %></span>
	          </a>
	          <!-- Single button -->
			  <div class="btn-group" role="group" style="margin-top:10px;">
			    <button type="button" class="btn btn-xs btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			             操作
			      <span class="caret"></span>
			    </button>
			    <ul class="dropdown-menu">
			      <li><a href="#" onclick="downLoadFile('<%=file.getPath() %>')">下载</a></li>
			      <li><a href="#" data-toggle="modal" data-target="#renameForm" onclick="setRenameForm('<%=file.getName() %>','<%=file.getPath() %>','no')">重命名</a></li>
				  <li><a href="#" data-toggle="modal" data-target="#transferDialog" onclick="setTransferForm('<%=file.getPath() %>','<%=file.getFileIndexId() %>')">转移</a></li>
			      <li><a href="delete?path=<%=file.getPath() %>">删除</a></li>
			    </ul>
			  </div>
	        </li>
     <%}
	 else if(file.getOwner().equals((session.getAttribute("name")))){
	 %>
		<li>
			  <a href="?path=<%=file.getPath() %>" title="<%=file.getName() %>">
			<span style="color:#8a6d3b;" class="glyphicon <%=file.getIsFile().equals("1")?"glyphicon-file":"glyphicon-th-list" %>" aria-hidden="true"></span>
			<span class="glyphicon-class long-text"><%=file.getName() %></span>
			</a>
			<!-- Single button -->
			<div class="btn-group" role="group" style="margin-top:10px;">
				<button type="button" class="btn btn-xs btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					操作
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a href="#" onclick="downLoadFile('<%=file.getPath() %>')">下载</a></li>
					<li><a href="#" data-toggle="modal" data-target="#renameForm" onclick="setRenameForm('<%=file.getName() %>','<%=file.getPath() %>','no')">重命名</a></li>
					<li><a href="#" data-toggle="modal" data-target="#transferDialog" onclick="setTransferForm('<%=file.getPath() %>','<%=file.getFileIndexId() %>')">转移</a></li>
					<li><a href="delete?path=<%=file.getPath() %>">删除</a></li>
				</ul>
			</div>
			</li>
			<% } else {
				continue;
			} %>
			<% } %>
	    </ul>
	  </div>
    </div> <!-- /container -->

    <div class="modal fade" id="transferDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title">文件转移</h4>
		      </div>
		      <div class="modal-body">
		        <form action="transferForm" method="post" id="transferForm">
		        	<div class="form-group">
		        		<label>当前位置：</label>
		        		<input class="form-control" type="text" disabled="disabled" id="nowpath" name="nowpath">
		        		<input type="hidden" name="nowPathId">
		        		<label>转移到目录：</label>
		        		<select name="newPathId" class="form-control">
		        			<option value="2">/</option>
		        			<option value="3">/abc</option>
		        			<option value="4">/abc/cc</option>
		        		</select>
		        	</div>
		        </form>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
		        <button type="button" class="btn btn-primary" onclick="moveFile()">保存</button>
		      </div>
		    </div>
		  </div>
	</div>
<jsp:include page="bottom.jsp"></jsp:include>