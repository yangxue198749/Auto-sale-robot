$(function(){
    //公用时间范围 开始日期-结束日期
    var start = {
        elem: '#timeStart',
        format: 'YYYY/MM/DD',
        //min: laydate.now(), //设定最小日期为当前日期
        //max: '2099/06/16', //最大日期
        //istime: true,
        istoday: false,
        choose: function (datas) {
            end.min = datas; //开始日选好后，重置结束日的最小日期
            end.start = datas; //将结束日的初始值设定为开始日
        }
    };
    var end = {
        elem: '#timeEnd',
        format: 'YYYY/MM/DD',
        //min: laydate.now(+2),
        //max: '2099/06/16',
        //istime: true,
        istoday: false,
        choose: function (datas) {
            start.max = datas; //结束日选好后，重置开始日的最大日期
        }
    };
    laydate(start);
    laydate(end);
	
	//公用date #CommonDate
	laydate({
	  elem: '#CommonDate',
	  format: 'YYYY/MM/DD'
	});
	
})
