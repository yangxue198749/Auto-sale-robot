<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<script type="text/javascript" src="/vmsjs/metronic.js"></script>
<div class="modal-dialog w600">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title f16" id="">选择公司</h4>
        </div>
        <div class="modal-body">
            <div class="main_commonhead hl30">
                <form method="post" action="/openApi/companyPage" name="form" id="search-openApi-form" class="unKeyCode">
                    <section>
                        <strong>公司名称:</strong>
                        <div class="form-group w120 pull-left"><input class="form-control input-sm" name="companyName" type="text" placeholder="请输入公司名称"></div>
                    </section>
                    <button  id="search-company-btn" type="button" class="btn btn-default f12 btn-lg2 pull-left mr5">查询</button>
                    <button type="reset" class="btn btn-default f12 btn-lg2 pull-left">重置</button>
                </form>
            </div>
            <div class="clearfix"></div>
            <div id="openApiDiv">
                <jsp:include page="/openApi/companyPage" flush="true"/>
            </div>
            <!--按钮-->
            <div class="modal-footer">
                <button type="button" id="search-openApi-btn" class="btn btn-success btn-lg2">确定</button>
                <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>

        </div>
    </div>
</div>

<script>
    $(function(){
        openApi.selectCompany();
    })
</script>
