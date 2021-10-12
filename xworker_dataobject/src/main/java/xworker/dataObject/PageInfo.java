/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.dataObject;

import java.util.*;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.UtilData;


/**
 * <p>分页信息，为兼容以前的程序继承HashMap，以前很多程序的分页信息是HashMap。</p>
 *
 * <p>有两种设置分页的方法。一是根据记录的偏移量（start，从0开始)和数量(limit，要大于0）设置。二是根据页数（page，从1开始）和页大小（pageSize，要大于0）
 * 来设置。这两种设置方法可以相互转换，其中Limit==pageSize。</p>
 * @author Administrator
 *
 */
public class PageInfo{
	private Map<String, Object> data = null;
	
	public PageInfo(){
		 data = new HashMap<>();
		 setLimit(0);
		 setPage(0);
	}
	
	public PageInfo(Map<String, Object> data){
		if(data == null) {
			data = new HashMap<>();
			this.data = data;

			setLimit(0);
			setPage(0);
		}else {
			this.data = data;
		}
	}
	
	public PageInfo(long start, long limit){
		this();
		
		setStart(start);
		setLimit(limit);
	}
		
	public static PageInfo getPageInfo(ActionContext actionContext){
		return getPageInfo(actionContext.get("pageInfo"));
	}
	
	@SuppressWarnings("unchecked")
	public static PageInfo getPageInfo(Object pageInfo){
		if(pageInfo instanceof PageInfo){
			return (PageInfo) pageInfo;
		}else if(pageInfo instanceof Map){
			return new PageInfo((Map<String, Object>) pageInfo);
		}
		
		return null;
	}

	/**
	 * 返回当前页数。
	 */
	public long getPage(){
		long start = getStart() + 1;
		long totalCount = getTotalCount();
		long limit = getLimit();
		if(limit <= 0){
			return 1;
		}
		if(start > totalCount){
			start = totalCount / limit + 1;
		}
		//start = start - 1;
		
		if(start == 0){
			return 1;
		}
		
		return start / limit + (start % limit > 0 ? 1 : 0);
	}

	/**
	 * 设置当前页。
	 */
	public void setPage(long page){
		if(page < 1){
			page = 1;
		}
		long start = (page - 1) * getLimit();
		setStart(start);
		//setLimit(start + getPageSize() - 1);		
	}

	/**
	 * 通过偏移量设置当前页。
	 */
	public void setPageByOffset(long offset){
		long pageSize = getPageSize();
		long start = (offset / pageSize) * pageSize;
		setStart(start);
	}

	/**
	 * 如果查询后的结果是动态生成的新的数据对象，那么可以通过该方法快速设置。
	 */
	public void setDataObject(Thing dataObject){
		data.put("dynamicDataObject", dataObject);
	}

	/**
	 * 返回查询新生成的数据对象，如果存在。
	 */
	public Thing getDataObject(){
		return (Thing) data.get("dynamicDataObject");
	}

	/**
	 * 返回是否有前一页。
	 *
	 * @return 如果当前页大于1则返回true
	 */
	public boolean hasPrePage(){
		return getPage() > 1;
	}

	/**
	 * 获取前面的最多5页。
	 */
	public List<Long> getPrePages(){
		List<Long> ps = new ArrayList<>();
		long page = getPage();
		
		while(page > 1){
			if(ps.size() >= 5){
				break;
			}
			
			page = page - 1;
			ps.add(page);
		}
		
		Collections.sort(ps);
		
		return ps;
	}

	/**
	 * 返回是否有下一页。
	 */
	public boolean hasNextPage(){
		return getTotalPage() > getPage();
	}

	/**
	 * 返回后续的最多5个页。
	 */
	public List<Long> getNextPages(){
		List<Long> ps = new ArrayList<>();
		long page = getPage();
		long totalPage = getTotalPage();
		long maxCount = 10 - page;
		if(maxCount < 4){
			maxCount = 4;
		}
		while(page < totalPage){
			if(ps.size() >= maxCount){
				break;
			}
			
			page = page + 1;
			ps.add(page);
		}
		
		return ps;
	}

	/**
	 * 返回总页数。
	 */
	public long getTotalPage(){
		long totalCount = getTotalCount();
		long limit = getLimit();
		if(limit <= 0){
			return 1;
		}

		return totalCount /limit + (totalCount % limit > 0 ? 1 : 0);
	}

	/**
	 * 返回记录的起始。
	 */
	public long getStart() {
		return getLong(DataObjectConstants.PAGEINFO_START);
	}

	/**
	 * 返回页大小。
	 */
	public long getPageSize(){
		return getLong(DataObjectConstants.PAGEINFO_PAGESIZE);
	}

	/**
	 * 设置一页的大小。
	 */
	public void setPageSize(int pageSize){
		if(pageSize < 1){
			pageSize = 1;
		}

		data.put(DataObjectConstants.PAGEINFO_PAGESIZE, pageSize);
		data.put(DataObjectConstants.PAGEINFO_LIMIT, pageSize);
	}

	private long getLong(String name){
		return UtilData.getLong(data.get(name), 0);
	}

	/**
	 * 设置起始偏移量。
	 */
	public void setStart(long start) {
		data.put(DataObjectConstants.PAGEINFO_START, start);
	}

	/**
	 * 返回限制数量，同pageSize。
	 */
	public long getLimit() {
		return getLong(DataObjectConstants.PAGEINFO_LIMIT);
	}

	/**
	 * 设置限制数量，同pageSize。
	 */
	public void setLimit(long limit) {
		data.put(DataObjectConstants.PAGEINFO_LIMIT, limit);
		data.put(DataObjectConstants.PAGEINFO_PAGESIZE, limit);
	}


	/**
	 *返回查询的数据列表。
	 */
	@SuppressWarnings("unchecked")
	public List<DataObject> getDatas() {
		return (List<DataObject>) data.get(DataObjectConstants.PAGEINFO_DATAS);
	}

	/**
	 * 返回PageInfo所属的Map。
	 */
	public Map<String, Object> getPageInfoData(){
		return data;
	}

	/**
	 * 设置查询的结果列表。
	 */
	public void setDatas(List<DataObject> datas) {
		data.put(DataObjectConstants.PAGEINFO_DATAS, datas);
	}

	private boolean getBoolean(String name){
		Object success = data.get(name);
		if(success instanceof String){
			return Boolean.parseBoolean((String) success);
		}else if(success instanceof Boolean){
			return (Boolean) success;
		}else if(boolean.class.isInstance(success)){
			return (Boolean) success;
		}
		
		return false;
	}

	public boolean isSuccess() {
		return getBoolean(DataObjectConstants.PAGEINFO_SUCCESS);
	}


	public void setSuccess(boolean success) {
		data.put(DataObjectConstants.PAGEINFO_SUCCESS, success);
	}


	public String getMsg() {
		return (String) data.get(DataObjectConstants.PAGEINFO_MSG);
	}


	public void setMsg(String msg) {
		data.put(DataObjectConstants.PAGEINFO_MSG, msg);
	}


	public long getTotalCount() {
		return getLong(DataObjectConstants.PAGEINFO_TOTALCOUNT);
	}


	public void setTotalCount(long totalCount) {
		data.put(DataObjectConstants.PAGEINFO_TOTALCOUNT, totalCount);
	}


	public String getSort() {
		return (String) data.get(DataObjectConstants.PAGEINFO_SORT);
	}


	public void setSort(String sort) {
		data.put(DataObjectConstants.PAGEINFO_SORT, sort);
	}


	public String getDir() {
		return (String) data.get(DataObjectConstants.PAGEINFO_DIR);
	}

	public boolean isSortAsc(){
		String dir = getDir();
		if(dir != null){
			return !"desc".equals(dir.toLowerCase(Locale.ROOT));
		}else{
			return true;
		}
	}

	public void setDir(String dir) {
		data.put(DataObjectConstants.PAGEINFO_DIR, dir);		
	}
	
	public Object put(String key, Object value){
		return data.put(key, value);
	}
	
	public Object get(String key){
		return data.get(key);
	}
	
	/**
	 * 返回分页信息。该方法应该还没有写完，不能使用。
	 */
	public List<Page> getPagination(int count){
		List<Page> pages = new ArrayList<Page>();
		long currentPage = this.getPage();
		long totalPage = this.getTotalPage();
		
		//是否有向前翻页
		if(currentPage - count >= 1){
			Page pre = new Page();
			pre.page = currentPage - count;
			pre.isPrePage = true;
		}
		
		
		
		return pages;
	}
	
	public static class Page{
		public long page;
		public boolean isCurrentPage = false;
		public boolean isPrePage = false;
		public boolean isNextPage = false;
	}

	@Override
	public String toString() {
		return "PageInfo{" +
				"start=" + getStart() + ",limit=" + getLimit() + ",totalCount:" + getTotalCount() +
				'}';
	}
}