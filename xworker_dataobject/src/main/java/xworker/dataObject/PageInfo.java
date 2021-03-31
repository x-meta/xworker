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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;


/**
 * 分页信息，为兼容以前的程序继承HashMap，以前很多程序的分页信息是HashMap。
 * 
 * @author Administrator
 *
 */
public class PageInfo{
	private Map<String, Object> data = null;
	
	public PageInfo(){
		 data = new HashMap<String, Object>();
		 setLimit(100);
		 setPage(0);
	}
	
	public PageInfo(Map<String, Object> data){
		if(data == null) {
			data = new HashMap<String, Object>();
		}
		
		this.data = data; 
	}
	
	public PageInfo(int start, int limit){
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
	
	public int getPage(){
		int start = getStart() + 1;
		int totalCount = getTotalCount();
		int limit = getLimit();
		if(start > totalCount){
			start = totalCount / limit + 1;
		}
		//start = start - 1;
		
		if(start == 0){
			return 1;
		}
		
		return start / limit + (start % limit > 0 ? 1 : 0);
	}
	
	public void setPage(int page){
		if(page < 1){
			page = 1;
		}
		int start = (page - 1) * getLimit(); 
		setStart(start);
		//setLimit(start + getPageSize() - 1);		
	}

	/**
	 * 查询后如果数据对象是新创建的，那么可以通过该方法快速设置。
	 * @param dataObject
	 */
	public void setDataObject(Thing dataObject){
		data.put("dynamicDataObject", dataObject);
	}

	/**
	 * 返回查询新生成的数据对象，如果存在。
	 *
	 * @return
	 */
	public Thing getDataObject(){
		return (Thing) data.get("dynamicDataObject");
	}
	
	public boolean hasPrePage(){
		return getPage() > 1;
	}
	
	public List<Integer> getPrePages(){
		List<Integer> ps = new ArrayList<Integer>();
		int page = getPage();
		
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
	
	public boolean hasNextPage(){
		return getTotalPage() > getPage();
	}
	
	public List<Integer> getNextPages(){
		List<Integer> ps = new ArrayList<Integer>();
		int page = getPage();
		int totalPage = getTotalPage();
		int maxCount = 10 - page;
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
	
	public int getTotalPage(){
		int totalCount = getTotalCount();
		int limit = getLimit();
		
		int totalPage = totalCount /limit + (totalCount % limit > 0 ? 1 : 0); 
		return totalPage;
	}
	public int getStart() {		
		return getInt(DataObjectConstants.PAGEINFO_START);		
	}
	
	public int getPageSize(){
		return getInt(DataObjectConstants.PAGEINFO_PAGESIZE);
	}
	
	public void setPageSize(int pageSize){
		data.put(DataObjectConstants.PAGEINFO_PAGESIZE, pageSize);
		data.put(DataObjectConstants.PAGEINFO_LIMIT, pageSize);
	}

	private int getInt(String name){
		Integer start = (Integer) data.get(name);
		if(start == null){
			return 0;
		}else{
			return start;
		}
	}

	public void setStart(int start) {
		data.put(DataObjectConstants.PAGEINFO_START, start);
	}

	/**
	 * 条目的截止。
	 * 
	 * @return
	 */
	public int getLimit() {
		int limit = getInt(DataObjectConstants.PAGEINFO_LIMIT);
		if(limit <= 0) {
			limit = 100;
		}
		return limit;
	}


	public void setLimit(int limit) {
		data.put(DataObjectConstants.PAGEINFO_LIMIT, limit);
		data.put(DataObjectConstants.PAGEINFO_PAGESIZE, limit);
	}


	@SuppressWarnings("unchecked")
	public List<DataObject> getDatas() {
		return (List<DataObject>) data.get(DataObjectConstants.PAGEINFO_DATAS);
	}

	public Map<String, Object> getPageInfoData(){
		return data;
	}

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


	public int getTotalCount() {
		return getInt(DataObjectConstants.PAGEINFO_TOTALCOUNT);
	}


	public void setTotalCount(int totalCount) {
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
	 * 返回分页信息。
	 * 
	 * @return
	 */
	public List<Page> getPagination(int count){
		List<Page> pages = new ArrayList<Page>();
		int currentPage = this.getPage();
		int totalPage = this.getTotalPage();
		
		//是否有向前翻页
		if(currentPage - count >= 1){
			Page pre = new Page();
			pre.page = currentPage - count;
			pre.isPrePage = true;
		}
		
		
		
		return pages;
	}
	
	public static class Page{
		public int page;
		public boolean isCurrentPage = false;
		public boolean isPrePage = false;
		public boolean isNextPage = false;
	}
}