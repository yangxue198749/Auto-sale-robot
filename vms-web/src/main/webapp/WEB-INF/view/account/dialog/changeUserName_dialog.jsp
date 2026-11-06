<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
    <form method="post" id="changeUserName_form">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16">修改用户名</h4>
        </div>
        <div class="modal-body">
            <div class="modal_commonbody clearfix">
                <%--<section class="col-md-12">
                    <span class="w100 pull-left mr10 text-right">新用户姓名:</span>
                    <input id="newUserName" class="pull-left form-control input-sm w160" name="userName" type="text" placeholder="请输入新用户姓名" required data-msg-required="请输入公司名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                </section>--%>
                <div class="form-group text-left">
                    <div class="input-group wh100" style="padding: 0 30px;">
                        <span class="input-group-addon f12">新用户姓名</span>
                        <input id="newUserName" type="text" class="form-control f12" name="userName" placeholder="请输入新用户姓名" required data-msg-required="请输入公司名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
                    </div>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <%--<button type="submit" class="btn btn-success btn-lg2">完成</button>--%>
            <button type="button" class="btn btn-success btn-lg2 btnsubmit">完成</button>
            <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
        </div>
    </div>
    </form>
</div>
<script>
	/*
	$(function(){
       	$("#changeUserName_form").validate({
	        submitHandler:function(form){
	        	updateUserName();
	        }    
	    });
	});*/

    $(function () {
        $('#changeUserName_form .btnsubmit').click(function(){
            var newUserName = $('#newUserName').val();
            var ruleAu9 = /^[a-zA-Z0-9\u4e00-\u9fa5]+$/;
            if($.trim(newUserName) == "" || $.trim(newUserName).length < 2){
                layer.msg("新用户姓名至少输入2个字符且不能为空",{shade:[0.6,'#fff'],time:2000});
                return false;
            }
            if(!ruleAu9.test(newUserName)){
                layer.msg("用户姓名只能输入中英文和数字，请重新输入",{shade:[0.6,'#fff'],time:2000});
                return false;
            }else{
                updateUserName();
            }
        })
    });

</script>
