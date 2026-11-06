<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal-dialog w400">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">新建场景</h4>
        </div>
        <form action="" name="" id="add_form">
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10" for="trading">场景名称:</span>
                        <div class="form-group w160 pull-left">
                            <input class="form-control input-sm" type="text" name="name" placeholder="请输入场景名称" required data-msg-required="请输入场景名称" minlength="2" data-msg-minlength="最少输入2个字符" data-rule-Au9="true">
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
   	$("#add_form").validate({
       submitHandler: function() {
       	add();
       }
    });
</script>
