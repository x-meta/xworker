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
package xworker.dataObject.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilJava;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.dataObject.http.ConditionHttpUtils;
import xworker.util.DebugUtils;

public class ConditionCreator {	
	private static Logger logger = LoggerFactory.getLogger(ConditionCreator.class);

	
    public static Object getValidConditions(ActionContext actionContext){
    	//这个方法好像不完整
    	/*
        def cds = [:];
        def condition = self;
        
        def isValid(condition, datas){
            if(datas.get(condition.dataName) != null){
                return true;
            }else{
                if(!condition.getBoolean("ignoreNull")){
                    return true;
                }
            }
        }*/
    	return null;
    }

    public static Object toSql(ActionContext actionContext){
    	World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
        
        try {
        	DebugUtils.pushDebug(self.getBoolean("debug"));
        	boolean debug = DebugUtils.isDebug();
	        String sql = "";
	        if(self instanceof Thing){
	            sql = (String) ((Thing) self).doAction("nodeToSql", actionContext);
	        }else{	           
	            Thing thing = world.getThing("xworker.dataObject.query.Condition/@actions/@nodeToSql");
	            sql = (String) thing.getAction().run(actionContext, null, true);
	        }
	        
	        String childSql = "";
	        for(Thing child : self.getChilds("Condition")){
	            String s = "";
	            if(child instanceof Thing){
	                s = (String) child.doAction("toSql", actionContext);
	            }else{
	                Thing thing = world.getThing("xworker.dataObject.query.Condition/@actions/@toSql");
	                try{
	                    Bindings bindings = actionContext.push(null);
	                    bindings.put("self", child);
	                    s = (String) thing.getAction().run(actionContext, null, true);
	                }finally{
	                    actionContext.pop();
	                }
	            }
	            
	            if(s != null && !"".equals(s)){
	                if(!"".equals(childSql)){
	                    childSql = childSql + " " + getJoin(child) + " ";
	                }
	                if(child.getChilds("Condition").size() > 0){
	                    childSql = childSql + "(" + s + ")";
	                }else{
	                    childSql = childSql + s;
	                }
	            }
	        }
	        if(sql == null){
	            sql = "";
	        }
	        if(!"".equals(childSql)){
	            if(!"".equals(sql)){
	                sql = sql + " " + getJoin(self) + " (" + childSql + ")";
	            }else{
	                sql = childSql;
	            }
	        }
	        
	        //从句的处理
	        if((!"".equals(sql)  || self.getBoolean("alwaysAddClause")) && self.getBoolean("isClause")){
	        	String clauseSQL  = self.getStringBlankAsNull("clauseSQL");
	    		if(clauseSQL != null){
	    			if(self.getBoolean("isClauseTemplate")){
	    				clauseSQL = clauseSQL.replace("%%SQL%%", sql);
	    				sql = "(" + clauseSQL + ")";
	    				
	    			}else{
	    				if(!"".equals(sql)) {
	    					sql = "(" + clauseSQL + " where " + sql + ")";
	    				}else {
	    					sql = "(" + clauseSQL + ")";
	    				}
	    			}
	    			
	    			String column = self.getString("sqlColumn");
	    	        if(column == null || "".equals(column)){
	    	            column = self.getString("attributeName");
	    	        }
	    			String stroperator = self.getString("operator");
	                if(stroperator != null &&  !"".equals(stroperator)){
	                	byte operator = self.getByte("operator");
	                    switch(operator){
	                        case UtilCondition.eq:
	                        	sql = column + " = " + sql;
	                            break;
	                        case UtilCondition.uneq:
	                        	sql = column + " <> " + sql;
	                            break;
	                        case UtilCondition.gt:
	                        	sql = column + " > " + sql;
	                            break;
	                        case UtilCondition.gteq:
	                        	sql = column + " >= " + sql;
	                            break;
	                        case UtilCondition.lt:
	                        	sql = column + " < " + sql;
	                            break;
	                        case UtilCondition.lteq:
	                        	sql = column + " <= " + sql;
	                            break;
	                        case UtilCondition.like:
	                        	sql = column + " like " + sql;
	                            break;
	                        case UtilCondition.llike:
	                        	sql = column + " like " + sql;
	                            break;
	                        case UtilCondition.rlike:
	                        	sql = column + " like " + sql;
	                            break;
	                        case UtilCondition.lrlike:
	                        	sql = column + " like " + sql;
	                            break;
	                        case UtilCondition.in:
	                        	sql = column + " in " + sql;
	                            break;
	                        case UtilCondition.notin:
	                        	sql = column + " not in " + sql;
	                            break;
	                        case UtilCondition.between:
	                        	sql = column + " between " + sql;
	                        
	                    }
	                }
	    		}
	    	}
	        
	        if(debug) {
	            logger.info("conditionsql=" + sql);
	        }
	        return sql;
        }finally {
        	DebugUtils.popDebug();
        }
    }
    
    public static String getJoin(Thing condition){
    	String join = condition.getString("join");
        if(join == null || "".equals(join)){
            return "and";
        }else{
            return join;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object nodeToSql(ActionContext actionContext) throws OgnlException, ParseException{
    	Thing self = (Thing) actionContext.get("self");
    	try {
    		DebugUtils.pushDebug(self.getBoolean("debug"));
			boolean debug = DebugUtils.isDebug();
			
			//Object datas = actionContext.get("datas");
			List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
			
		    String sql = "";
		    Thing condition = self;
		    Object value = self.doAction("getConditionValue",  actionContext);
		    //Object value = getConditionValue(condition, condition.getString("dataName"), datas, actionContext);
		
		    //log.info("xxx=" + condition.dataName + "=" + value);
		    if(debug){
		    	logger.info(condition.getString("dataName") + "=" + value);
		    }
		    
		    if(value != null || condition.getBoolean("ignoreNull") == false){
		    	String stroperator = condition.getString("operator");
		        if(stroperator != null &&  !"".equals(stroperator)){
		        	byte operator = condition.getByte("operator");
		            switch(operator){
		                case UtilCondition.eq:
		                    sql = getSql(condition, value, "=", cds);
		                    break;
		                case UtilCondition.uneq:
		                    sql = getSql(condition, value, "<>", cds);
		                    break;
		                case UtilCondition.gt:
		                    sql = getSql(condition, value, ">", cds);
		                    break;
		                case UtilCondition.gteq:
		                    sql = getSql(condition, value, ">=", cds);
		                    break;
		                case UtilCondition.lt:
		                    sql = getSql(condition, value, "<", cds);
		                    break;
		                case UtilCondition.lteq:
		                    sql = getSql(condition, value, "<=", cds);
		                    break;
		                case UtilCondition.like:
		                    sql = getSql(condition, value, "like", cds);
		                    break;
		                case UtilCondition.llike:
		                    if(value != null){
		                        value = "%" + value;
		                    }
		                    sql = getSql(condition, value, "like", cds);
		                    break;
		                case UtilCondition.rlike:
		                    if(value != null){
		                        value = value + "%";
		                    }
		                    sql = getSql(condition, value, "like", cds);
		                    break;
		                case UtilCondition.lrlike:
		                    if(value != null){
		                        value = "%" + value + "%";
		                    }
		                    sql = getSql(condition, value, "like", cds);
		                    break;
		                case UtilCondition.in:
		                    sql = getSqlIn(condition, value, cds, operator);
		                    break;
		                case UtilCondition.notin:
		                    sql = getSqlNotIn(condition, value, cds, operator);
		                    break;
		                case UtilCondition.between:
		                    sql = getSqlIn(condition, value, cds, operator);
		                    break;
		                
		            }
		        }
		    }
		    return sql;
    	}finally {
    		DebugUtils.popDebug();
    	}

    }

    //返回in sql的表达式
    @SuppressWarnings("unchecked")
	public static String getSqlIn(Thing condition, Object value, List<Map<String, Object>> cds, byte operator){
        String column = condition.getString("sqlColumn");
        if(column == null || "".equals(column)){
            column = condition.getString("attributeName");
        }
        String sql = column + " in (";
        Object vl = getMulityValues(value);
        if(vl instanceof Object[] || vl instanceof List){
        	int index = 0;
            for(Object v : (Iterable<Object>) vl){
            	if(index > 0) {
                //if(!"".equals(sql)){
                    sql = sql + ",";
                }
                sql = sql + "?";
                Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",v, "operator",operator});
                cds.add(cd);
                
                index++;
            }
        }else{
            sql = sql + "?";
            Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",value, "operator",operator});
            cds.add(cd);
        }
        return sql + ")";
    }
    
    //返回in sql的表达式
    @SuppressWarnings("unchecked")
	public static String getSqlNotIn(Thing condition, Object value, List<Map<String, Object>> cds, byte operator){
        String column = condition.getString("sqlColumn");
        if(column == null || "".equals(column)){
            column = condition.getString("attributeName");
        }
        String sql = column + " not in (";
        Object vl = getMulityValues(value);
        if(vl instanceof Object[] || vl instanceof List){
        	int index = 0;
            for(Object v : (Iterable<Object>) vl){
            	if(index > 0) {
                //if(!"".equals(sql)){
                    sql = sql + ",";
                }
                sql = sql + "?";
                Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",v, "operator",operator});
                cds.add(cd);
                
                index++;
            }
        }else{
            sql = sql + "?";
            Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",value, "operator",operator});
            cds.add(cd);
        }
        return sql + ")";
    }
    
    //返回普通sql的表达式
    @SuppressWarnings("unchecked")
	public static String getSql(Thing condition, Object value, String operator, List<Map<String, Object>> cds){
    	if(condition.getBoolean("dummySql")){
    		Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value", value, "operator",operator, "condition", condition});
            cds.add(cd);
            return null;
    	}
    	
        String column = condition.getString("sqlColumn");
        if(column == null || "".equals(column)){
            column = condition.getString("attributeName");
        }
        String sql = "";
        Object vl = getMulityValues(value);
        if(vl instanceof Object[] || vl instanceof List){
            sql = sql + "(";
            for(Object v : UtilJava.getIterable(vl)){
                if(!"(".equals(sql)){
                	if("and".equals(condition.getString("multiValueJoin"))){
                		sql = sql + " and ";
                	}else{
                		sql = sql + " or ";
                	}
                }
                sql = sql + column + " " + operator + " ?";
                Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",v, "operator",operator, "condition", condition});
                cds.add(cd);
            }
            sql = sql + ")";
        }else{
            sql = column + " " + operator + " ?";
            Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",value, "operator",operator, "condition", condition});
            cds.add(cd);
        }
        return sql;
    }
    
    public static Object getMulityValues(Object value){
        if(value == null){
            return null;
        }
        
        if(value instanceof String){
        	List<String> values = new ArrayList<String>();
        	String str = (String) value;
        	for(String vs : str.split("[,]")) {
        		for(String v : vs.split("[ ]")) {
        			v = v.trim();
        			if("".equals(v)) {
        				continue;
        			}else {
        				values.add(v);
        			}
        		}
        	}
        	
            //for(String v : ((String) value).split("[,]")){
            //	values.add(v);
            //}
            return values;
        }else{
            return value;
        }
    }
    
    public static Object getConditionValue(Thing thing, String dataName, Object condition, ActionContext actionContext) throws OgnlException, ParseException{
    	//取条件源
    	String valueSource = thing.getString("valueSource");
    	if(valueSource != null && !"".equals(valueSource)){
    		String sourceDataName = thing.getString("sourceDataName");
    		if("ActionConext".equals(valueSource)){
    			condition = actionContext;
    		}else if("HttpServletRequest".equals(valueSource)){
    			condition = ConditionHttpUtils.getHttpRequestAttribute(actionContext, sourceDataName);
    		}else if("HttpSession".equals(valueSource)){
    			condition = ConditionHttpUtils.getHttpSessionAttribute(actionContext, sourceDataName);
    		}else if("XMetaSession".equals(valueSource)){
    			condition = SessionManager.getSession(actionContext);
    		}
    	}
    	
    	//获取查询参数值
        Object cValue = null;
        if(dataName != null && !"".equals(dataName) && condition != null){
        	try{
        		cValue = OgnlUtil.getValue(dataName, condition);
        	}catch(Exception e){
        		logger.info("get condition exception, dataName=" + dataName + ",thing=" + thing.getMetadata().getPath() 
        				+ "," + e.getMessage());
        	}
        }
    
        if(cValue == null){
            cValue = thing.get("value");
        }
        
        if(cValue instanceof String && ((String) cValue).trim().equals("")){
            cValue = null;
        }
        
        if(cValue != null && thing.getBoolean("addOneDay")){
        	if(cValue instanceof Date){
        		cValue = new Date(((Date) cValue).getTime() + 24 * 3600000);
        	}else if(cValue instanceof String){
        		String pattern = thing.getString("pattern");
        		if(pattern == null || "".equals(pattern)){
        			pattern = "yyyy-MM-dd";
        		}
        		SimpleDateFormat sf = new SimpleDateFormat(pattern);
        		Date d = sf.parse((String) cValue);
        		cValue = new Date(d.getTime() + 24 * 3600000);
        	}        	        	
        }
        
        if(cValue instanceof String && "".equals(cValue)){
        	cValue = null;
        }
        
        String type = thing.getString("type");
        if("string".equals(type)){
        	if(cValue instanceof Date){
        		String pattern = thing.getStringBlankAsNull("pattern");
        		if(pattern == null){
        			pattern = "yyyy-MM-dd";
        		}
        		SimpleDateFormat sf = new SimpleDateFormat(pattern);
        		cValue = sf.format((Date) cValue);
        	}
        }
        return cValue;
    }
    
    @SuppressWarnings("unchecked")
	public static Object isMatch(ActionContext actionContext) throws OgnlException, ParseException{
        Thing self = (Thing) actionContext.get("self");
        Object condition = actionContext.get("condition");
        Object data = actionContext.get("data");
        
        boolean result = true;
        String attributeName= self.getString("attributeName");
        if(attributeName != null && !"".equals(attributeName)){
            //获取查询参数值
        	Object cValue = self.doAction("getConditionValue",  actionContext, "datas", condition);
            //Object cValue = getConditionValue(self, self.getString("dataName"), condition, actionContext);
            
            //数据参数值
            Object value = OgnlUtil.getValue(self.getString("attributeName"), data);
        
            //log.info("conditionValue:" + self.name + "=" + cValue);
            //log.info("dataValue:" + self.dataName + "=" + value);
            //log.info("conditionPath:" + self.metadata.path);
            //log.info("operator:" + self.getByte("operator"));
            //比较
            if(cValue instanceof String && cValue != null){
                result = false;
                if("and".equals(self.getStringBlankAsNull("multiValueJoin"))){
                	List<String> values = (List<String>) getMulityValues(cValue);
                	for(String cChildValue : values) {//((String) cValue).split("[,]")){
	                    result = UtilCondition.isMatch(value, cChildValue, self.getByte("operator"), self.getString("type"), self.getString("pattern"),
	                         self.getBoolean("ignoreNull"), actionContext);
	                    if(!result){
	                        break;
	                    }
	                }
                }else{
                	List<String> values = (List<String>) getMulityValues(cValue);
	                for(String cChildValue : values) {// ((String) cValue).split("[,]")){
	                    result = UtilCondition.isMatch(value, cChildValue, self.getByte("operator"), self.getString("type"), self.getString("pattern"),
	                         self.getBoolean("ignoreNull"), actionContext);
	                    if(result){
	                        break;
	                    }
	                }
                }
            }else{
                result = UtilCondition.isMatch(value, cValue, self.getByte("operator"), self.getString("type"), self.getString("pattern"),
                         self.getBoolean("ignoreNull"), actionContext);
            }
            //log.info("isMatch " + result + ",operator=" + self.operator + "v1=" + value + ",v2=" + cValue);
        }
        
        if(result == false){
            return false;
        }else{
            //判断子条件
            for(Thing child : self.getChilds("Condition")){
                String joinType = child.getString("join");
                if("or".equals(joinType)){
                    result = result | (Boolean) child.doAction("isMatch", actionContext);
                }else{
                    //log.info("child=" + child + ",childResult=" + child.doAction("isMatch", actionContext));
                    //break;
                    result = result & (Boolean) child.doAction("isMatch", actionContext);
                }
                
                if(!result){
                    return false;
                }
            }
        }
        
        return result;
    }

	public static Object parseHttpRequest(ActionContext actionContext){
    	return ConditionHttpUtils.parseHttpRequest(actionContext);
    }

	/*
    public static void initParamsSwt(ActionContext actionContext) throws ParseException{
    	ConditionSwtUtils.initParamsSwt(actionContext);
    }
    */
	
	public static Object getConditionValue(ActionContext actionContext) throws OgnlException, ParseException{
		Thing self = (Thing) actionContext.get("self");
    	//boolean debug = UtilAction.getDebugLog(self, actionContext);
    	
    	Object datas = actionContext.get("datas");
    	//List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
    	
        //String sql = "";
        Thing condition = self;
        return getConditionValue(condition, condition.getString("dataName"), datas, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void addCdsData(ActionContext actionContext){
		Thing condition = (Thing) actionContext.get("self");
		List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
		String name = actionContext.getObject("name");
		String value = actionContext.getObject("value");
		String operator = actionContext.getObject("operator");
		Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",name, "value",value, "operator",operator, "condition", condition});
		cds.add(cd);
	}
}