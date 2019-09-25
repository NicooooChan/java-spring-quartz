package com.quartz.demo.common.persist;

import com.alibaba.fastjson.annotation.JSONType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 分页类
 */
@JSONType(orders={"count","size","orderBy","list","enableCount","enablePage","pageNo","pageSize","totalPage",
		"first","firstPage","firstResult","last","lastPage","maxResults","prev","next","funcName","funcParam","html"})//设置排序规则
public class Page<T> {
	private int pageNo = 1; 	// 当前页码
	private int pageSize = 20; 	// 页面大小，设置为-1表示不进行分页
	private long count = 0;		// 总记录数，设置为-1表示不查询总数
	private long size = 0;		// 当前list的大小
	private int first;		// 首页索引
	private int last;		// 尾页索引
	private int prev;		// 上一页索引
	private int next;		// 下一页索引
	private boolean firstPage;	// 是否是第一页
	private boolean lastPage;	// 是否是最后一页
	private int length = 8;	// 显示页面长度
	private int slider = 1;	// 前后显示页面长度
	private String orderBy = ""; 		// 标准查询有效， 实例： updatedate desc, name asc
	private String funcName = "page"; 	// 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
	private String funcParam = ""; 		// 函数的附加参数，第三个参数值。
	private List<T> list = new ArrayList<T>();	//数据
	
	/**
	 * 默认构造，不分页，不查询总数
	 */
	public Page() {
		this.pageSize = -1;
		this.count = -1;
	}
	/**
	 * 构造方法，默认排序
	 * @param pageNo 第几页
	 * @param pageSize 每页多少条
	 */
	public Page(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	/**
	 * 构造方法
	 * @param pageNo 第几页
	 * @param pageSize 每页多少条
	 * @param orderBy 排序方式，可以为空
	 */
	public Page(int pageNo, int pageSize, String orderBy) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		// 设置排序参数
		if (StringUtils.isNotBlank(orderBy)) {
			this.orderBy = orderBy;
		}
	}
	
	/**
	 * 初始化参数
	 */
	public void initialize() {
		this.size = list.size();
		this.first = 1;
		this.last = (int) (count / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);
		if (this.count % this.pageSize != 0 || this.last == 0) {
			this.last++;
		}
		if (this.last < this.first) {
			this.last = this.first;
		}
		if (this.pageNo <= 1) {
			this.pageNo = this.first;
			this.firstPage = true;
		}
		if (this.pageNo >= this.last) {
			this.pageNo = this.last;
			this.lastPage = true;
		}
		if (this.pageNo < this.last - 1) {
			this.next = this.pageNo + 1;
		} else {
			this.next = this.last;
		}
		if (this.pageNo > 1) {
			this.prev = this.pageNo - 1;
		} else {
			this.prev = this.first;
		}
		if (this.pageNo < this.first) {	// 如果当前页小于首页
			this.pageNo = this.first;
		}
		if (this.pageNo > this.last) {	// 如果当前页大于尾页
			this.pageNo = this.last;
		}
	}

	/**
	 * 默认输出当前分页标签 <div class="page">${page}</div>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"fixed-table-pagination\" style=\"display: block;\">");
		long startIndex = (pageNo - 1) * pageSize + 1;
		long endIndex = pageNo * pageSize <= count ? pageNo * pageSize : count;
		sb.append("<div class=\"pull-left pagination-detail\">");
		sb.append("<span class=\"pagination-info\">显示第 " + startIndex + " 到第 " + endIndex + " 条记录，总共 " + count + " 条记录</span>");
		sb.append("<span class=\"page-list\">每页显示 <span class=\"btn-group dropup\">");
		sb.append("<button type=\"button\" class=\"btn btn-default  btn-outline dropdown-toggle\" data-toggle=\"dropdown\" aria-expanded=\"false\">");
		sb.append("<span class=\"page-size\">" + pageSize + "</span> <span class=\"caret\"></span>");
		sb.append("</button>");
		sb.append("<ul class=\"dropdown-menu\" role=\"menu\">");
		sb.append("<li class=\"" + getSelected(pageSize, 10) + "\"><a href=\"javascript:" + funcName + "(" + pageNo
				+ ",10,'" + funcParam + "');\">10</a></li>");
		sb.append("<li class=\"" + getSelected(pageSize, 25) + "\"><a href=\"javascript:" + funcName + "(" + pageNo
				+ ",25,'" + funcParam + "');\">25</a></li>");
		sb.append("<li class=\"" + getSelected(pageSize, 50) + "\"><a href=\"javascript:" + funcName + "(" + pageNo
				+ ",50,'" + funcParam + "');\">50</a></li>");
		sb.append("<li class=\"" + getSelected(pageSize, 100) + "\"><a href=\"javascript:" + funcName + "(" + pageNo
				+ ",100,'" + funcParam + "');\">100</a></li>");
		sb.append("</ul>");
		sb.append("</span> 条记录</span>");
		sb.append("</div>");
		sb.append("<div class=\"pull-right pagination-roll\">");
		sb.append("<ul class=\"pagination pagination-outline\">");
		if (pageNo == first) {// 如果是首页
			sb.append("<li class=\"paginate_button previous disabled\"><a href=\"javascript:\"><i class=\"fa fa-angle-double-left\"></i></a></li>\n");
			sb.append("<li class=\"paginate_button previous disabled\"><a href=\"javascript:\"><i class=\"fa fa-angle-left\"></i></a></li>\n");
		} else {
			sb.append("<li class=\"paginate_button previous\"><a href=\"javascript:\" onclick=\"" + funcName + "("
					+ first + "," + pageSize + ",'" + funcParam + "');\"><i class=\"fa fa-angle-double-left\"></i></a></li>\n");
			sb.append("<li class=\"paginate_button previous\"><a href=\"javascript:\" onclick=\"" + funcName + "("
					+ prev + "," + pageSize + ",'" + funcParam + "');\"><i class=\"fa fa-angle-left\"></i></a></li>\n");
		}
		int begin = pageNo - (length / 2);
		if (begin < first) {
			begin = first;
		}
		int end = begin + length - 1;
		if (end >= last) {
			end = last;
			begin = end - length + 1;
			if (begin < first) {
				begin = first;
			}
		}
		if (begin > first) {
			int i = 0;
			for (i = first; i < first + slider && i < begin; i++) {
				sb.append("<li class=\"paginate_button \"><a href=\"javascript:\" onclick=\"" + funcName + "(" + i + ","
						+ pageSize + ",'" + funcParam + "');\">" + (i + 1 - first) + "</a></li>\n");
			}
			if (i < begin) {
				sb.append("<li class=\"paginate_button disabled\"><a href=\"javascript:\">...</a></li>\n");
			}
		}
		for (int i = begin; i <= end; i++) {
			if (i == pageNo) {
				sb.append("<li class=\"paginate_button active\"><a href=\"javascript:\">" + (i + 1 - first)
						+ "</a></li>\n");
			} else {
				sb.append("<li class=\"paginate_button \"><a href=\"javascript:\" onclick=\"" + funcName + "(" + i + ","
						+ pageSize + ",'" + funcParam + "');\">" + (i + 1 - first) + "</a></li>\n");
			}
		}
		if (last - end > slider) {
			sb.append("<li class=\"paginate_button disabled\"><a href=\"javascript:\">...</a></li>\n");
			end = last - slider;
		}
		for (int i = end + 1; i <= last; i++) {
			sb.append("<li class=\"paginate_button \"><a href=\"javascript:\" onclick=\"" + funcName + "(" + i + ","
					+ pageSize + ",'" + funcParam + "');\">" + (i + 1 - first) + "</a></li>\n");
		}
		if (pageNo == last) {
			sb.append("<li class=\"paginate_button next disabled\"><a href=\"javascript:\"><i class=\"fa fa-angle-right\"></i></a></li>\n");
			sb.append("<li class=\"paginate_button next disabled\"><a href=\"javascript:\"><i class=\"fa fa-angle-double-right\"></i></a></li>\n");
		} else {
			sb.append("<li class=\"paginate_button next\"><a href=\"javascript:\" onclick=\"" + funcName + "(" + next
					+ "," + pageSize + ",'" + funcParam + "');\">" + "<i class=\"fa fa-angle-right\"></i></a></li>\n");
			sb.append("<li class=\"paginate_button next\"><a href=\"javascript:\" onclick=\"" + funcName + "(" + last
					+ "," + pageSize + ",'" + funcParam + "');\">" + "<i class=\"fa fa-angle-double-right\"></i></a></li>\n");
		}
		sb.append("</ul>");
		sb.append("</div>");
		sb.append("</div>");
		return sb.toString();
	}
	/**
	 * 获取选中的class
	 * @return
	 */
	private String getSelected(int pageNo, int selectedPageNo) {
		if (pageNo == selectedPageNo) {
			// return "selected";
			return "active";
		} else {
			return "";
		}
	}
	/**
	 * 是否启用了分页
	 * @return pageSize != -1
	 */
	public boolean isEnablePage() {
		return this.pageSize != -1;
	}

	/**
	 * 是否进行总数统计
	 * @return count != -1
	 */
	public boolean isEnableCount() {
		return this.count != -1;
	}
	/**
	 * 获取 FirstResult
	 */
	public int getFirstResult() {
		int firstResult = (getPageNo() - 1) * getPageSize();
		return firstResult;
	}
	/**
	 * 获取 MaxResults
	 */
	public int getMaxResults() {
		return getPageSize();
	}
	/**
	 * 获取分页HTML代码
	 * @return
	 */
	public String getHtml() {
		//return toString();
		return "";
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
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
	public int getFirst() {
		return first;
	}
	public int getLast() {
		return last;
	}
	public int getTotalPage() {
		return getLast();
	}
	public boolean isFirstPage() {
		return firstPage;
	}
	public boolean isLastPage() {
		return lastPage;
	}
	public int getPrev() {
		if (isFirstPage()) {
			return pageNo;
		} else {
			return pageNo - 1;
		}
	}
	public int getNext() {
		if (isLastPage()) {
			return pageNo;
		} else {
			return pageNo + 1;
		}
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
		initialize();//计算Page
	}
	/**
	 * 获取查询排序字符串
	 * @return
	 */
	public String getOrderBy() {
		// SQL过滤，防止注入
		String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
				+ "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
		Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		if (sqlPattern.matcher(orderBy).find()) {
			return "";
		}
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public String getFuncParam() {
		return funcParam;
	}
	public void setFuncParam(String funcParam) {
		this.funcParam = funcParam;
	}
	public long getSize() {
		return size;
	}
	
}
