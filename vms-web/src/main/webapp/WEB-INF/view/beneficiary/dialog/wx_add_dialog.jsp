<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>

    <div class="modal-dialog w600">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">微信收款参数</h4>
            </div>
            <form method="post" action="/mtBeneficiary/saveWxInfo" name="form" id="wx-pay-form" class="unKeyCode">
                <div class="modal-body">
                    <div class="modal_commonbody clearfix">
                        <div class="clearfix">
                            <div class="col-md-12 mb15">
                                <div class="tipsGroup">
                                    <div class="tipsBox">
                                        <p>1、请慎重填写，并认真核对，<em>提交后不支持修改！</em></p>
                                        <p>2、<a href="/mtBeneficiary/wxHelp" target="_blank">点击查看</a> 如何获取微信收款参数</p>
                                        <p>3、账号中需保留充足余额以便自动退款成功</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-12">
                                <div class="commonTable max400">
                                    <table>
                                        <tbody>
                                        <tr>
                                            <td align="right" width="150"><span class="pr10">微信AppID:</span></td>
                                            <td colspan="">
                                                <div class="form-group w200">
                                                    <input class="form-control input-sm w200" name="appId" type="text" placeholder="请填写微信AppID" required>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="right" width="150"><span class="pr10">AppSecret:</span></td>
                                            <td colspan="">
                                                <div class="form-group w200">
                                                    <input class="form-control input-sm w200" name="appSecret" type="text" placeholder="请填写AppSecret" required>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="right" width="150"><span class="pr10">微信支付商户号:</span></td>
                                            <td colspan="">
                                                <div class="form-group w200">
                                                    <input class="form-control input-sm w200" name="mchId" type="text" placeholder="请填写微信支付商户号" required>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="right" width="150"><span class="pr10">商户支付API密钥:</span></td>
                                            <td colspan="">
                                                <div class="form-group w200">
                                                    <input class="form-control input-sm w200" name="mchPwd" type="text" placeholder="请填写商户支付API密钥" required>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="right" width="150"><span class="pr10">API证书(p12格式):</span></td>
                                            <td colspan="">
                                                <div class="form-group w200">
                                                    <input type="file" class="form-control input-sm w200" name="cert" placeholder="选择文件" required>
                                                </div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!--按钮-->
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success btn-lg2">确定</button>
                    <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
                </div>
            </form>
        </div>
    </div>
<script type="text/javascript">
    $(function(){
        Beneficiary.addWxInfo();
    })
</script>