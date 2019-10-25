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
package xworker.swt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingRegistor;

/**
 * <p>资源管理的目标是图片、字体、颜色、光标等资源共享。</p>
 * 
 * 任何资源事物都应有getKey和createResource两个方法。
 * 
 * @author zhangyuxiang
 *
 */
public class ResourceManager {
	private static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	/**
	 * 资源在不同的Device之间应该是不能共享的。
	 */
	private static Map<Device, Map<String, ResourceRegistor>> deviceResources = new HashMap<Device, Map<String, ResourceRegistor>>();
	private static Map<String, ResourceRegistor> getResources(){
		Display display = Display.getCurrent();
		Map<String, ResourceRegistor> resources = deviceResources.get(display);
		if(resources == null) {
			resources = new  HashMap<String, ResourceRegistor>();
			deviceResources.put(display, resources);
		}
		
		return resources;		
	}
		
	
	/**
	 * 获取资源。
	 * 
	 * @param key
	 * @return
	 */
	private static Resource getResource(String key){
		Map<String, ResourceRegistor> resources = getResources();
		ResourceRegistor registor = resources.get(key);
		if(registor != null){
			return registor.resource;
		}else{
			return null;
		}
	}
	
	/**
	 * 放入资源。
	 * 
	 * @param key
	 * @param resource
	 * @param actionContext
	 */
	public static synchronized void putResource(String key, Resource resource, ActionContext actionContext){
		if(getResource(key) == null){
			Map<String, ResourceRegistor> resources = getResources();
			
			Object parent = actionContext.get("parent");
			if(parent != null && parent instanceof Widget){
				ResourceRegistor registor = new ResourceRegistor(key, (Widget) parent, resource);
				resources.put(key, registor);
			}else{
				ResourceRegistor registor = new ResourceRegistor(key, null, resource);
				resources.put(key, registor);
			}
		}
	}
	
	/**
	 * 创建或者获取图片资源。
	 * 
	 * @param path
	 * @param actionContext
	 * @return
	 */
	public static synchronized Resource createIamge(String path, ActionContext actionContext){
		if(path == null || "".equals(path)){
			return null;
		}
		
		//logger.info("key=" + path + ",parent=" + actionContext.get("parent"));
		Map<String, ResourceRegistor> resources = getResources();
		ResourceRegistor registor = resources.get(path);
		if(registor != null){
			Object parent = actionContext.get("parent");
			if(parent != null && parent instanceof Widget){
				registor.addWidget((Widget) parent);
			}else {
				logger.warn("Can not create image, parent is null");
				return null;
			}
			
			if(registor.resource.isDisposed() || registor.resource.getDevice().isDisposed()){
				resources.put(path, null);
			}else{
				return registor.resource;
			}
		}
		
		Image image = null;
		World world = World.getInstance();
		String imageFilePath = null;

	    imageFilePath = path;
	    try{
	    	//如果是http的数据
	    	URL url = new URL(path);
	    	if(url.getProtocol() != null && url.getProtocol().startsWith("http")){
	    		URLConnection con = url.openConnection();
	    		try{
		    		con.connect();
	    			image = new Image(Display.getCurrent(), con.getInputStream());
	    		}catch(Exception ee){
	    			logger.warn("Create image from http error", ee);
	    		}
	    	}
	    }catch(Exception e){		    			    	
	    }
	    
	    if(image == null){
		    //直接从文件系统取
		    File file = new File(imageFilePath);
		    
		    //从默认图片路径
		    if(!file.exists() || !file.isFile()){
		        Thing globalConfig = World.getInstance().getThing(ThingRegistor.getPath("_xworker_globalConfig"));
		        if(globalConfig != null){
		        	imageFilePath = globalConfig.getString("imagePath") + "/" + imageFilePath;
		        	file = new File(imageFilePath);
		        }
		    }		    
		    
		    //从world根目录
		    if(!file.exists() || !file.isFile()){
		        imageFilePath = world.getPath() + "/" + path;
		        file = new File(imageFilePath);
		    }
		    
		    //从world/webroot下取
		    if(!file.exists() || !file.isFile()){
		    	String webRoot = world.getWebFileRoot();
		    	if(webRoot == null){
		    		webRoot = world.getPath() + "/webroot";
		    	}
		        imageFilePath = webRoot + "/" + path;
		        file = new File(imageFilePath);
		    }
		    
		    //从系统资源的变量上下文取
		    if(!file.exists() || !file.isFile()){
		    	try {
		    		if(!path.startsWith("/") && !path.startsWith("\\")){
		    			imageFilePath = "/" + path;
		    		}else{
		    			imageFilePath = path;
		    		}
					InputStream rin = world.getResourceAsStream(imageFilePath);
					if(rin != null){
						image = new Image(Display.getCurrent(), rin);
					}
				} catch (IOException e) {					
				}
		    	
		        if(image == null){
		        	Object obj = actionContext.get(path);
			        if(obj instanceof Image){
			            image = (Image) image;
			        }
		        }
		    }else{
		    	image = new Image(Display.getCurrent(), imageFilePath);
		    }
	    }

		if(image != null){
			if(UtilSwt.getScaling() > 0 && UtilSwt.getScaling() != 1){
				//缩放图片
				int width = image.getBounds().width;  
				int height = image.getBounds().height;  
				Image newImage = new Image(image.getDevice(), image.getImageData().scaledTo(UtilSwt.getInt(width), UtilSwt.getInt(height)));
				image.dispose();
				image = newImage;
			}
			
			Object parent = actionContext.get("parent");
			if(parent != null && parent instanceof Widget){
				putResource(path, image, parent);				
			}
		}
		
		return image;
	}
	
	/**
	 * 防止资源，避免重复放入。
	 * 
	 * @param key
	 * @param resource
	 * @param parent
	 */
	private static void putResource(String key, Resource resource, Object parent) {
		Map<String, ResourceRegistor> resources = getResources();
		ResourceRegistor registor = resources.get(key);
		if(registor != null && registor.resource == resource) {
			return;
		}
		
		if(parent instanceof Widget) {
			registor = new ResourceRegistor(key, (Widget) parent, resource);
			resources.put(key, registor);
		}
	}
	
	/**
	 * 方法同步避免同一个key的资源被建立两次。
	 * 
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static synchronized Resource createResource(Thing thing, ActionContext actionContext){
		String key = (String) thing.doAction("getKey", actionContext);
		if(key == null){
			return null;
		}
		
		Map<String, ResourceRegistor> resources = getResources();
		ResourceRegistor registor = resources.get(key);
		if(registor != null){
			synchronized(registor){
				if(!registor.resource.isDisposed()){
					Object parent = actionContext.get("parent");
					if(parent != null && parent instanceof Widget){
						registor.addWidget((Widget) parent);
					}
					
					return registor.resource;
				}
			}
		}
		
		Resource resource = (Resource) thing.doAction("createResource", actionContext);
		if(resource == null){
			return null;
		}
		
		Object parent = actionContext.get("parent");
		if(parent != null && parent instanceof Widget){
			putResource(key, resource, parent);	
		}
		
		return resource;		
	}
	
	private static synchronized void removeResource(String key){
		Map<String, ResourceRegistor> resources = getResources();
		ResourceRegistor registor = resources.get(key);
		if(registor != null && registor.bindWidgets.size() == 0){
			resources.remove(key);
		}
	}
	
	static class ResourceRegistor implements DisposeListener{
		//绑定的控件，如果控件全部销毁，则资源销毁
		List<Widget> bindWidgets = new ArrayList<Widget>();
		//资源的key
		String key;
		//资源
		Resource resource;
		
		public ResourceRegistor(String key, Widget bindWidget, Resource resource){
			this.key = key;
			this.resource = resource;
			addWidget(bindWidget);
		}
		
		public synchronized void addWidget(Widget widget){
			if(widget != null && !bindWidgets.contains(widget)){
				widget.addDisposeListener(this);
				bindWidgets.add(widget);
			}
		}

		@Override
		public synchronized void widgetDisposed(DisposeEvent event) {
			Widget widget = event.widget;
			bindWidgets.remove(widget);
			
			if(bindWidgets.size() == 0){
				removeResource(key);
				if(SwtUtils.isRWT()) {
					//RWT下，Shell关闭时，资源会先释放，这样Shell会报错，增加下面的语句为解决该问题
					event.display.asyncExec(new Runnable() {
						public void run() {
							try {
								ResourceRegistor.this.resource.dispose();
							}catch(Exception e) {
								logger.warn("release resource exception", e);
							}
						}
					});
				}else {
					resource.dispose(); //暂时注释掉，看看如果没有dispose是否还有函数的那个让其他编辑器的图标dispose的情况
				}
			}
		}
		
		public Resource getResource(){
			return resource;
		}
		
		public List<Widget> getWidgets(){
			return bindWidgets;
		}
		
		public String toString(){
			return resource.toString();
		}
	}
	
	/**
	 * 创建图片、颜色等资源。
	 * 
	 * @param value
	 * @param descriptor
	 * @param attributeName
	 * @param actionContext
	 * @return
	 */
	public static Object createResource(String value, String descriptor, String attributeName, ActionContext actionContext){
		if(value == null || "".equals(value)){
	        return null;
	    }
		    
	    Thing resThing = new Thing(descriptor);
	    resThing.set(attributeName, value);
	    return resThing.doAction("create", actionContext);
	}
	
	/**
	 * 创建图片。
	 * 
	 * @param parent
	 * @param rgb
	 * @param actionContext
	 * @return
	 */
	public static Color createColor(Control parent, String rgb, ActionContext actionContext){
		Thing thing = new Thing("xworker.swt.graphics.Color");
		thing.put("rgb", rgb);
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", parent);
			return (Color) thing.doAction("create", actionContext);
		}finally{
			actionContext.pop();
		}
	}
	
	/**
	 * 创建字体。
	 * 
	 * @param parent
	 * @param fontStr
	 * @param actionContext
	 * @return
	 */
	public static Font createFont(Control parent, String fontStr, ActionContext actionContext){
		Thing thing = new Thing("xworker.swt.graphics.Font");
		thing.put("fontData", fontStr);
		try{
			Bindings bindings = actionContext.push();
			bindings.put("parent", parent);
			return (Font) thing.doAction("create", actionContext);
		}finally{
			actionContext.pop();
		}
	}
}