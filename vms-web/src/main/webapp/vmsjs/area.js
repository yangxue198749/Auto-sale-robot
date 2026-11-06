
//table
// 渲染【省】下拉框
function renderProvince(){
    var provinceOption = "<option value=''>请选择</option>";
    $("#provinceSelect").append(provinceOption);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == 1){
            var provinceOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            $("#provinceSelect").append(provinceOption);
        }
    }

}

// 渲染【市】下拉框
function renderCity(provinceId){

    $("#citySelect").empty();
    var citySelect = "<option value=''>请选择</option>";
    $("#citySelect").append(citySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == provinceId){
            var cityOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            $("#citySelect").append(cityOption);
        }
    }
}

// 渲染【县】下拉框
function renderCountry(cityId){

    $("#countrySelect").empty();
    var countrySelect = "<option value=''>请选择</option>";
    $("#countrySelect").append(countrySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == cityId){
            var countryOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            $("#countrySelect").append(countryOption);
        }
    }
}

// 刷新【市】和【县】两级下拉框
function refreshCitySelect(){

    var selectedProvinceId = $("#provinceSelect").val();

    renderCity(selectedProvinceId);

    var selectedCityeId = $("#citySelect").val();

    renderCountry(selectedCityeId);
}

// 刷新【县】下拉框
function refreshCountrySelect(){

    var selectedCityeId = $("#citySelect").val();

    renderCountry(selectedCityeId);
}

//update

// 渲染【省】下拉框
function renderProvinceUpdate(){
    var provinceOption = "<option value=''>请选择</option>";
    $("#provinceAdd").append(provinceOption);
    var checked=$("#provinceAdd").attr('value');
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == 1){
            var provinceOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                provinceOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#provinceAdd").append(provinceOption);
        }
    }

}

// 渲染【市】下拉框
function renderCityUpdate(provinceId){
    var checked=$("#cityAdd").attr('value');
    $("#cityAdd").empty();
    var citySelect = "<option value=''>请选择</option>";
    $("#cityAdd").append(citySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == provinceId){
            var cityOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                cityOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#cityAdd").append(cityOption);
        }
    }
}

// 渲染【县】下拉框
function renderCountryUpdate(cityId){
    var checked=$("#countryAdd").attr('value');
    $("#countryAdd").empty();
    var countrySelect = "<option value=''>请选择</option>";
    $("#countryAdd").append(countrySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == cityId){
            var countryOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                countryOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#countryAdd").append(countryOption);
        }
    }
}

// 刷新【市】和【县】两级下拉框
function refreshCitySelectUpdate(){

    var selectedProvinceId = $("#provinceAdd").val();

    renderCityUpdate(selectedProvinceId);

    var selectedCityeId = $("#cityAdd").val();

    renderCountryUpdate(selectedCityeId);
}

// 刷新【县】下拉框
function refreshCountrySelectUpdate(){

    var selectedCityeId = $("#cityAdd").val();

    renderCountryUpdate(selectedCityeId);
}

//update1

// 渲染【省】下拉框
function renderProvinceUpdate1(){
    var provinceOption = "<option value=''>请选择</option>";
    $("#provinceAdd1").append(provinceOption);
    var checked=$("#provinceAdd1").attr('value');
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == 1){
            var provinceOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                provinceOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#provinceAdd1").append(provinceOption);
        }
    }

}

// 渲染【市】下拉框
function renderCityUpdate1(provinceId){
    var checked=$("#cityAdd1").attr('value');
    $("#cityAdd1").empty();
    var citySelect = "<option value=''>请选择</option>";
    $("#cityAdd1").append(citySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == provinceId){
            var cityOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                cityOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#cityAdd1").append(cityOption);
        }
    }
}

// 渲染【县】下拉框
function renderCountryUpdate1(cityId){
    var checked=$("#countryAdd1").attr('value');
    $("#countryAdd1").empty();
    var countrySelect = "<option value=''>请选择</option>";
    $("#countryAdd1").append(countrySelect);
    for(var i=0; i<areaJson.length; i++){

        if(areaJson[i].pid == cityId){
            var countryOption = "<option value="+areaJson[i].id+">"+areaJson[i].name+"</option>";
            if(areaJson[i].id == checked){
                countryOption = "<option value="+areaJson[i].id+" selected>"+areaJson[i].name+"</option>";
            }
            $("#countryAdd1").append(countryOption);
        }
    }
}

// 刷新【市】和【县】两级下拉框
function refreshCitySelectUpdate1(){

    var selectedProvinceId = $("#provinceAdd1").val();

    renderCityUpdate1(selectedProvinceId);

    var selectedCityeId = $("#cityAdd1").val();

    renderCountryUpdate1(selectedCityeId);
}

// 刷新【县】下拉框
function refreshCountrySelectUpdate1(){

    var selectedCityeId = $("#cityAdd1").val();

    renderCountryUpdate1(selectedCityeId);
}