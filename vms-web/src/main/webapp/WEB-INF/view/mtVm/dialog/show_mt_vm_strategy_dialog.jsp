<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <div class="modal-dialog w400">
  <div class="modal-content">
      <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title f16" id="">查看策略</h4>
      </div>
      <div class="modal-body">
          <div class="main_commontable max400 border_line">
              <table>
                   <thead>
                       <tr>
              			<td class="wh40">策略类型</td>
                          <td class="wh60">当前策略</td>
                       </tr>
                   </thead>
                   <tbody class="auto">
                       <tr>
              			<td class="wh40">货道策略</td>
                          <td class="wh60">${stChannelName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">闪购键策略</td>
                          <td class="wh60">${stShortcutsName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">价格策略</td>
                          <td class="wh60">${stVmPriceName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">节能策略</td>
                          <td class="wh60">${stEnergySaveName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">商品促销策略</td>
                          <td class="wh60">${skuEventName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">售货机促销策略</td>
                          <td class="wh60">${vmEventName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">走马灯策略</td>
                          <td class="wh60">${stTextName }</td>
                       </tr>
                       <tr>
              			<td class="wh40">皮肤策略</td>
                          <td class="wh60">${stThemeName}</td>
                       </tr>
                       <td class="wh40">版式</td>
                       <td class="wh60">${stThemePlate}</td>
                       </tr>
                   </tbody>
              </table>
          </div>
      </div>
      <!--按钮-->
        <div class="modal-footer">
            <button type="button" class="btn btn-success btn-lg2 confirmbtn" data-dismiss="modal">关闭</button>
        </div>
        
    </div>
</div>