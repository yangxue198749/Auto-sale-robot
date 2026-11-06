<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新增</h4>
        </div>
		<form id="addUpgradePackage-form" class="unKeyCode" action="/upgradePackage/addUpgradePackage" method="POST" enctype="multipart/form-data">
			<div class="modal-body">
				<!--基本信息 start-->
				<div class="modal_commonbody mt10 clearfix">
					<section class="col-md-12">
						<span class="pull-left text-right w80 mr10">程序类型:</span>
						<div class="form-group pull-left">
							<label class="mr10">
								<input type="radio" class="input_v2" name="programType" value="1" checked><span>vmBox</span>
							</label>
							<label class="mr10">
								<input type="radio" class="input_v2" name="programType" value="2"><span>通信板软件</span>
							</label>
							<label>
								<input type="radio" class="input_v2" name="programType" value="3"><span>出货板软件</span>
							</label>
						</div>
					</section>
					<section class="col-md-12">
						<span class="pull-left text-right w80 mr10">版本号:</span>
						<div class="form-group pull-left">
                            <input type="text" class="form-control input-sm w220" placeholder="请输入版本号" id="version" name="version" >
						</div>
					</section>
					<section class="col-md-12">
						<span class="pull-left text-right w80 mr10">程序包:</span>
						<div class="form-group pull-left">
							<div class="btn btn-default btn-block btn-lg2 fileinput fileinput-add-zip w220" id="uploadFileBox">
								<span class="uploadPlaceholder">上传文件</span>
								<input type="file" class="uploadFile uploadFile-add-zip w220 uploadInput" id="upgradePackageZip" name="upgradePackageZip">
							</div>
							<p><span class="ml10">格式:zip</span><span class="ml10">大小:</span></p>
						</div>
					</section>
				</div>
				<!--基本信息 end-->
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn btn-success btn-lg2 confirmbtn">确定</button>
				<button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
			</div>
		</form>
    </div>
</div>

<script type="text/javascript">
  // 页面加载完成后执行的方法
  $("#addUpgradePackage-form").validate({
    submitHandler:function(form){
      //form.submit();
      var version = $("#version").val();
      var zipFile = $("#upgradePackageZip").val();
      if(version == ""){
        layer.msg("请输入版本号",{shade: [0.5, '#fff'],time:2000});
        $("#version").focus();
        return false;
      } else if(zipFile == ""){
        layer.msg("请选择要上传升级包",{shade: [0.5, '#fff'],time:2000});
        return false;
      } else {
        var type = zipFile.substring(zipFile.lastIndexOf(".") + 1);
        if(type.toLowerCase() != "zip") {
          layer.msg("升级包请选择zip文件！",{shade: [0.5, '#fff'],time:2000});
          return false;
      } else {
          var options = {
            type : 'POST',
            beforeSubmit : function() {
                $("#addUpgradePackage-form .confirmbtn").text('提交中').attr('disabled','disabled');
			},
            success : function(data) {
              $('#addUpgradePackage').modal('hide');
              $("#addUpgradePackage-form .confirmbtn").text('确定').removeAttr('disabled');
              if(data.code == "0"){
                layer.msg("新建成功！",{shade: [0.5, '#fff'],time:2000});
                UpgradePackage.searchForm();
              } else {
                layer.msg(data.message,{shade: [0.5, '#fff'],time:2000});
              }
            }
          };
          $(form).ajaxSubmit(options);
        }
      }
    }
  });
    //激活tooltip
    $(function () { $("[data-toggle='tooltip']").tooltip(); });



	//点击上传文件后改变按钮的文字
	$("#uploadFileBox").on("change",".uploadInput",function(){
		var filePath = $(this).val();
		if(filePath.indexOf("zip")!= -1){
			var arrs=filePath.split('\\');
			var filename=arrs[arrs.length-1];
			$("#uploadFileBox .uploadPlaceholder").html(filename);
			console.log($("#uploadFileBox .uploadInput").val()); //打印 type="file" 的值
		}else{
			$("#uploadFileBox .uploadPlaceholder").html("上传文件"); //还原占位文字描述
			$("#uploadFileBox .uploadInput").val(""); //清空 type="file" 的值
			layer.msg('文件格式错误，请重新上传！',{shade:[0.6,'#fff'],time:1500});
		}

	});

</script>
