package xworker.swt.xworker.codeassist.variableproviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.VariableDesc;
import xworker.swt.xworker.CodeAssistor;
import xworker.swt.xworker.codeassist.VariableProvider;
import xworker.util.StringUtils;
import xworker.util.UtilTemplate;
import xworker.util.XWorkerUtils;

public class CachedVaribleProvider implements VariableProvider{
	/** 根据事物做的缓存 */
	private static Map<String, List<VariableDesc>> caches = new HashMap<String, List<VariableDesc>>();
	private static List<VariableDesc> defaultCaches = new ArrayList<VariableDesc>();
	
	public static CachedVaribleProvider instance = new CachedVaribleProvider();
	
	private CachedVaribleProvider() {	
		defaultCaches.add(VariableDesc.create("SWT", org.eclipse.swt.SWT.class));
		defaultCaches.add(VariableDesc.create("UtilData", xworker.util.UtilData.class));
		defaultCaches.add(VariableDesc.create("System", java.lang.System.class));
		defaultCaches.add(VariableDesc.create("XWorkerUtils", XWorkerUtils.class));
		defaultCaches.add(VariableDesc.create("UtilTemplate", UtilTemplate.class));
		defaultCaches.add(VariableDesc.create("World", World.class));
		defaultCaches.add(VariableDesc.create("StringUtils", StringUtils.class));
		defaultCaches.add(VariableDesc.create("world", World.class));
		defaultCaches.add(VariableDesc.create("log", Logger.class));
		defaultCaches.add(VariableDesc.create("CodeAssistor", CodeAssistor.class));
		defaultCaches.add(VariableDesc.create("self", Thing.class));
		defaultCaches.add(VariableDesc.create("actionContext", ActionContext.class));
		defaultCaches.add(VariableDesc.create("DataObjectUtil", DataObjectUtil.class));
	}
	
	@Override
	public List<VariableDesc> getVariables(String code, int offset,  List<String> statements, Thing thing, ActionContext actionContext) {
		List<VariableDesc> vars = new ArrayList<VariableDesc>();
		if(thing != null) {
			//模型本身的Cache
			List<VariableDesc> cache = caches.get(thing.getMetadata().getPath());
			VariableDesc.addAll(cache, vars);
			
			//根节点的Cache
			cache = caches.get(thing.getRoot().getMetadata().getPath());
			VariableDesc.addAll(cache, vars);
			
			//默认的Cache
			VariableDesc.addAll(defaultCaches, vars);
		}
		
		return vars;
	}
	
	public void initCaches(String path, ActionContext actionContext){
		Thing thing = World.getInstance().getThing(path);
		if(thing != null){
			initCaches(thing, actionContext);
		}
	}
	
	public void initCaches(Thing thing, ActionContext actionContext){
		if(thing == null) {
			return;
		}
		
		List<VariableDesc> cache = caches.get(thing.getMetadata().getPath());
		if(cache == null){
			cache = new ArrayList<VariableDesc>();
			caches.put(thing.getMetadata().getPath(), cache);
		}
		
		cache.clear();
		for(String key : actionContext.keySet()){
			Object obj = actionContext.get(key);
			cache.add(VariableDesc.create(key, obj));
		}
	}
	
	public void putCache(Thing thing, String name, Class<?> cls){
		if(thing == null) {
			return;
		}
		
		List<VariableDesc> cache = caches.get(thing.getMetadata().getPath());
		if(cache != null) {
			for(VariableDesc desc : cache) {
				if(desc.getName() != null && desc.getName().equals(name)) {
					desc.setClassName(cls.getName());
					return;
				}
			}
			
			cache.add(VariableDesc.create(name, cls));
		}
	}
}
