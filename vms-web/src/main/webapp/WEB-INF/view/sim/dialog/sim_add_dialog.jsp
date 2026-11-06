<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<div class="modal-dialog w400">
    <div class="modal-content">
        <form id="addSim-form" action="/sim/addSim" method="POST">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">新建SIM卡</h4>
            </div>
            <div class="modal-body">
                <!--基本信息 start-->
                <div class="modal_commonbody mt10 clearfix">
                    <section class="col-md-12">
                        <span class="w100 pull-left text-right mr10">SIM卡号码:</span>
                        <div class="form-group w160 pull-left">
                            <input class="form-control input-sm" name="addSimPhone" type="text" placeholder="请输入SIM卡号"></div>
                        </section>
                        <section class="col-md-12">
                            <span class="w100 pull-left text-right mr10">SIM卡编号:</span>
                            <div class="form-group w160 pull-left">
                                <input class="form-control input-sm" name="addSimNumber" type="text" placeholder="请输入SIM卡号码"></div>
                            </section>
                            <section class="col-md-12">
                                <span class="w100 pull-left text-right mr10">运营商:</span>
                                <div class="form-group w160 pull-left">
                                    <select class="form-control input-sm" name="addServiceProvider">
                                        <option value="">请选择</option>
                                        <option value="1">移动</option>
                                        <option value="2">联通</option>
                                        <option value="3">电信</option>
                                    </select>
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
                </from>
            </div>
        </div>
        <script type="text/javascript">
            $(function () {
                Sim.addSim();
            })
        </script>
