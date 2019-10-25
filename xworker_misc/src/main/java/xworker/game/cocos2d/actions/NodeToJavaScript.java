package xworker.game.cocos2d.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class NodeToJavaScript {
	/**
	 * 节点转化为JavaScript的方法。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toJavaScript(ActionContext actionContext){
		String js = null;
		List<Thing> things = actionContext.getThings();
        Thing self = null;
        if(things.size() > 1){
        	self = things.get(things.size() - 2);
        }
        
        Map<String, String> paramContext = new HashMap<String, String>();
		//所有的描述者
		List<Thing> descriptors = self.getAllDescriptors();
		//所有的toJavaScript动作列表
		List<Thing> actions = getToJavaScriptActions(descriptors);
		//是否是其他节点的子节点
	    boolean haveParent = JavaScriptUtils.isHaveParent(actionContext);	    
	    
		boolean comma = false;
		Thing currentAction = actions.get(0);
		//生成原型
		if(self.getBoolean("prototype") == true && !haveParent){
			js = "var " + self.getMetadata().getName() + " = " + currentAction.getString("className") + ".extend({";
			//属性
			for(Thing properties : self.getChilds("Properties")){
				for(Thing property : properties.getChilds()){
					if(comma){
						js = js + ",\r\n";
					}else{
						comma = true;
					}
					
					String name = property.getMetadata().getName();
					String value = property.getStringBlankAsNull("defaultValue");
					if(value == null){
						value = "null";
					}
					js = js + "\r\n    " + name + ":" + value;
				}
			}
			
			
			if(comma){
				js = js + ",\r\n";
			}
			js = js + "\r\n";
			Bindings bindings = actionContext.push();
			bindings.put("haveParent", true);
			bindings.put("parentName", "this");
								
			try{
				//init函数
				js = js + "    init:function(){\r\n        this._super();";
			
				for(Thing childs : self.getChilds("Childs")){
					for(Thing child : childs.getChilds()){
						String childJs = (String) child.doAction("toJavaScript", actionContext);
						if(childJs != null){									
							js = js +"\r\n" +  getIdentString(childJs, "        ");
							
							if(child.getBoolean("addToParent")){
								js = js + "\r\n        this.addChild(" + child.getMetadata().getName();
								String zOrder = child.getStringBlankAsNull("zOrder");
								String tag = child.getStringBlankAsNull("tag");
								if(zOrder != null && tag != null){
									js = js + "," + zOrder + ", " + tag;
								}else if(zOrder != null){
									js = js + ", " + zOrder;
								}
								js = js + ");";
							}
						}
					}
				}
				//属性赋值
				for(Thing action : actions){
					String attrJs = geAttributes(self, action,"this", paramContext);
					if(attrJs != null){					
						js = js + "\r\n" + getIdentString(attrJs, "        ");
					}
				}
				
				//Actions
				for(Thing acs : self.getChilds("Actions")){
					if(!"Actions".equals(acs.getThingName())){
						continue;
					}
					for(Thing ac : acs.getChilds()){
						String acJs = (String) ac.doAction("toJavaScript", actionContext);
						if(acJs != null){
							js = js + "\r\n" + getIdentString(acJs, "        ");
							if(ac.getBoolean("runByParent")){
								js = js + "\r\n        this." + ".runAction(" + ac.getMetadata().getName() + ");";
							}
						}
					}
				}
				
				js = js + "\r\n\r\n        return true;\r\n    }";
				
				//自定义的方法
				for(Thing methods : self.getChilds("Methods")){
					for(Thing method : methods.getChilds()){
						String childJs = (String) method.doAction("toJavaScript", actionContext);
						if(childJs != null){
							js = js + ",";
							js = js + "\r\n";
							js = js + getIdentString(childJs, "    ");
						}
					}
				}
				
				//事件的定义
				for(Thing methods : self.getChilds("Events")){
					for(Thing method : methods.getChilds()){
						String childJs = (String) method.doAction("toJavaScript", actionContext);
						if(childJs != null){
							js = js + ",";
							js = js + "\r\n";
							js = js + getIdentString(childJs, "    ");
						}
					}
				}
			}finally{
				actionContext.pop();
			}
			js = js + "\r\n});";
		}else{
			//在执行脚本非原型脚本中生成JavaScript代码
			js = toJavaScriptWithParent(self, currentAction, actions, paramContext, actionContext);
		}
		
		return js;
	}
	
	/**
	 * 在有父节点的情况下生成JavaScript。
	 * 
	 * @param self
	 * @param currentAction
	 * @param actionContext
	 * @return
	 */
	public static String toJavaScriptWithParent(Thing self, Thing currentAction, List<Thing> actions, Map<String, String> paramContext, ActionContext actionContext){
		String js = null;
		
		//首先生成子节点的代码
		List<Thing> childs = self.getChilds("Childs");
		for(Thing clds : childs){
			for(Thing child : clds.getChilds()){
				String childJs = (String) child.doAction("toJavaScript", actionContext);
				if(childJs != null){
					if(js == null){
						js = childJs;
					}else{
						js = js + "\r\n" + childJs;
					}
				}
			}
		}
		
		
		List<Thing> ctors = currentAction.getChilds("Constructor");
		if(ctors.size() == 0){				
			throw new Cocos2dException("NodeToJavaScript action not define Constructor, action=" + currentAction);
		}else{
			boolean ctorOk = false;
			for(Thing ctor : ctors){
				if(ctor.getStringBlankAsNull("paramsByChilds") != null && self.getChilds(ctor.getString("childThingName")) != null){
					String type = ctor.getStringBlankAsNull("paramsByChilds");
					List<Thing> ctorChilds = self.getChilds(ctor.getString("childThingName"));
					List<Thing> ctorParamChilds = new ArrayList<Thing>();
					String params = null;
					for(Thing ctorChild : ctorChilds){
						if("childschilds".equals(type)){
							for(Thing cld : ctorChild.getChilds()){
								ctorParamChilds.add(cld);								
							}
						}else{
							ctorParamChilds.add(ctorChild);
						}
					}
					for(Thing ctorChild : ctorParamChilds){
						String ctorChildJs = (String) ctorChild.doAction("toJavaScript", actionContext);
						if(ctorChildJs != null){
							if(js == null){
								js = ctorChildJs;
							}else{
								js = js + "\r\n" + ctorChildJs;
							}
							if(params != null){
								params = params + "," + ctorChild.getMetadata().getName();
							}else{
								params = ctorChild.getMetadata().getName();
							}
						}
					}
					
					//其次生成自己的构造函数
					if(js != null){
						if(self.getBoolean("newVar")){
							js = js + "\r\nvar ";
						}else{
							js = js + "\r\n";
						}
					}else{
						if(self.getBoolean("newVar")){
							js = "var ";
						}else{
							js = "";
						}
					}
					js = js + self.getMetadata().getName() + " = ";
					
					js = js + ctor.getString("function") + "(" + params + ");";
					ctorOk = true;
					break;
				}else{
					String params = getConstructorParams(self, ctor, paramContext);
					if(params != null){
						if(ctor.getStringBlankAsNull("function") == null){
							throw new Cocos2dException("Constructor not set attribute function, Constructor=" + ctor);
						}
						
						//其次生成自己的构造函数
						if(js != null){
							if(self.getBoolean("newVar")){
								js = js + "\r\n\r\nvar ";
							}else{
								js = js + "\r\n\r\n";
							}
						}else{
							if(self.getBoolean("newVar")){
								js = "var ";
							}else{
								js = "";
							}
						}
						js = js + self.getMetadata().getName() + " = ";
						
						js = js + ctor.getString("function") + "(" + params + ");";
						ctorOk = true;
						break;
					}
				}
			}
			if(!ctorOk){
				throw new Cocos2dException("Node donn't have property Constructor, please set enough Constructor paramters, Node=" + self);
			}			
		}
		
		//属性
		for(Thing properties : self.getChilds("Properties")){
			for(Thing property : properties.getChilds()){
				String name = property.getMetadata().getName();
				String value = property.getStringBlankAsNull("defaultValue");
				if(value == null){
					value = "null";
				}
				js = js + "\r\n" + self.getMetadata().getName() + "." + name + " = " + value + ";";
			}
		}
		
		//添加子事物
		for(Thing clds : childs){
			for(Thing child : clds.getChilds()){
				if(child.getBoolean("addToParent")){
					js = js + "\r\n" + self.getMetadata().getName() + ".addChild(" + child.getMetadata().getName();
					String zOrder = child.getStringBlankAsNull("zOrder");
					String tag = child.getStringBlankAsNull("tag");
					if(zOrder != null && tag != null){
						js = js + "," + zOrder + ", " + tag;
					}else if(zOrder != null){
						js = js + ", " + zOrder;
					}
					js = js + ");";
				}
			}
		}
		
		//属性赋值
		for(Thing action : actions){
			String attrJs = geAttributes(self, action, self.getMetadata().getName(), paramContext);
			if(attrJs != null){					
				js = js + "\r\n" + attrJs;
			}
		}
		
		actionContext.peek().put("parentName", self.getMetadata().getName());
		//自定义的方法
		for(Thing methods : self.getChilds("Methods")){
			for(Thing method : methods.getChilds()){
				String childJs = (String) method.doAction("toJavaScript", actionContext);
				if(childJs != null){
					js = js + "\r\n\r\n";
					js = js + getIdentString(childJs, "");
				}
			}
		}
		
		//事件的定义
		for(Thing methods : self.getChilds("Events")){
			for(Thing method : methods.getChilds()){
				String childJs = (String) method.doAction("toJavaScript", actionContext);
				if(childJs != null){
					js = js + "\r\n\r\n";
					js = js + getIdentString(childJs, "");
				}
			}
		}
		
		//Actions
		for(Thing acs : self.getChilds("Actions")){
			if(!"Actions".equals(acs.getThingName())){
				continue;
			}
			for(Thing ac : acs.getChilds()){
				String acJs = (String) ac.doAction("toJavaScript", actionContext);
				if(acJs != null){
					js = js + "\r\n" + acJs;
					if(ac.getBoolean("runByParent")){
						js = js + "\r\n" + self.getMetadata().getName() + ".runAction(" + ac.getMetadata().getName() + ");";
					}
				}
			}
		}
		
		return js;
	}
	
	/**
	 * 获得构造函数的参数，如果返回null表示改构造函数参数不足无法构造。
	 * 
	 * @param self
	 * @param constructor
	 * @param context
	 * @return
	 */
	public static String getConstructorParams(Thing self, Thing constructor, Map<String, String> context){
		String params = "";
		for(Thing param : constructor.getChilds("Param")){
			String paramValue = self.getStringBlankAsNull(param.getMetadata().getName());
			if(paramValue == null){
				return null;
			}else{
				if(!"".equals(params)){
					params = params + ", ";
				}
				
				params = params + paramValue;
			}
		}
		
		//设置参数已被使用
		for(Thing param : constructor.getChilds("Param")){
			context.put(param.getMetadata().getName(), param.getMetadata().getName());
		}
		
		return params;
	}
	
	public static String geAttributes(Thing self, Thing action, String varName, Map<String, String> context){
		String js = null;
		for(Thing method : action.getChilds("Method")){
			List<String> params = getMethodParams(method);
			List<String> paramValues = new ArrayList<String>();
			
			boolean ok = true;
			for(String param : params){
				if(context.get(param) != null){
					//参数已被使用，不重复使用
					ok = false;
					break;
				}
				
				String paramValue = self.getStringBlankAsNull(param);
				if(paramValue == null){
					//参数的值为空
					ok = false;
					break;
				}
				
				paramValues.add(paramValue);
			}
			
			if(ok){
				for(String param : params){
					context.put(param, param);
				}
				
				if(js == null){
					js = varName + "." + method.getString("method") + "(";					
				}else{
					js = js + "\r\n" + varName + "." + method.getString("method") + "(";
				}
				for(int i=0; i<paramValues.size(); i++){
					if(i != 0){
						js = js + ", ";
					}
					js = js + paramValues.get(i);
				}
				js = js + ");";
			}
		}		
		return js;
	}
	
	public static List<String> getMethodParams(Thing method){
		List<String> params = new ArrayList<String>();
		String param = method.getStringBlankAsNull("paramName");
		if(param != null){
			params.add(param);
		}else{
			for(Thing child : method.getChilds("Param")){
				params.add(child.getMetadata().getName());
			}
		}
		
		return params;
	}
	
	public static String getIdentString(String js, String ident){
		return JavaScriptUtils.getIdentString(js, ident);
	}
	
	public static String getProperty(Thing property){
		String defaultValue = property.getStringBlankAsNull("defaultValue");
		if(defaultValue == null){
			return "var " + property.getMetadata().getName();
		}else{
			return "var " + property.getMetadata().getName() + " = " + defaultValue;
		}
	}
		
	/**
	 * 返回所有NodeToJavaScript定义的toJavaScript动作。
	 * 
	 * @param descriptors
	 * @return
	 */
	public static List<Thing> getToJavaScriptActions(List<Thing> descriptors) {
		List<Thing> actions = new ArrayList<Thing>();
		for(Thing desc : descriptors){
			Thing acs = (Thing) desc.get("actions@0");
			if(acs != null){
				for(Thing ac : acs.getChilds()){
					if(ac.getMetadata().getName().equals("toJavaScript")){
						actions.add(ac);
					}
				}
			}
		}
		
		return actions;
	}
}
