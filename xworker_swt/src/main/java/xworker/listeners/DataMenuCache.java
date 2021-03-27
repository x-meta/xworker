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
package xworker.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmeta.Thing;

public class DataMenuCache {
	/** 菜单定义缓存 */
	private List<Thing> menuConfigCache = new ArrayList<Thing>();
	/** 用来存储数据菜单的缓存 */
	private Map<String, List<Thing>> dataCache = new HashMap<String, List<Thing>>();
	/** 用来存储包菜单的缓存 */	
	private Map<String, List<Thing>> packageCache = new HashMap<String, List<Thing>>();
	/** 用来存储结构包菜单的缓存 */
	private Map<String, List<Thing>> structurePackageCache = new HashMap<String, List<Thing>>();
	/** 用来存储数据包菜单的缓存 */
	private Map<String, List<Thing>> dataPackageCache = new HashMap<String, List<Thing>>();
	/** 用来存储配置包菜单的缓存 */
	private Map<String, List<Thing>> configPackageCache = new HashMap<String, List<Thing>>();
	/** 数据对象自身所携带的菜单的名称 */
	public String thingMenuName = "menus";
	

	/**
	 * 通过给定的配置字符串列表和类型获得菜单定义。
	 * 
	 * @param dataNames 需要匹配的字符串列表
	 * @param type 类型
	 */	
	@SuppressWarnings("unchecked")
	public List<Thing> getMenu(Thing thing, String dataNames[], String type){
		List<Thing> menuList = new ArrayList<Thing>();
		if(dataNames == null || type == null){
			return menuList;
		}
		
		Map<String, List<Thing>> caches[] = null;
		if("data".equals(type)){
			caches = new Map[1];
			caches[0] = dataCache;
		}else if("package".equals(type)){
			caches = new Map[1];
			caches[0] = packageCache;
		}else if("dataPackage".equals(type)){
			caches = new Map[2];
			caches[0] = packageCache;
			caches[1] = dataPackageCache; 
		}else if("structurePackage".equals(type)){
			caches = new Map[2];
			caches[0] = packageCache;
			caches[1] = structurePackageCache;
		}else if("configPackage".equals(type)){
			caches = new Map[2];
			caches[0] = packageCache;
			caches[1] = configPackageCache;
		}
		
		//获取数据对象结构本身定义的菜单
		List<Thing> descriptors = null;
		if(thing != null){
			descriptors = thing.getAllDescriptors();
		}else{
			descriptors = Collections.emptyList();
		}
		
		//获得描述者定义的菜单并把它们放入一个Map中
		Map<String, List<Thing>> descriptMenus = new HashMap<String, List<Thing>>();
		List<Thing> menuConfigs = new ArrayList<Thing>();
		Map<String, String> context = new HashMap<String, String>();//过滤重复菜单
		for(Iterator<Thing> iter = descriptors.iterator(); iter.hasNext();){			
			Thing descriptor = iter.next();
			//System.out.println(descriptor.getMetadata().getPath());
			
			Thing menus = descriptor.getThing(thingMenuName + "@0");
			if(menus == null) continue;

			if(context.get(menus.getMetadata().getPath()) != null){
				continue;
			}else{
				context.put(menus.getMetadata().getPath(), menus.getMetadata().getPath());
			}
            //取主菜单定义
			List<Thing> mainMenus = (List<Thing>) menus.get("menuDefine@0/menu@");
			joinMenuDefines(menuConfigs, mainMenus);
			//Collections.(menuConfigs, mainMenus);
			
			List<Thing> menuSettings = (List<Thing>) menus.get("menuSetting@");			
			for(Iterator<Thing> miter = menuSettings.iterator(); miter.hasNext();){
				//取菜单定义
				Thing menuSetting = miter.next();				
				List<Thing> childs = menuSetting.getAllChilds("menu");
				if(childs.size() == 0){
					//如果菜单定义的菜单详细没有，返回继续
					continue;
				}
				
				String menuType = menuSetting.getString("menuType");
				List<Thing> dmenus = (List<Thing>) descriptMenus.get(menuType);
				if(dmenus == null){
					dmenus = new ArrayList<Thing>();
					descriptMenus.put(menuType, dmenus);
				}
				
				if(dmenus.size() != 0){
					//添加split
					Thing object = new Thing();
		    		object.put("isSplit", "true");
		    		dmenus.add(object);
				}
				
				//添加每个子菜单
				for(Iterator<Thing> cmiter = childs.iterator(); cmiter.hasNext();){
					addToList(dmenus, cmiter.next());
				}
			}
		}
		
		joinMenuDefines(menuConfigs, menuConfigCache);	
		
		//按照菜单定义的顺序取得菜单
		Map<String, String> contextTemp = new HashMap<String, String>();  //上下文，用于过滤菜单
		for(Iterator<Thing> iter = menuConfigs.iterator(); iter.hasNext();){
			Thing menuConfig = (Thing) iter.next();
			String path = menuConfig.getMetadata().getPath();
			if(contextTemp.get(path) != null){
				continue;
			}else{
				contextTemp.put(path, path);
			}
			
			//添加注册到项目菜单定义的子菜单
			List<Thing> subMenuList = new ArrayList<Thing>();
			for(int i=0; i<caches.length; i++){
				setMenu(caches[i].get(menuConfig.getMetadata().getName()), subMenuList, dataNames);
			}
			
			//添加直接从结构定义的菜单
			List<Thing> dmenus = descriptMenus.get(menuConfig.getMetadata().getName());
			if(dmenus != null && dmenus.size() != 0){
				if(subMenuList.size() != 0){
					Thing object = new Thing();
		    		object.put("isSplit", "true");
		    		subMenuList.add(object);
		    		
		    		for(Iterator<Thing> cmiter = dmenus.iterator(); cmiter.hasNext();){
						addToList(subMenuList, cmiter.next());
					}
				}else{
					subMenuList = dmenus;
				}
			}
			
			if(subMenuList.size() != 0){
				Thing menu = new Thing();
				menu.put("name", menuConfig.getMetadata().getName());
				menu.put("label", menuConfig.getMetadata().getLabel());		
				for(Thing child : subMenuList){
					menu.addChild(child, false);
				}

				menuList.add(menu);
			}
		}
		
		return menuList;
	}
	
	/**
	 * 设置和合并菜单。
	 * 
	 * @param cacheList
	 * @param menuList
	 * @param names
	 */
	private void setMenu(List<Thing> cacheList, List<Thing> menuList, String[] names){
		if(cacheList == null || names == null){
			return;
		}
		
		//遍历菜单的定义	
		for(Iterator iter = cacheList.iterator(); iter.hasNext();){
			Thing menu = (Thing) iter.next();
			
			//是否匹配
			String dataName = menu.getString("dataName");
			boolean isOk = false;
			if(dataName == null || "".equals(dataName)){
				isOk = true;
			}else if(dataName.startsWith("*") && dataName.endsWith("*")){
				if("*".equals(dataName)){
					isOk = true;
				}else{
					String name = dataName.substring(1, dataName.length() - 1);
					for(int i=0; i<names.length; i++){
						if(names[i].indexOf(name) != -1){
							isOk = true;
							break;
						}
					}
				}
			}else if(dataName.startsWith("*")){
				String name = dataName.substring(1, dataName.length());
				for(int i=0; i<names.length; i++){
					if(names[i].endsWith(name)){
						isOk = true;
						break;
					}
				}
			}else if(dataName.endsWith("*")){
				String name = dataName.substring(0, dataName.length() - 1);
				for(int i=0; i<names.length; i++){
					if(names[i].startsWith(name)){
						isOk = true;
						break;
					}
				}
			}else{
				for(int i=0; i<names.length; i++){
					if(names[i].equals(dataName)){
						isOk = true;
						break;
					}
				}
			}
			
			//如果匹配则增加菜单
			if(isOk){
				if(!isAllntList(menuList, menu.getAllChilds()) && menuList.size() != 0){
					Thing object = new Thing();
		    		object.put("isSplit", "true");
		    		menuList.add(object);
				}
				
				for(Iterator citer = menu.getAllChilds().iterator(); citer.hasNext();){
					Thing target = (Thing) citer.next();
					addToList(menuList, target);
				}
			}
		}
	}
	
	private void joinMenuDefines(List<Thing> menuConfigs, List<Thing> mainMenus){
		for(Iterator<Thing> iter = mainMenus.iterator(); iter.hasNext();){
			Thing s = iter.next();
			
			if(menuConfigs.size() == 0){
				menuConfigs.add(s);
			}else{
				boolean inserted = false;
				for(int i=0; i<menuConfigs.size(); i++){
					Thing d = menuConfigs.get(i);
					if(d.getString("name") != null && d.getString("name").equals(s.getString("name"))) break;
					
					if(d.getInt("index") > s.getInt("index")){
						if(i == 0)
						    menuConfigs.add(0, s);
						else
							menuConfigs.add(i-1, s);
						inserted = true;
						break;
					}
				}
				
				if(!inserted){
					menuConfigs.add(s);
				}
			}
		}
	}
	
	private boolean isAllntList(List<Thing> soruce, List<Thing> target){
		boolean allHave = true;
		for(Iterator citer = target.iterator(); citer.hasNext();){
			Thing cChild = (Thing) citer.next();
			boolean cHave = false;
			for(int i=0; i<soruce.size(); i++){
				Thing obj = (Thing) soruce.get(i);
				if((obj.getMetadata().getName() == null && cChild.getMetadata().getName() == null) || (obj.getMetadata().getName() != null && cChild.getMetadata().getName() != null && obj.getMetadata().getName().equals(cChild.getMetadata().getName()))){
					cHave = true;
					break;
				}
			}
			if(!cHave){
				allHave = false;
				break;
			}
		}
		return allHave;
	}
	
	private void addToList(List<Thing> source, Thing target){
		//如果源中不包含目标则添加，如果源中包含有和目标相同名称的迭代覆盖
		boolean have = false;
		
		for(Iterator iter = source.iterator(); iter.hasNext();){			
			Thing menu = (Thing) iter.next();
			if((menu.getMetadata().getName() == null && target.getMetadata().getName() == null) || (menu.getMetadata().getName() != null && target.getMetadata().getName() != null &&menu.getMetadata().getName().equals(target.getMetadata().getName()))){
				have = true;
				List<Thing> sourceChilds = menu.getAllChilds();
				if(sourceChilds.size() != 0){
					boolean allHave = isAllntList(sourceChilds, target.getAllChilds());
					
					if(!allHave){
						//添加split
						Thing object = new Thing();
			    		object.put("isSplit", "true");
			    		sourceChilds.add(object);
					}
				}
				
				//添加子列表
				for(Iterator citer = target.getAllChilds().iterator(); citer.hasNext();){
					Thing subTarget = (Thing) citer.next();
					addToList(sourceChilds, subTarget);
				}
				break;
			}
		}
		
		if(!have){
			source.add(target);
		}
	}
	
	/**
	 * 更新菜单定义。<p>
	 * 这个方法在系统启动或者配置文件更改后调用，有DataCenter里调用。
	 * 
	 * @param thing
	 */
	@SuppressWarnings("unchecked")
	public void update(Thing thing){
		//处理菜单定义，更新主菜单定义列表		
		for(Iterator iter = thing.getAllChilds("menuDefine").iterator(); iter.hasNext();){
			Thing menuConfigObj = (Thing) iter.next();
			for(Iterator cciter = menuConfigObj.getAllChilds().iterator(); cciter.hasNext();){
				Thing menuConfig = (Thing) cciter.next();
				
				boolean have = false;
				int realIndex = -1;
				int index = menuConfig.getInt("index");
				
				int addIndex = -1;
				int count = 1;			
				boolean indexSeted = false;
				//插入菜单的定义到缓存，按照index的序号排列插入
				for(Iterator citer = menuConfigCache.iterator(); citer.hasNext();){					
					//如果没有确定位置，设置当前的位置
					realIndex ++;
					Thing cacheMenuConfig = (Thing) citer.next();
					
					if(cacheMenuConfig.getMetadata().getName().equals(menuConfig.getMetadata().getName())){						
						//如果已经存在，覆盖
						have = true;														
					}
					//计算位置
					int currentIndex = cacheMenuConfig.getInt("index");
					
					count ++;
					if(index < currentIndex && !indexSeted){
						indexSeted = true;		
						addIndex = count;
					}
				}				
				
				if(have) continue;
				
				if(realIndex == -1){
					//如果缓存为空，添加
					menuConfigCache.add(menuConfig);
				}else {
					//如果不存在，添加
					if(addIndex == -1){
						menuConfigCache.add(menuConfig);
					}else{
						if(menuConfigCache.size() > addIndex){
							menuConfigCache.add(addIndex, menuConfig);
						}else{
							menuConfigCache.add(menuConfig);
						}
					}
				}
			}
		}
		
		//处理具体的菜单
		for(Iterator iter = thing.getAllChilds("menuSetting").iterator(); iter.hasNext();){
			Thing menu = (Thing) iter.next();
			String type = menu.getString("type");
			String menuType = menu.getString("menuType");
			Map<String, List<Thing>> cache = null;
			if("data".equals(type)){
				cache = dataCache;
			}else if("package".equals(type)){
				cache = packageCache;
			}else if("structurePackage".equals(type)){
				cache = structurePackageCache;
			}else if("dataPackage".equals(type)){
				cache = dataPackageCache;
			}else if("configPackage".equals(type)){
				cache = configPackageCache;
			}
						
			if(cache == null){
				//如果不是这几个缓存，退出
				return;
			}
			
			//取菜单缓存
			List<Thing> menuCache = (List) cache.get(menuType);
			if(menuCache == null){
				menuCache = new ArrayList<Thing>();
				cache.put(menuType, menuCache);
			}
			
			//添加菜单定义到缓存里
			if(menuCache.size() == 0){
				menuCache.add(menu);
			}else{
				//如果存在先删除
				for(int i=0; i<menuCache.size(); i++){
					Thing menuCached = (Thing) menuCache.get(i);
					if(menuCached.getMetadata().getId().equals(menu.getMetadata().getId())){
						menuCache.remove(i);
						break;
					}					
				}
				
				int index = menu.getInt("index");
				int realIndex = -1;
				boolean inserted = false;
				for(int i=0; i<menuCache.size(); i++){					
					Thing menuCached = (Thing) menuCache.get(i);
					int currentIndex = menuCached.getInt("index");
					if(index > currentIndex){
						menuCache.add(menu);
						inserted = true;
						break;
					}else if(index < currentIndex){
						realIndex = i;
					}
				}
				if(!inserted){
					if(realIndex == -1){
						menuCache.add(menu);
					}else{
						menuCache.add(realIndex, menu);
					}
				}
			}
		}
	}
	
	public void checkCache(){
		checkCache(dataCache);
		checkCache(packageCache);
		checkCache(structurePackageCache);
		checkCache(dataPackageCache);
		checkCache(configPackageCache);
	}
	
	/**
	 * 校验菜单。
	 * 
	 * @param caches
	 */
	public void checkCache(Map<String, List<Thing>> caches){
		for(String key : caches.keySet()){
			List<Thing> cs = caches.get(key);
			for(Iterator<Thing> iter = cs.iterator(); iter.hasNext();){
				Thing thing = iter.next();
				
				if(!key.equals(thing.getString("menuType"))){
					iter.remove();
				}
			}
		}
	}
}