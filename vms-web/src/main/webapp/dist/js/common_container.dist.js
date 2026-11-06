$(function(){
	 
  //处理左侧导航切换效果（可以移除）
  $("#LeftMenuGroup dt").click(function(){
      $(this).toggleClass("on").siblings("dd").toggleClass("on"); 
  });
  $("#LeftMenuGroup dl a").click(function(){
	  var titleName = $(this).text();
      $(this).addClass("on").siblings().removeClass("on");
      $(this).parents("dl").siblings().find("a").removeClass("on");
	  $("#MainHeaderTitle").html(titleName);//更改 Main Title
  });
  
  //table checkbox delete
  $(".main_commontable>table>tfoot .deletebtn").click(function(){
		var chkBox = $(".main_commontable>table>tbody input:checked");
		if(chkBox.length == 0){
			layer.msg("请先选中需要删除的内容",{shade: [0.5, '#fff'],time:2000,});
			return false;
		}else{
			layer.confirm('您确定要删除这'+ chkBox.length +'条内容？', {
			  btn: ['确定','取消'],
			  title: '提示',
			}, function(){
			  chkBox.parents("tr").remove();
			  layer.msg('已删除',{shade: [0.5, '#fff'],time:2000,});
			  tablePlaceholder();
			}, function(index){
			  layer.close(index);
			});
			
		}
  });
  
  
  //bootstrap tooltip
  $("[data-toggle='tooltip']").tooltip();
  
});