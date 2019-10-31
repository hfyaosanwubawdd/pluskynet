$(function() {
	//页面加载完成之后执行
	pageInit();
});
function pageInit() {
	//创建jqGrid组件
	jQuery("#list2").jqGrid(
		{
			url : '../LoginAction!dataReport.action', //组件创建完成之后请求数据的url
			datatype : "json", //请求数据返回的类型。可选json,xml,txt
			colNames : [ '范围', '划分名称', '已存储', '已处理', '已导入' ], //jqGrid的列显示名字
			colModel : [ //jqGrid每一列的配置信息。包括名字，索引，宽度,对齐方式.....
				{
					name : 'partType',
					index : 'partType',
					width : 25,
					sortable : false
				},
				{	
					name : 'partName',
					index : 'partName',
					width : 30,
					sortable : false
				},
				{
					name : 'total',
					index : 'total',
					width : 55,
					sortable : false
				},
				{
					name : 'handle',
					index : 'handle',
					width : 55,
					sortable : false
				},
				{
					name : 'finish',
					index : 'finish',
					width : 55,
					sortable : false
				}
			],
			rowNum : 20, //一页显示多少条
			/*rowList : [ 10, 20, 30 ],//可供用户选择一页显示多少条*/
			pager : '#pager2', //表格页脚的占位符(一般是div)的id
			mtype : "post", //向后台请求数据的ajax的类型。可选post,get
			loadonce : true,
			scroll : true,
			height : "600",
			width : "1500",
			viewrecords : true,
			multiselect : true,
			jsonReader : {
				root : 'data'
			},
			caption : "大数据信息统计" //表格的标题名字
		});
	/*创建jqGrid的操作按钮容器*/
	/*可以控制界面上增删改查的按钮是否显示*/
	jQuery("#list2").jqGrid('navGrid', '#pager2', {
		edit : false,
		add : false,
		del : false
	});
}