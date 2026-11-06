$(function(){
    //配置通用规则
    /*$.validator.setDefaults({
        errorElement:'label', //配置错误提示的节点
        onfocusout: function(element){ //离开焦点时验证
            $(element).valid();
        }
    });*/

	//配置错误提示的节点，默认为label，这里配置成 em
	 $.validator.setDefaults({
		 errorElement:'label',
		 onkeyup:false,
         onclick:false,
		 //onfocusout:false,
		 //focusInvalid:false,
		 errorPlacement: function(error, element){
			 var msg = error.text();
			 //console.log(msg);
			 layer.msg(msg, {shade:[0.6,'#fff'],time:2000});
		 }
	 });

//配置通用的默认提示语
    $.extend($.validator.messages, {
        required: "必填",
        remote: "请修正该字段",
        email: "请输入正确格式的电子邮件",
        url: "请输入合法的网址",
        date: "请输入合法的日期",
        dateISO: "请输入合法的日期 (ISO).",
        number: "请输入合法的数字",
        digits: "只能输入整数",
        creditcard: "请输入合法的信用卡号",
        equalTo: "请再次输入相同的值",
        accept: "请输入拥有合法后缀名的字符串",
        maxlength: $.validator.format("请输入一个长度最多是 {0} 的字符串"),
        minlength: $.validator.format("请输入一个长度最少是 {0} 的字符串"),
        rangelength: $.validator.format("请输入一个长度介于 {0} 和 {1} 之间的字符串"),
        range: $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
        max: $.validator.format("请输入一个最大为 {0} 的值"),
        min: $.validator.format("请输入一个最小为 {0} 的值")
    });


	/*
	 //demo参考
	 $("#DemoValidate").validate({
	 submitHandler: function() {
	 form.submit();//提交表单
	 //验证通过后 的js代码写在这里
	 }
	 });*/

	/*//配置通用的默认提示语   20180105 14:14  备份
	 $.extend($.validator.messages, {
	 required: '必填',
	 equalTo: "请再次输入相同的值",
	 min:$.validator.format( "请输入不小于 {0}的数字" )
	 });*/

	/*//配置通用的默认提示语  20180105 14:15  4.2.0版本避免效验文字显示错位，移除显示
	 $.extend($.validator.messages, {
	 required: ' ',
	 equalTo: " ",
	 min:$.validator.format( "请输入不小于 {0}的数字" )
	 });*/

	/*-------------扩展验证规则-------------*/
//菜单URL menu_url
    jQuery.validator.addMethod("menu_url", function (value, element) {
        //var menu_url = /^\/[a-z.\/]{1,50}$/;
        var menu_url = /^[\/]{1}[a-zA-Z]{1,20}[\/]{1}[a-zA-Z]{1,20}.do$/;
        return this.optional(element) || (menu_url.test(value));
    }, "请正确填写访问路径，例如：/user/save.do");

//菜单代码 menu_code
    jQuery.validator.addMethod("menu_code", function (value, element) {
        //var menu_code = /^menu:[a-z0-9]{1,50}$/;
        var menu_code = /^menu:[A-Za-z0-9:]+$/;
        return this.optional(element) || (menu_code.test(value));
    }, "请正确填写菜单代码，例如：menu:credit");

//模块代码 model_code
    jQuery.validator.addMethod("model_code", function (value, element) {
        //var model_code = /^model:[a-z0-9]{1,50}$/;
        var model_code = /^model:[A-Za-z0-9:]+$/;
        return this.optional(element) || (model_code.test(value));
    }, "请正确填写模块代码，例如：model:credit");

//权限代码 role_code
    jQuery.validator.addMethod("role_code", function (value, element) {
        //var role_code = /^button:[a-z0-9]{1,50}$/;
        var role_code = /^button:[A-Za-z0-9:]+$/;
        return this.optional(element) || (role_code.test(value));
    }, "请正确填写权限代码，例如：button:credit");

//用户名
    jQuery.validator.addMethod("usename", function (value, element) {
        var usename = /^[a-z0-9_-]{3,16}$/;
        return this.optional(element) || (usename.test(value));
    }, "至少2个字符");

// 新用户名
    jQuery.validator.addMethod("newusername", function(value, element) {
        return this.optional(element) || /^[\u0391-\uFFE5\w]+$/.test(value);
    }, "用户名只能包括中文字、英文字母、数字和下划线");
    //正整数
    jQuery.validator.addMethod("di9", function (value, element) {
        var di9 = /^\+?[1-9]\d*$/;
        return this.optional(element) || (di9.test(value));
    }, "只能输入正整数");
    //必须输入大写小写数字，长度为8-20
    jQuery.validator.addMethod("pda9", function (value, element) {
        var pda9 = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,20}$/;
        return this.optional(element) || (pda9.test(value));
    }, "必须包含大写字母、小写字母和数字,且长度为8-20位之间。");
    //中英数  
    jQuery.validator.addMethod("Au9", function (value, element) {
        var Au9 = /^[a-zA-Z0-9\u4e00-\u9fa5]+$/;
        return this.optional(element) || (Au9.test(value));
    }, "只能输入中文、英文或数字");
    //英数
    jQuery.validator.addMethod("A9", function (value, element) {
        var A9 = /^[a-zA-Z0-9]+$/;
        return this.optional(element) || (A9.test(value));
    }, "只能输入英文或数字");
    //英下划线  
    jQuery.validator.addMethod("Aline", function (value, element) {
        var Aline = /^[a-zA-Z_]+$/;
        return this.optional(element) || (Aline.test(value));
    }, "只能输入英文或下划线");
    //邮箱
    jQuery.validator.addMethod("email", function (value, element) {
        var email = /^[a-z0-9._%-]+@([a-z0-9-]+\.)+[a-z]{2,4}$/;
        return this.optional(element) || (email.test(value));
    }, "邮箱格式不对");

    //电话验证规则
    jQuery.validator.addMethod("phone", function (value, element) {
        var phone = /^0\d{2,3}-\d{7,8}$/;
        return this.optional(element) || (phone.test(value));
    }, "电话格式如：0371-68787027");

    //区号验证规则
    jQuery.validator.addMethod("ac", function (value, element) {
        var ac = /^0\d{2,3}$/;
        return this.optional(element) || (ac.test(value));
    }, "区号如：010或0371");

    //无区号电话验证规则
    jQuery.validator.addMethod("noactel", function (value, element) {
        var noactel = /^\d{7,8}$/;
        return this.optional(element) || (noactel.test(value));
    }, "电话格式如：68787027");

    //手机验证规则
    jQuery.validator.addMethod("mobile", function (value, element) {
        var mobile = /^1[3|4|5|7|8]\d{9}$/;
        return this.optional(element) || (mobile.test(value));
    }, "手机格式不对");

    //邮箱或手机验证规则
    jQuery.validator.addMethod("mailPhone", function (value, element) {
        var mailPhone = /^[a-z0-9._%-]+@([a-z0-9-]+\.)+[a-z]{2,4}$|^1[3|4|5|7|8]\d{9}$/;
        return this.optional(element) || (mailPhone.test(value));
    }, "邮箱或手机格式不对");

    //电话或手机验证规则
    jQuery.validator.addMethod("telPhone", function (value, element) {
        var telPhone=/(^1[3|4|5|7|8]\d{9}$)|(^\d{3,4}-\d{7,8}$)|(^\d{7,8}$)|(^\d{3,4}-\d{7,8}-\d{1,4}$)|(^\d{7,8}-\d{1,4}$)/;
        return this.optional(element) || (telPhone.test(value));
    }, "电话或手机格式不对");

    //年龄
    jQuery.validator.addMethod("age", function(value, element) {
        var age = /^(?:[1-9][0-9]?|1[01][0-9]|120)$/;
        return this.optional(element) || (age.test(value));
    }, "不能超过120岁");
    ///// 20-60   /^([2-5]\d)|60$/

    //传真
    jQuery.validator.addMethod("fax",function(value,element){
        var fax = /^(\d{3,4})?[-]?\d{7,8}$/;
        return this.optional(element) || (fax.test(value));
    },"传真格式如：0371-68787027");

    //验证当前值和目标val的值相等 相等返回为 false
    jQuery.validator.addMethod("equalTo2",function(value, element){
        var returnVal = true;
        var id = $(element).attr("data-rule-equalto2");
        var targetVal = $(id).val();
        if(value === targetVal){
            returnVal = false;
        }
        return returnVal;
    },"不能和原始密码相同");

    //大于指定数
    jQuery.validator.addMethod("gt",function(value, element){
        var returnVal = false;
        var gt = $(element).data("gt");
        if(value > gt && value != ""){
            returnVal = true;
        }
        return returnVal;
    },"不能小于0 或空");

    //汉字
    jQuery.validator.addMethod("chinese", function (value, element) {
        var chinese = /^[\u4E00-\u9FFF]+$/;
        return this.optional(element) || (chinese.test(value));
    }, "格式不对");

    //指定数字的整数倍
    jQuery.validator.addMethod("times", function (value, element) {
        var returnVal = true;
        var base=$(element).attr('data-rule-times');
        if(value%base!=0){
            returnVal=false;
        }
        return returnVal;
    }, "必须是发布赏金的整数倍");

    //身份证
    jQuery.validator.addMethod("idCard", function (value, element) {
        var isIDCard1=/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/;//(15位)
        var isIDCard2=/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;//(18位)

        return this.optional(element) || (isIDCard1.test(value)) || (isIDCard2.test(value));
    }, "格式不对");

});