$(function(){
    //datetimepicker
    $('.form_time').datetimepicker({
        language:  'zh-CN',
        format: 'hh:ii',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 1,
        minuteStep: 1,
        minView: 0,
        maxView: 1,
        forceParse: 0
    });

    $('#begin_time').datetimepicker({
        format:'yyyy-mm-dd hh:ii',
        language:  'zh-CN',
        //weekStart: 1,
        //todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        //minView:2,
        forceParse: 0,
        //showMeridian: 1
    }).on("changeDate",function(ev){
        var transferdate=transferDate($("#begin_time").val());//转时间日期
        $('#end_time').datetimepicker('remove');
        $('#end_time').datetimepicker({
            format:'yyyy-mm-dd hh:ii',
            language:  'zh-CN',
            //minView:2,
            autoclose: 1,
            'startDate':transferdate
        }).on("changeDate",function(ev){
            var enddate=$("#end_time").val();
            setEndTime(enddate);
        });
    });
    $('#end_time').datetimepicker({
        format:'yyyy-mm-dd hh:ii',
        language:  'zh-CN',
        minView:2,
        autoclose: 1
    }).on("changeDate",function(ev){
        var enddate=$("#end_time").val();
        setEndTime(enddate);
    });
    function setEndTime(enddate){
        $('#begin_time').datetimepicker('remove');
        $('#begin_time').datetimepicker({
            format:'yyyy-mm-dd hh:ii',
            language:  'zh-CN',
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            'endDate':transferDate(enddate)
        });
    }
    //将时间字符串转为date
    function transferDate(data){
        var start_time=data;
        var newTime= start_time.replace(/-/g,"-");
        var transferdate = new Date(newTime);
        return transferdate;
    }
    function transferTime(str){
        var newstr=str.replace(/-/g,'-');
        var newdate=new Date(newstr);
        var time=newdate.getTime();
        return time;
    }


});
