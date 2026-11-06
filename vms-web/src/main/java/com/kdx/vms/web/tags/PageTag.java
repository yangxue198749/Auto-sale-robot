package com.kdx.vms.web.tags;

import java.io.Serializable;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 分页标签处理类
 * @author zhangguodong
 *
 */
@SuppressWarnings("serial")
public class PageTag extends TagSupport  {
	/**
	 * 请求地址
	 */
	private String url;
    private int pageNo = -1;
    private int pageSize = 10;
    private int totalSize = 0;
    private int totalPage = 0;
    private int prev = -1;
    private int next = -1;
    private boolean show;
    private int btnNum = 5;
    private int cutBtnNum;
    private int rest;
    private String id="pagediv";
    
    private boolean setPage = true;
    private String disableClass = "disabled";
    private String currentClass = "active";
    
    @Override
    public int doStartTag() throws JspException {
        if(pageSize <=0 ){
            throw new JspException("pageSize must be more than 0 !");
        }
        if(btnNum<3){
            throw new JspException("btnNum must be more than 3!");
        }
        HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        if(totalSize > 0){
            show = true;
            totalPage = totalSize/pageSize + ((totalSize%pageSize) == 0?0:1);
            pageNo = Math.max(pageNo, 1);
            pageNo = Math.min(pageNo, totalPage);
            prev = pageNo > 1 ? pageNo - 1 : -1;
            next = pageNo < totalPage ? pageNo + 1 : -1;
            url = url != null ? url : request.getRequestURI();
            url = getRequestURL(url, request);
            cutBtnNum = btnNum - 2;
            rest = cutBtnNum%2;
        }else{
        	 show = false;
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        if(show == false){
            return super.doEndTag();
        }
        String html = createHtml();
        // 输出显示分页的代码
        try {
            // 输出分页创建的form
            pageContext.getOut().println(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 表示按照正常的流程继续执行JSP网页
        return EVAL_PAGE;
    }

    private String createHtml(){
        StringBuffer html = new StringBuffer();
        html.append("<div class=\"row\">");
        html.append("<div class=\"col-md-12\">");
       //设置每页显示数量
        html.append("<div class=\"pull-left setpage\">");
        if(setPage) {
        	html.append(createSetPageHtml());
        }else{
        	html.append("<div class=\"mgt15\">");
        	html.append(createPageMsgHtml());
        	html.append("</div>");
        }
        html.append("</div>");
        //分页栏
        html.append("<div class=\"pull-right gopage text-right\">");
        html.append("<ul class=\"pagination mgb\">");
        
        //上一页按钮
        if(prev>0){
            html.append(wrapGoLink(prev, "<"));
        }else{
            html.append("<li class=\""+disableClass+"\"><a href=\"javascript:void(0);\"><</a></li>");
        }
        
        //总页数小于最大按钮数
        if(totalPage<=cutBtnNum){
            for(int i = 1;i<= totalPage;i++){
                if(pageNo == i){
                    html.append("<li class=\""+currentClass+" "+disableClass+"\"><a href=\"javascript:void(0);\" >" + i + "</a></li>");
                }else{
                	html.append(wrapGoLink(i, i));
                }
            }
        }else{
            //中间有省略
            //第一页
        	if(totalPage ==4){
        		 for(int i = 1;i<= totalPage;i++){
                     if(pageNo == i){
                         html.append("<li class=\""+currentClass+" "+disableClass+"\"><a href=\"javascript:void(0);\" >" + i + "</a></li>");
                     }else{
                     	html.append(wrapGoLink(i, i));
                     }
                 }
        	}else{
        		
        		if(pageNo == 1){
        			html.append("<li class=\""+ currentClass +" "+disableClass +"\"><a href=\"javascript:void(0);\">1</a></li>");
        		}else{
        			html.append(wrapGoLink(1, 1));
        		}
        		int harf = cutBtnNum/2;
        		//中间
        		if(pageNo>harf + cutBtnNum%2 + 1){
        			html.append("<li class=\""+disableClass+"\"><a href=\"javascript:void(0);\">...</a></li>");
        		}
        		int start = pageNo - harf;
        		int end = pageNo + harf;
        		if(pageNo - harf<2){
        			start = 2;
        			end = start + cutBtnNum - rest;
        		}
        		if(pageNo + harf>totalPage -1){
        			end = totalPage - 1;
        			start = end - cutBtnNum + rest;
        		}
        		for(int i = start;i<=end;i++){
        			if(pageNo == i){
        				html.append("<li class=\"" + currentClass +" "+disableClass + "\"><a>" + i + "</a></li>");
        			}else{
        				html.append(wrapGoLink(i, i));
        			}
        		}
        		if(pageNo<totalPage - harf - cutBtnNum%2){
        			html.append("<li class=\""+disableClass+"\"><a href=\"javascript:void(0);\">...</a></li>");
        		}
        		//最后一页
        		if(pageNo == totalPage){
        			html.append("<li class=\"" + currentClass +" "+disableClass + "\"><a href=\"javascript:void(0);\">" + totalPage + "</a></li>");
        		}else{
        			html.append(wrapGoLink(totalPage, totalPage));
        		}
        	}
        }
        
        //下一页按钮
        if(next>0){
            html.append(wrapGoLink(next, ">"));
        }else{
        	html.append("<li class=\""+disableClass+"\"><a href=\"javascript:void(0);\">></a></li>");
        }
        html.append("</ul>");
        if(setPage) {
        	html.append("<div>");
        	html.append(createPageMsgHtml());
        	html.append("</div>");
        }
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<script> \n");
        html.append(" Metronic.bindPage('"+id+"'); \n");
        html.append("</script>");
        return html.toString();
    }
    
    private String wrapGoLink(int pageNo, Serializable text){
        return new StringBuilder().append("<li><a href=\""+url+"\" id=\""+pageNo+"\">" + text + "</a></li>").toString();
    }

    private String createSetPageHtml(){
    	 StringBuffer html = new StringBuffer();
    	 
    	 html.append("<div class=\"pull-left mgt15\">每页数量：</div><ul class=\"pagination mgb\">");
         html.append(createPageSize(10));
         html.append(createPageSize(20));
         html.append(createPageSize(40));
         html.append(createPageSize(60));
         html.append(createPageSize(80));
         html.append(createPageSize(100));
         html.append("</ul>");
         return html.toString();
    }
    
    private String createPageMsgHtml(){
         return "总共" + totalSize + "个记录&nbsp; <span>每页" + pageSize + "条&nbsp;当前" + pageNo + "/" + totalPage + "页</span>";
    }
    
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPrev() {
		return prev;
	}

	public void setPrev(int prev) {
		this.prev = prev;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public int getBtnNum() {
		return btnNum;
	}

	public void setBtnNum(int btnNum) {
		this.btnNum = btnNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCutBtnNum() {
		return cutBtnNum;
	}

	public void setCutBtnNum(int cutBtnNum) {
		this.cutBtnNum = cutBtnNum;
	}

	public int getRest() {
		return rest;
	}

	public void setRest(int rest) {
		this.rest = rest;
	}
	
	public boolean isSetPage() {
		return setPage;
	}

	public void setSetPage(boolean setPage) {
		this.setPage = setPage;
	}
	
	private String createPageSize(int size){
		String html = "";
		if(pageSize==size){
			html = "<li class=\""+disableClass+" "+currentClass+"\"><a href=\"javascript:void(0);\" id=\""+size+"\">"+size+"</a></li>";
        }else{
        	html = "<li><a href=\""+url+"\" id=\""+size+"\">"+size+"</a>";
        }
		return html;
	}
    
	@SuppressWarnings("rawtypes")
	private String getRequestURL(String url, HttpServletRequest request) {
		if (request == null) return "";
		java.util.Enumeration paramterNames = request.getParameterNames();
		int i = 0;
		if (paramterNames != null) {
			while (paramterNames.hasMoreElements()) {
				String name = (String) paramterNames.nextElement();
				if("pageSize".equals(name)||"pageNo".equals(name))
					continue;
				if (i == 0) {
					url = url + "?";
				} else {
					url = url + "&";
				}
				String value = request.getParameter(name);
				if (value == null)  value = "";
				try {
					url = url + name + "=" + URLEncoder.encode(value, "UTF-8") ;
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return url;
	}
	
}
