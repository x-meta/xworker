package xworker.app.model.tree.impl;

import java.util.Map;

import xworker.app.model.tree.impl.ThingRegistTreeModelActions.Group;

public class ThingRegistCache {
	private Map<String, Group> datas;
	private long expireTime;
	private long nextExpireTime;
	private long refreshInterval;
	private String thingPath;
	private String registType;
	private String child;
	private long lastLoadTime;
	
	public ThingRegistCache(String thingPath, String registType, String child, long expireTime, long refreshInterval, Map<String, Group> datas){
		this.thingPath = thingPath;
		this.registType = registType;
		this.child = child;
		this.refreshInterval = refreshInterval;
		if(refreshInterval > 0 && refreshInterval < 5000){
			refreshInterval = 5000;
		}
		this.expireTime = expireTime;
		initExpireTime();
		setDatas(datas);
	}
	
	public void setDatas(Map<String, Group> datas){
		this.datas = datas;
		initExpireTime();
		lastLoadTime = System.currentTimeMillis();
	}
	
	public boolean isNeedReload(){
		return refreshInterval > 0 && System.currentTimeMillis() > (refreshInterval + lastLoadTime);
	}
	
	public void initExpireTime(){
		nextExpireTime = System.currentTimeMillis() + expireTime;
	}
	
	public Map<String, Group> getDatas(){
		initExpireTime();
		return datas;
	}
	
	public boolean isExpired(){
		return expireTime > 0 && System.currentTimeMillis() > nextExpireTime;
	}
	
	public long getExpireTime(){
		return nextExpireTime;
	}

	public long getRefreshInterval() {
		return refreshInterval;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public String getThingPath() {
		return thingPath;
	}

	public String getRegistType() {
		return registType;
	}

	public String getChild() {
		return child;
	}	
}
