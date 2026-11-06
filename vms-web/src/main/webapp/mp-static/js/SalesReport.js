$(function () {
    //设置日期默认值
    $("#dateToday").val(todayDate());
    //calendar
    $("#dateToday").calendar({
        onChange: function (p, values, displayValues) {
            if ($("#dateToday").val() == values[0]) {
                return true;
            }
            otherdayReport(values[0]);
        }
    });

    //charts


})

function todayDate() {
    var mydate = new Date();
    var str = "" + mydate.getFullYear() + "-";
    str += (mydate.getMonth() < 9 ? "0" + (mydate.getMonth() + 1) : (mydate.getMonth() + 1)) + "-";
    str += mydate.getDate() < 10 ? "0" + mydate.getDate() : mydate.getDate();
    return str;
}

function otherdayReport(time) {
    $.ajax({
        cache: false,
        type: "POST",
        url: "/mp/slReport/otherdayList",
        data: {"time": time},
        async: false,
        error: function (request) {
            $.toast("通讯异常,请稍后重试");
        },
        success: function (json) {
            var data = eval(json);
            if (data != null && data.type == 'ok' ) {
                $("#vmCount").html('全部'+data.vmCount+"台售货机");
                if (time == todayDate()) {
                    $("#monthSkuSaleListBox, #todayBox, #monthBox, #yesterdayBox").show();
                    $("#otherdayBox").hide();
                    $("#dayAmount1").html(data.dayAmount);
                    $("#dayVolume1").html(data.dayVolume);
                    $("#dayAmount2").html(data.yesterdayAmount);
                    $("#dayVolume2").html(data.yesterdayVolume);
                    $("#totalAmount").html(data.totalAmount);
                    $("#totalVolume").html(data.totalVolume);
                    $('#skuData').empty();
                    $.each(data.skuDataList, function (index, item) {
                        $('#skuData').append('<div class="goodslist updown">' +
                            '<p><span>' + item.skuName + '</span></p>' +
                            '<div>' +
                            '<section>' +
                            '<ul>' +
                            '<li><span class="cf_red saleMoney">' + item.salesAmount + '</span></li>' +
                            '<li><b>' + item.salesVolume + '</b></li>' +
                            '</ul>' +
                            '</section>' +
                            '</div>' +
                            '</div>');
                    });


                } else {
                    $("#otherdayBox").show();
                    $("#monthSkuSaleListBox, #todayBox, #monthBox, #yesterdayBox").hide();
                    $("#dayAmount3").html(data.dayAmount);
                    $("#dayVolume3").html(data.dayVolume);
                }
                charts(data.chart.dayList, data.chart.salesAmountList, data.chart.salesVolumeList);
            } else {
                $.toast(data.message, "text");
            }
        }
    });
}


function monthReport() {
    location.href = "/mp/slReport/monthReport";
}

function charts(dayList, saleMoneyList, saleSizeList) {
    var dayOfMonthChart = echarts.init(document.getElementById('dayOfMonthChart'));
    var option = {
        title: {
            text: '日度销售曲线'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['销售额(元)', '销量(件)'],
            right: 20
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                //saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dayList
        },
        yAxis: {
            type: 'value',
        },
        series: [
            {
                name: '销售额(元)',
                type: 'line',
                stack: '总额',
                lineStyle: {normal: {color: "#95c9b0", width: 3}},
                areaStyle: {normal: {color: "#aed4c2"}},
                data: saleMoneyList
            },
            {
                name: '销量(件)',
                type: 'line',
                stack: '总量',
                lineStyle: {normal: {color: "#61a0a8", width: 3}},
                areaStyle: {normal: {color: "#8db9be"}},
                data: saleSizeList
            }
        ]
    };
    dayOfMonthChart.setOption(option);
}
