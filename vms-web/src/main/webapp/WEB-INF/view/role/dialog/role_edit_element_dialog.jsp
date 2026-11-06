<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.vms.tags" prefix="t"%>
<%@ include file="/WEB-INF/view/include/taglib.jsp"%>
<div class="modal-dialog w400">
    <form method="post" action="${path}/role/editElement" name="form" id="edit-element-form">
      <input type="hidden" name="roleId" value="${roleId}" >
      <input type="hidden" id="elementId" name="elementIds" value="" >
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title f16" id="">角色权限分配</h4>
            </div>
            <div class="modal-body">
                <!--新建角色 start-->
                <div class="modal_commonbody clearfix">
                    <!--填写基本信息-->
                    <div class="step_info">
                      <p><a href="javascript:void(0);" onclick="allOpen();">全部打开</a> | <a href="javascript:void(0);" onclick="allClose();">全部关闭</a></p>
                      <div id="tree" class="ztree" style="margin-top:3px;float:left;"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
              <c:choose>
                <c:when test="${edit == 1}">
                  <button type="button" id="submitElement" class="btn btn-success f12 btn-lg2 mr10" >保存</button>
                </c:when>
              </c:choose>
              <button type="button" class="btn btn-default btn-lg2" data-dismiss="modal">取消</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript">
  <%--var elementListJson = eval('${elementList}');--%>
  var tree;

  $(function(){
      var setting = {check:{enable:true,nocheckInherit:true},view:{selectedMulti:false},
          data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
              tree.checkNode(node, !node.checked, true, true);
              return false;
          }}};
      var zNodes=[
              <c:forEach items="${elementList}" var="elem">{id:"${elem["id"]}", pId:"${not empty elem["pid"]?elem["pid"]:0}", name:"${elem["name"]}"},
          </c:forEach>];
      // 初始化树结构
      tree = $.fn.zTree.init($("#tree"), setting, zNodes);
      // 不选择父节点
      tree.setting.check.chkboxType = { "Y" : "ps", "N" : "s" };
      // 默认选择节点
      var ids = "${elementIds}".split(",");
      for(var i=0; i<ids.length; i++) {
          var node = tree.getNodeByParam("id", ids[i]);
          try{tree.checkNode(node, true, false);}catch(e){}
      }
      // 默认展开全部节点
//      tree.expandAll(true);
  });

  $(function() {
      $("#submitElement").bind("click", function() {
        var ids = [], nodes = tree.getCheckedNodes(true);
        for(var i=0; i<nodes.length; i++) {
          ids.push(nodes[i].id);
        }
        $("#elementId").val(ids);
        var options = {
          success: function(data) {
              $('#edit-element-div').modal('hide');
              layer.msg(data.message, {
                  shade: [0.5, '#fff'],
                  time: 2000,
              });
          }
        };
        $('#edit-element-form').ajaxSubmit(options);
      });
    });

    var allOpen = function () {
       tree.expandAll(true);
    }

    var allClose = function () {
       tree.expandAll(false);
    }
</script>
