package xworker.manong;

import java.util.ArrayList;
import java.util.List;

public class MaNongProjectPageInfo {
	int totalCount;
	int page;
	int pageSize;
	String keys;
	List<MaNongProjectInfo> datas;
	
	public int getTotalPage(){
		return totalCount / pageSize + (totalCount % pageSize > 0 ? 1 : 0) - 1;
	}
	
	public List<Integer> getPrePages(){
		List<Integer> pages = new ArrayList<Integer>();
		for(int i=1; i<10; i++){
			int prePage = page - i;
			if(prePage >= 0){
				pages.add(0, prePage);
			}else{
				break;
			}
		}
		
		return pages;
	}
	
	public List<Integer> getNextPages(){
		int totalPage = getTotalPage();
		List<Integer> pages = new ArrayList<Integer>();
		for(int i=1; i<10; i++){
			int nextPage = page + i;
			if(nextPage <= totalPage){
				pages.add(nextPage);
			}else{
				break;
			}
		}
		
		return pages;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<MaNongProjectInfo> getDatas() {
		return datas;
	}
	public void setDatas(List<MaNongProjectInfo> datas) {
		this.datas = datas;
	}
	
	public boolean hasNextPage(){
		return (page + 1) * pageSize < totalCount;
	}
	
	public boolean hasPrePage(){
		return page > 0;
	}
	
	public int getNextPage(){
		return page + 1;
	}
	
	public int getPrePage(){
		return page - 1;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}
}
