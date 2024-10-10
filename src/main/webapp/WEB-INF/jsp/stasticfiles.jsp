        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="com.mypro.clouddisk.hdfs.FileTypeStats" %>
<jsp:include page="top.jsp"></jsp:include>

<%
    Map<String, FileTypeStats> staticResult = (Map<String, FileTypeStats>) request.getAttribute("staticResult");
    Long totalSize = (long) request.getAttribute("totalSize");
%>
<script type="text/javascript" src="js/echarts.min.js"></script>

<div id="main" class="col-sm-6" style="height:400px;"></div>
<div id="chart2" class="col-sm-6" style="height:400px;"></div>
<div>Total Space: <%=convertBytesToSizeString(totalSize)%></div>

<script type="text/javascript">

    //柱状图
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));
    // 指定图表的配置项和数据
    myChart.title = '用户文件数量统计';

    var xAxisData = [];
    var fileCountData = [];
    var fileSizeDate = [];

    <% for (Map.Entry<String, FileTypeStats> entry : staticResult.entrySet()) { %>
    xAxisData.push('<%=entry.getKey()%>');
    fileCountData.push(<%=entry.getValue().getFileCount()%>);
    fileSizeDate.push(<%=entry.getValue().getTotalSize()%>);
    <% } %>

    var option = {
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',    //即当鼠标悬停在某个数据点上时显示提示框
            axisPointer : {            // 坐标轴指示器，用于指示鼠标悬停位置所对应的数据项
                type : 'shadow'        // 设置坐标轴指示器的类型为 'shadow'，表示坐标轴指示器显示为阴影
            }
        },
        grid: {
            left: '3%',   //设置图表绘制区域距离容器左侧的距离为 3%
            right: '4%',   //设置图表绘制区域距离容器右侧的距离为 4%
            bottom: '3%',   //设置图表绘制区域距离容器底部的距离为 3%
            containLabel: true    //设置网格区域是否包含坐标轴的刻度标签,true为包含
        },
        xAxis: {
            type: 'category', //指定 x 轴的类型为类目轴，用来展示离散的数据。这表示 x 轴上的数据是以分类方式呈现，而不是连续的数值
            data: xAxisData, //x轴上的类目
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            type: 'value'//指定 y 轴的类型为数值轴，用来展示连续的数值数据。这表示 y 轴上的数据是数值型数据，而不是类目型数据
        },
        series: [{
            type: 'bar', //指定了数据系列的类型为柱状图。这表示图表将展示柱状图类型的数据
            barWidth: '60%',
            data: fileCountData,
        }]
    };

    myChart.setOption(option);

    //饼图
    var pieChart = echarts.init(document.getElementById('chart2')); //为指定的 DOM 元素（在这里是 id 为 'chart2' 的元素）创建一个饼图

    var pieData = [];

    //通过entrySet()方法返回一个Map中所有的键值对
    <%Long usedSize = 0L;%>
    <% for (Map.Entry<String, FileTypeStats> entry : staticResult.entrySet()) { %>
    //通过getValue()方法获取FileTypeStats对象
    <%FileTypeStats value = entry.getValue(); %>
    //获取每个FileTypeStats对象的size值
    <%Long size = value.getTotalSize(); %>
    <%usedSize = usedSize + size;%>

    pieData.push({value: <%=size%>, name: '<%=entry.getKey()%> '+ '<%=convertBytesToSizeString(size)%>'});
    <% } %>
    pieData.push({value:<%=totalSize - usedSize%>, name: '剩余空间 '+' <%=convertBytesToSizeString(totalSize - usedSize)%>'});

    var option2 = {
        title : {
            text: '文件统计',
            x:'center'
        },
        tooltip : {    //配置饼图（Pie Chart）中的提示框（tooltip）的显示内容格式
            trigger: 'item',    //指定了当鼠标悬停在图形元素上时触发提示框
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {   //配置图例（legend）
            orient: 'vertical',   //设置图例的布局方向为垂直方向
            left: 'left',   //将图例位于左侧
            data: xAxisData
        },
        series: [{
            type: 'pie',//指定了数据系列的类型为饼图
            radius: '65%',//设置饼图的半径大小为整个图表容器宽高的55%
            center: ['51%', '60%'], //设置饼图的中心位置相对于图表容器的位置，这里中心位置为水平方向50%，垂直方向60%处
            data: pieData,
            itemStyle: { //设置数据项在鼠标悬停时的样式，这里设置了阴影效果
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };

    pieChart.setOption(option2);
    var myChart = echarts.init(document.getElementById('main'));

</script>

<jsp:include page="bottom.jsp"></jsp:include>
        <%!
            public String convertBytesToSizeString(long bytes) {
                if (bytes < 1024) {
                    return bytes + "B";
                } else if (bytes < 1024 * 1024) {
                    return String.format("%.2fKB", (double) bytes / 1024);
                } else if (bytes < 1024 * 1024 * 1024) {
                    return String.format("%.2fMB", (double) bytes / (1024 * 1024));
                } else {
                    return String.format("%.2fGB", (double) bytes / (1024 * 1024 * 1024));
                }
            }
        %>