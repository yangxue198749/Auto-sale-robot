$(function(){
    //设置日期默认值
    function todayDate(){
        var mydate = new Date();
        var str = "" + mydate.getFullYear() + "-";
        str += (mydate.getMonth()+1) + "-";
        str += mydate.getDate();
        return str;
    }
    $("#dateToday").val(todayDate());
    //calendar
    $("#dateToday").calendar({
        //value: ['2016-12-12'],
        onChange: function (p, values, displayValues) {
            console.log(values, displayValues);

        }
    });
})