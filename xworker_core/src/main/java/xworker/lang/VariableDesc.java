package xworker.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class VariableDesc implements java.lang.Comparable<VariableDesc>{
	private static Logger logger = LoggerFactory.getLogger(VariableDesc.class);
	
	public static final String THING = "thing";
	public static final String OBJECT = "object";
	public static final String ACTIONCONTAINER = "actionContainer";
	public static final String ACTION = "action";
	/** 作用范围，全局 */
	public static final byte SCOPE_GLOBAL = 0;
	/** 作用范围，只对当前节点生效 */
	public static final byte SCOPE_NODE = 1;
	/** 作用范围，只对当前节点及其子节点生效 */
	public static final byte SCOPE_NODE_AND_CHILDS = 2;
	
	String name;
	
	String type;
	
	String className;
	
	Thing thing;
	
	boolean passive = false;
	
	byte scope = SCOPE_GLOBAL;
	
	public VariableDesc(String name, String type, String className, Thing thing) {
		this(name, type, className, thing, SCOPE_GLOBAL);
	}
	
	public VariableDesc(String name, String type, String className, Thing thing, byte scope) {
		if("_thingName_".equals(name) && thing != null) {
			this.name = thing.getMetadata().getName();
		}else {
			this.name = name;
		}
		this.type = type;
		this.className = className;
		this.thing = thing;
		this.scope = scope;
	}
		
	public byte getScope() {
		return scope;
	}

	public void setScope(byte scope) {
		this.scope = scope;
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean passive) {
		this.passive = passive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		if(THING.equals(type)) {
			return "org.xmeta.Thing";
		}else if(ACTIONCONTAINER.equals(type)) {
			return "org.xmeta.util.ActionContainer";
		}
		
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}
	
	/**
	 * 返回指定属性的变量描述。
	 * 
	 * @param name
	 * @return
	 */
	public VariableDesc getFiledVariableDesc(String name) {
		if(VariableDesc.OBJECT.equals(type)) {
			if(className != null && !"".equals(className)) {
				try {
					Class<?> cls = World.getInstance().getClassLoader().loadClass(className);
					return getFieldVariableDesc(name, cls, name);
				}catch(Exception e) {
					return null;
				}
			}
		}else if(VariableDesc.THING.equals(type)) {
			if(thing != null) {
				Object value = thing.get(name);
				if(value != null) {
					return VariableDesc.create(name, value.getClass());
				}else {
					return getFieldVariableDesc(name, Thing.class, name);
				}
			}
		}else if(VariableDesc.ACTIONCONTAINER.equals(type)) {
			return null;
		}
		
		return null;
	}
	
	/**
	 * 返回方法的返回值的类型定义。
	 * 
	 * @param name
	 * @return
	 */
	public VariableDesc getMethodDesc(String name) {
		if(VariableDesc.OBJECT.equals(type)) {
			if(className != null && !"".equals(className)) {
				try {
					Class<?> cls = World.getInstance().getClassLoader().loadClass(className);
					return getMethodVariableDesc(name, cls, name);
				}catch(Exception e) {
					return null;
				}
			}
		}else if(VariableDesc.THING.equals(type)) {
			return null;
		}else if(VariableDesc.ACTIONCONTAINER.equals(type)) {
			return null;
		}
		
		return null;
	}
	
	private VariableDesc getMethodVariableDesc(String name, Class<?> cls, String methodName) {
		try {
			if(cls == null) {
				return null;
			}
			
			for(Method method : cls.getMethods()) {
				if(method.getName().equals(methodName)) {
					return VariableDesc.create(name, method.getReturnType());
				}
			}
		}catch(Exception e) {
			
		}
		
		return null;
	}
	
	private VariableDesc getFieldVariableDesc(String name, Class<?> cls, String fieldName) {
		try {
			if(cls == null) {
				return null;
			}
			
			Field field = cls.getField(fieldName);
			if(field != null) {
				return VariableDesc.create(name, field.getType());
			}
		}catch(Exception e) {
			
		}
		
		return null;
	}
	
	public static VariableDesc createVariableDesc(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String name = self.doAction("getVarName", actionContext);
		if(name == null || "".equals(name)) {
			return null;
		}
		Thing thing_ = actionContext.getObject("thing");
		if("_thingName_".equals(name)) {
			if(thing_ == null) {
				return null;
			}else {
				name = thing_.getMetadata().getName();
			}
		}
				
		String type = self.doAction("getType", actionContext);
		String className = self.doAction("getClassName", actionContext);
		Thing thing = self.doAction("getThing", actionContext);
		byte scope = self.getByte("scope");
		if("parent".equals(type)) {
			thing = thing_;
			type = THING;
		}
		
		VariableDesc var = new VariableDesc(name, type, className, thing, scope);
		var.setPassive(self.getBoolean("passive"));
		
		return var;
	}
	
	/**
	 * 返回指定事物的所有预定义的变量。
	 * 
	 * @param thing
	 * @param actionContext
	 * @return
	 */
	public static List<VariableDesc> getVariableDescs(Thing thing, ActionContext actionContext){
		return getVariableDescs(thing, false, actionContext);
	}
	
	public static List<VariableDesc> getVariableDescs(Thing thing, boolean passive, ActionContext actionContext){
		
		List<VariableDesc> vars = new ArrayList<VariableDesc>();
		
		Map<String, String> context = new HashMap<String, String>();
		if(passive) {
			getVariableDescs(thing, passive, context, vars, actionContext, true, thing);
			getVariableDescs(thing, passive, context, vars, actionContext, false, thing);
		}else {
			//变量定义的优先级，当前节点优先，然后向上遍历到根节点
			Thing temp = thing;
			while(temp != null) {
				//优先获取当前事物模型的
				getVariableDescs(temp, passive, context, vars, actionContext, true, thing);
				temp = temp.getParent();
			}
			
			temp = thing;
			while(temp != null) {
				//其次获取描述者和继承的事物的定义
				getVariableDescs(temp, passive, context, vars, actionContext, false, thing);
				temp = temp.getParent();
			}
		}
		
		return vars;		
	}
	
	/**
	 * 获取一个事物的变量定义，从自身以及它的描述者和继承者上获取。
	 * 
	 * @param thing
	 * @param context
	 * @param vars
	 * @param actionContext
	 */
	private static void getVariableDescs(Thing thing,  boolean passive, Map<String, String> context, List<VariableDesc> vars, ActionContext actionContext, boolean onlySelf, Thing originThing) {
		if(thing == null) {
			return;
		}
				
		if(onlySelf) {
			//获取自身的
			initVariableDescs(thing, passive, thing, context, vars, actionContext, originThing);
		}else {
			//获取描述者的
			for(Thing desc : thing.getAllDescriptors()) {
				initVariableDescs(desc, passive, thing, context, vars, actionContext, originThing);
			}
			
			//获取继承的
			for(Thing ext : thing.getAllExtends()) {
				initVariableDescs(ext, passive, thing, context, vars, actionContext, originThing);
			}
		}
		
		//获取子节点
		for(Thing child : thing.getChilds()) {
			getVariableDescs(child, passive, context, vars, actionContext, onlySelf, originThing);
		}
	}
	
	public static VariableDesc create(String name, Object object) {
		if(object instanceof Thing) {
			Thing thing = (Thing) object;
			return new VariableDesc(name, VariableDesc.THING, null, thing);
		}else if(object instanceof ActionContainer) {
			ActionContainer actionContainer = (ActionContainer) object;
			return new VariableDesc(name, VariableDesc.ACTIONCONTAINER, null, actionContainer.getThing());
		}else if(object != null) {
			return new VariableDesc(name, VariableDesc.OBJECT, object.getClass().getName(), null);
		}else {
			return new VariableDesc(name, VariableDesc.OBJECT, "java.lang.Object", null);
		}
	}
		
	public static VariableDesc create(String name, Class<?> cls) {
		return new VariableDesc(name, VariableDesc.OBJECT, cls.getName(), null);		
	}
	
	public static List<VariableDesc> getActionInputParams(Thing action){
		List<VariableDesc> list = new ArrayList<VariableDesc>();
		
		Thing ins = action.getThing("ins@0");
		if(ins != null){
			for(Thing p : ins.getChilds()){
				String pname = p.getMetadata().getName();
				String type = p.getStringBlankAsNull("type");
				if(type == null) {
					list.add(VariableDesc.create(pname, Object.class));
				}else {
					list.add(new VariableDesc(pname, OBJECT, type, null));
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 把source中的VariableDesc添加到target中，如果变量名已存在则不填添加。
	 * 
	 * @param source
	 * @param target
	 */
	public static void addAll(List<VariableDesc> source, List<VariableDesc> target) {
		if(source == null || target == null) {
			return;
		}
		
		for(VariableDesc s : source) {
			String name = s.getName();
			boolean have = false;
			for(VariableDesc t : target) {
				if(name != null && name.equals(t.getName())) {
					have = true;
					break;
				}
			}
			
			if(!have) {
				target.add(s);
			}
		}
	}
	
	/**
	 * 初始化变量定义。
	 * 
	 * @param thing
	 * @param context
	 * @param vars
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	private static void initVariableDescs(Thing thing, boolean passive, Thing currentThing, Map<String, String> context, List<VariableDesc> vars, ActionContext actionContext, Thing originThing) {
		if(thing == null) {
			return;
		}
		
		String path = thing.getMetadata().getPath() + "-" + currentThing.getMetadata().getPath();
		if(context.get(path) != null) {
			return;
		}
		context.put(path, path);
		
		Thing descs = null;
		for(Thing child : thing.getChilds()) {
			if(child.getDescriptors().get(0).getMetadata().getName().equals("VariablesDescs")) {
				descs = child;
				break;
			}
		}
		
		if(descs == null) {
			return;
		}
		
		for(Thing desc : descs.getChilds()) {
			try {
				//创建变量时，把当前事物作为变量thing传入
				if(desc.getBoolean("passive") == passive) {
					Object result = desc.doAction("create", actionContext, "thing", currentThing);
					if(result instanceof VariableDesc) {
						VariableDesc var = (VariableDesc) result;
						if(checkScope(currentThing, originThing, var)) {
							addVariables(var, passive, vars, context);			
						}
					}else if(result instanceof List) {
						List<VariableDesc> list = (List<VariableDesc>) result;
						for(VariableDesc var : list) {
							if(checkScope(currentThing, originThing, var)) {
								addVariables(var, passive, vars, context);
							}
						}
					}				
				}
			}catch(Exception e) {
				logger.warn("Create variable desc error, thing=" + desc.getMetadata().getPath(), e);
			}
		}
	}

	/**
	 * 检查变量的作用范围。
	 * 
	 * @param thing
	 * @param originThing
	 * @param var
	 * @return
	 */
	private static boolean checkScope(Thing thing, Thing originThing, VariableDesc var) {
		if(var.getScope() == SCOPE_GLOBAL) {
			return true;
		}
		
		if(var.getScope() == SCOPE_NODE) {
			if(thing == originThing) {
				return true;
			}else {
				return false;
			}
		}
		
		if(var.getScope() == SCOPE_NODE_AND_CHILDS) {
			Thing parent = thing;
			while(parent != null) {
				if(parent == thing) {
					return true;
				}
				
				parent = parent.getParent();
			}
			
			return false;
		}
		
		return true;
	}
	
	private static void addVariables(VariableDesc varDesc, boolean passive, List<VariableDesc> vars, Map<String, String> context) {
		if(varDesc != null) {
			String name = varDesc.getName();
			//同样的变量名只定义一次
			if(context.get(name) == null) {
				vars.add(varDesc);
				context.put(name, name);
			}
		}
	}
	
	/**
	 * 根据thing.getVariableDescType()的返回值返回变量定义。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static VariableDesc createVariableSwitch(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thing = actionContext.getObject("thing");
		
		if(thing == null) {
			return null;
		}
		
		String type = thing.doAction("getVariableDescType", actionContext);
		if(type == null) {
			return null;
		}
		
		for(Thing child : self.getChilds()) {
			if(type.equals(child.getMetadata().getName())) {
				return (VariableDesc) child.doAction("create", actionContext);
			}
		}
		
		return null;
	}
	
	@Override
	public int compareTo(VariableDesc o) {
		if(o == null) {
			return -1;
		}else {
			return this.name.compareTo(o.getName());
		}
	}
}
