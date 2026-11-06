<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">新建字典内容</h4>
        </div>
        <form method="post" id="addDictionary_form">
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10" for="bookTypeCode">字典类型编码:</span>
                        <div class="form-group pull-left">
                            <input onchange='checkCode(this.value,"dictionary")' name="dictionaryCode" type="text" class="form-control input-sm"  placeholder="请输入字典类型编码" required data-msg-required="请输入字典类型编码" >
                        </div>
                    </section>
                    <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10" for="bookTypeName">字典类型名称:</span>
                        <div class="form-group pull-left">
                            <input name="dictionaryName" type="text" class="form-control input-sm"  placeholder="请输入字典类型名称" required data-msg-required="请输入字典类型名称">
                        </div>
                    </section>
                    <!-- <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10" for="bookTypeName">字典类型状态:</span>
                        <div class="form-group pull-left">
                            <input name="dictionaryState" type="text" class="form-control input-sm"  placeholder="请输入字典类型状态" required digits=true data-msg-digits="必须为整数">
                        </div>
                    </section> -->
                    <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10" for="bookTypeName">字典类型说明:</span>
                        <div class="form-group pull-left">
                            <input name="dictionaryDesc" type="text" class="form-control input-sm"  placeholder="请输入字典类型说明" >
                        </div>
                    </section>
                </div>
                <!--基本信息 end-->
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="submit" class="btn btn-success btn-lg2">提交</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </form>

    </div>
</div>

<script type="text/javascript">
    $(function(){
	  $("#addDictionary_form").validate({
	        submitHandler:function(form){
	        	addDictionary();
	        }    
	    });
    })
</script>
