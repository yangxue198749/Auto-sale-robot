<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>

<div class="modal-dialog w500">
  <div class="modal-content">
    <form id="addTheme-form" action="/theme/addTheme" method="POST" enctype="multipart/form-data" class="unKeyCode">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title f16">新增皮肤</h4>
      </div>
      <div class="modal-body">
        <!--基本信息 start-->
        <div class="modal_commonbody mt10 clearfix">
          <section class="col-md-12">
            <span class="w100 pull-left text-right mr10">皮肤名称:</span>
            <div class="form-group w240 pull-left">
              <input class="form-control input-sm" name="themeName" id="themeName" type="text" placeholder="请输入皮肤名称">
            </div>
          </section>
          <section class="col-md-12">
            <span class="w100 pull-left text-right mr10">皮肤描述:</span>
            <div class="form-group w240 pull-left">
              <textarea name="themeDesc" id="themeDesc" class="w240 form-control input-sm" type="text" maxlength="50" placeholder="50字以内" style="height:70px"></textarea>
            </div>
          </section>
          <section class="col-md-12">
            <span class="w100 pull-left text-right mr10">皮肤包:</span>
            <div class="form-group w260 pull-left">
              <input type="file" name="themeZip" id="themeZip" accept="application/x-zip-compressed" class="form-control input-sm w240 pull-left" />
              <i class="iconfont f14 ml5 icon-tishi cf_blue pull-left" data-toggle="tooltip" data-placement="left" title="仅支持zip格式文件" data-original-title="仅支持zip格式文件"></i>
            </div>
          </section>
          <section class="col-md-12">
            <span class="w100 pull-left text-right mr10">缩略图:</span>
            <div class="form-group w260 pull-left">
              <input type="file" name="themePreview" id="themePreview" accept="image/png" class="form-control input-sm w240 pull-left" />
              <i class="iconfont f14 ml5 icon-tishi cf_blue pull-left" data-toggle="tooltip" data-placement="left" title="仅支持png格式文件" data-original-title="仅支持png格式文件"></i>
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
    </from>
  </div>
</div>

<script type="text/javascript">
  // 页面加载完成后执行的方法
  $("#addTheme-form").validate({
    submitHandler:function(form){
      //form.submit();
      var themeName = $("#themeName").val();
      var zipFile = $("#themeZip").val();
      var pngFile = $("#themePreview").val();
      if(themeName == ""){
        layer.msg("请输入皮肤名称",{shade: [0.5, '#fff'],time:2000,});
        $("#themeName").focus();
        return false;
      } else if(zipFile == ""){
        layer.msg("请选择要上传的皮肤包",{shade: [0.5, '#fff'],time:2000,});
        return false;
      } else if(pngFile == ""){
        layer.msg("请选择预览图",{shade: [0.5, '#fff'],time:2000,});
        return false;
      } else {
        var type = zipFile.substring(zipFile.lastIndexOf(".") + 1);
        var type2 = pngFile.substring(pngFile.lastIndexOf(".") + 1);
        if(type.toLowerCase() != "zip") {
          layer.msg("皮肤包请选择zip文件！",{shade: [0.5, '#fff'],time:2000,});
          return false;
        } else if (type2.toLowerCase() != "png") {
          layer.msg("缩略图请选择png文件！",{shade: [0.5, '#fff'],time:2000,});
          return false;
        } else {
          $("#addTheme-form .confirmbtn").attr('disabled',true);
          var options = {
            type : 'POST',
            beforeSubmit : function() {},
            success : function(data) {
              $('#addTheme').modal('hide') ;
              if(data.code == "0"){
                layer.msg("新建成功！",{shade: [0.5, '#fff'],time:2000,});
                Theme.searchForm();
              } else {
                $("#addTheme-form .confirmbtn").attr('disabled',false);
                layer.msg(data.message,{shade: [0.5, '#fff'],time:2000,});
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
</script>
