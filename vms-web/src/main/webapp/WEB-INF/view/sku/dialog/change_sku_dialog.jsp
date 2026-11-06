<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">编辑商品</h4>
        </div>
       	<form action="/sku/change.do" id="changeSku_form" method="POST" enctype="multipart/form-data">
	        <div class="modal-body">
	            <!--基本信息 start-->
	            	<input name="skuId" value="${skuId}" type="hidden" >
	                <div class="modal_commonbody mt10 clearfix">
	                    <section class="col-md-12">
	                        <span class="w80 pull-left text-right mr10">商品图片:</span>
							<div class="form-group w180 pull-left">
	                        	<input type="file" name="fileMaster" id="changeFile" accept="image/png" style="width: 180px;" /><span class="cf_red">主图必选</span>
	                        	<input type="file" name="fileSecond" id="changeFileOne" accept="image/png" style="width: 180px;" />
	                        	<input type="file" name="fileThird" id="changeFileTwo" accept="image/png" style="width: 180px;" />
								<input type="hidden" id="imgSize" value="${imgSize}">
								<p>大小200KB以内，格式PNG</p>
							</div>
	                    </section>
						<section class="col-md-12">
							<span class="w80 pull-left text-right mr10">描述图片:</span>
							<div class="form-group w180 pull-left">
								<input type="file" name="fileDesc" id="changeFileThree" accept="image/png" style="width: 180px;" />
								<p>大小500KB以内，格式PNG</p>
							</div>
						</section>
	                    <section class="col-md-12 mt20">
	                        <span class="w80 pull-left text-right mr10">商品描述:</span>
	                        <div class="form-group w180 pull-left">
	                            <textarea name="describe" class="w180 form-control input-sm" type="text" maxlength="75" placeholder="75字以内" style="height: 100px">${skuDesc}</textarea>
	                        </div>
	                    </section>
	                </div>
	                <!--基本信息 end-->
	        </div>
	        <!--按钮-->
	        <div class="modal-footer">
	            <button type="submit" class="btn btn-success btn-lg2 confirmbtn">提交</button>
	            <button type="button" class="btn btn-default btn-lg2 restbtn" data-dismiss="modal">取消</button>
	        </div>
       	</form>
    </div>
</div>

<script type="text/javascript">
	// 页面加载完成后执行的方法
    $("#changeSku_form").validate({
        submitHandler:function(form){
        	//form.submit();
      	    //changeSku();
			var changeFile = $("#changeFile").val();
			var changeFileOne = $("#changeFileOne").val();
			var changeFileTwo = $("#changeFileTwo").val();
			var imgSize = $("#imgSize").val();
			console.log("imgSize=" +imgSize);
			if(null == imgSize && changeFile == "" && changeFileOne == "" && changeFileTwo == ""){
                layer.msg("请至少上传1张商品图片！",{shade: [0.5, '#fff'],time:2000,});
                return false;
			} else {
					var options = {
						type : 'POST',
						beforeSubmit : function() {},
						success : function(data) {
							$('#changeSku').modal('hide') ;
							if(data.code =="0"){
								layer.msg("编辑成功！",{shade: [0.5, '#fff'],time:2000,});
								searchEvent();
							} else {
								layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
							}
						}
					};
					$(form).ajaxSubmit(options);
			}
        }
    });
</script>
