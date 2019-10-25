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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;


/**
 * 条件比较的工具类。
 * 
 * @author Administrator
 *
 */
public class UtilCondition {
	/** = */
	public static final byte eq = 1;
	/** ！= */
	public static final byte uneq = 2;
	/** &lt; */
	public static final byte gt = 3;
	/** &lt;= */
	public static final byte gteq = 4;
	/** &gt; */
	public static final byte lt = 5;
	/** &gt;= */
	public static final byte lteq = 6;
	/** like */
	public static final byte like = 7;
	/** left like, end width */
	public static final byte llike = 8;
	/** right like, start width */
	public static final byte rlike = 9;
	/** left and right like, start or end width */
	public static final byte lrlike = 10;
	/** in */
	public static final byte in = 11;
	/** regex */
	public static final byte regex = 12;
	/** equals */
	public static final byte equals = 13;
	/** isnull */
	public static final byte isnull = 14;
	/** notnull */
	public static final byte notnull = 15;
	/** between */
	public static final byte between = 16;
	/** not in */
    public static final byte notin = 17;
	public static final String AND = "and";
	public static final String OR = "or";
	
	/**
	 * 根据操作符判断是否匹配。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param operator
	 * @param ignoreNull
	 * @return
	 */
	public static boolean isMatch(Object value, Object conditionValue, byte operator, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		switch(operator){
		case UtilCondition.eq:
			return UtilCondition.eq(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.equals:
			return UtilCondition.equals(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.gt:
			return UtilCondition.gt(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.gteq:
			return UtilCondition.gteq(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.in:
			return UtilCondition.in(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.notin:
			return UtilCondition.notin(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.isnull:
			return UtilCondition.isnull(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.like:
			return UtilCondition.like(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.llike:
			return UtilCondition.llike(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.lrlike:
			return UtilCondition.lrlike(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.lt:
			return UtilCondition.lt(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.lteq:
			return UtilCondition.lteq(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.notnull:
			return UtilCondition.notnull(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.regex:
			return UtilCondition.regex(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.rlike:
			return UtilCondition.rlike(value, conditionValue, type, pattern, ignoreNull, actionContext);
		case UtilCondition.uneq:
			return UtilCondition.uneq(value, conditionValue, type, pattern, ignoreNull, actionContext);
		}
		
		return false;
	}
	
	/**
	 * 比较like。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean like(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		return UtilCondition.lrlike(value, conditionValue, type, pattern, ignoreNull, actionContext);		
	}
	
	/**
	 * 左类似。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean llike(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(value == conditionValue){
			return true;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			
		String v[] = objsToString(type, pattern, value, conditionValue);
		return v[0].endsWith(v[1]);
	}
	
	/**
	 * 右类似。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean rlike(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(value == conditionValue){
			return true;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			
		String v[] = objsToString(type, pattern, value, conditionValue);
		return v[0].startsWith(v[1]);
	}
	
	/**
	 * 是否在里面。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean in(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(value == conditionValue){
			return true;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
					
		String v[] = objsToString(type, pattern, value, conditionValue);
		for(String cv : v[1].split("[,]")){
			if(UtilCondition.eq(v[0], cv, type, pattern, ignoreNull, actionContext)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 是否在不在里面。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean notin(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(value == conditionValue){
			return false;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return true;
		}
		if(value == null && conditionValue != null){
			return true;
		}
					
		String v[] = objsToString(type, pattern, value, conditionValue);
		for(String cv : v[1].split("[,]")){
			if(UtilCondition.eq(v[0], cv, type, pattern, ignoreNull, actionContext)){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 正则表达式。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean regex(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value == null && conditionValue == null){
			return false;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
					
		String v[] = objsToString(type, pattern, value, conditionValue);
		return v[0].matches(v[1]);
	}
	
	/**
	 * 通过Java的equals比较。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean equals(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value == null && conditionValue == null){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
					
		return value.equals(conditionValue);
	}
	
	/**
	 * 是否为null。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean isnull(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		return value == null;
	}
	
	/**
	 * 是否部位null。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean notnull(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		return value != null;
	}
	
	/**
	 * 左右类似。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean lrlike(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		if(value == conditionValue){
			return true;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}	   
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			
		String v[] = objsToString(type, pattern, value, conditionValue);
		return v[0].indexOf(v[1]) != -1;
	}
	
	private static String[] objsToString(String type, String pattern, Object value, Object conditionValue){
		String[] vs = new String[2];
		if("date".equals(type)){
			DateFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new SimpleDateFormat(pattern);
			}else{
				sf = SimpleDateFormat.getInstance();
			}
			if(value instanceof Date && conditionValue instanceof String){
				vs[0] = sf.format((Date) value);
				vs[1] = (String) conditionValue;
			}else if(value instanceof String && conditionValue instanceof Date){
				vs[0] = (String) value;
				vs[1] = sf.format((Date) conditionValue);
			}else{
			    vs[0] = value.toString();
			    vs[1] = conditionValue.toString();
			}
		}else if("number".equals(type)){
			NumberFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new DecimalFormat(pattern);
			}else{
				sf = NumberFormat.getInstance();
			}
			if(value instanceof Number && conditionValue instanceof String){
				vs[0] = sf.format((Date) value);
				vs[1] = (String) conditionValue;
			}else if(value instanceof String && conditionValue instanceof Number){
				vs[0] = (String) value;
				vs[1] = sf.format((Date) conditionValue);
			}else{
			    vs[0] = value.toString();
			    vs[1] = conditionValue.toString();
			}
		}else{
			vs[0] = value.toString();
			vs[1] = conditionValue.toString();
		}
		
		return vs;
	}
	
	/**
	 * 比较两个值不相等。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean uneq(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		try{
			return !UtilCondition.eq1(value, conditionValue, type, pattern, ignoreNull, actionContext);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 比较两个值是否相等。
	 * 
	 * @param value 值
	 * @param conditionValue 条件值
	 * @param type 类型
	 * @param pattern 模式
	 * @param ignoreNull 是否忽略null
	 * @param actionContext 变量上下文
	 * @return 比对结果
	 */
	public static boolean eq(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		try{
			return UtilCondition.eq1(value, conditionValue, type, pattern, ignoreNull, actionContext);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 比对两个值是否相等。
	 * 
	 * @param value 值
	 * @param conditionValue 条件值
	 * @param type 类型
	 * @param pattern 模式
	 * @param ignoreNull 是否忽略null
	 * @param actionContext 变量上下文
	 * @return 比对结果
	 * @throws ParseException 分析异常
	 */
	private static boolean eq1(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext) throws ParseException{	    
		if(value == conditionValue){
			return true;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			
		if(value.equals(conditionValue)){
			return true;
		}else{
			if("date".equals(type)){
				DateFormat sf = null;
				if(pattern != null && !"".equals(pattern)){
					sf = new SimpleDateFormat(pattern);
				}else{
					sf = SimpleDateFormat.getInstance();
				}
				if(value instanceof Date && conditionValue instanceof String){
					Date v1 = (Date) value;
					Date v2 = sf.parse((String) conditionValue);				   
					return v1.getTime() == v2.getTime();
				}else if(value instanceof String && conditionValue instanceof Date){
					Date v1 = sf.parse((String) value);
					Date v2 = (Date) conditionValue;
					return v1.getTime() == v2.getTime();
				}
			}else if("number".equals(type)){
				NumberFormat sf = null;
				if(pattern != null && !"".equals(pattern)){
					sf = new DecimalFormat(pattern);
				}else{
					sf = NumberFormat.getInstance();
				}				

				if(value instanceof Number && conditionValue instanceof String){
				    Number v1 = (Number) value;
				    Number v2 = sf.parse((String) conditionValue);				   
					return v1.doubleValue() == v2.doubleValue();
				}else if(value instanceof String && conditionValue instanceof Number){
				    Number v1 = sf.parse((String) value);
				    Number v2 = (Number) conditionValue;
					return v1.doubleValue() == v2.doubleValue();
				}
			}else if("boolean".equals(type)){
				if(value instanceof Boolean && conditionValue instanceof String){
					String v2 = ((String) conditionValue).trim().toLowerCase();
					boolean bv2 = v2.equals(true) || v2.equals(1);
					return ((Boolean) value).booleanValue() == bv2;
				}else if(value instanceof String && conditionValue instanceof Boolean){
					String v2 = ((String) value).trim().toLowerCase();
					boolean bv2 = v2.equals(true) || v2.equals(1);
					return ((Boolean) conditionValue).booleanValue() == bv2;
				}
			}else if(value.toString().equals(conditionValue.toString())){
			    return true;
			}
			
			return false;
		}	
	}
	
	/**
	 * 比较大于等于。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean gteq(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		return UtilCondition.eq(value, conditionValue, type, pattern, ignoreNull, actionContext) 
				|| UtilCondition.gt(value, conditionValue, type, pattern, ignoreNull, actionContext);
	}
	
	/**
	 * 比较大于。
	 * 
	 * @param value 值
	 * @param conditionValue 条件值
	 * @param type 类型
	 * @param pattern 模式
	 * @param ignoreNull 是否忽略null
	 * @param actionContext 变量上下文
	 * @return 结果
	 */
	public static boolean gt(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		try{
			return UtilCondition.gt1(value, conditionValue, type, pattern, ignoreNull, actionContext);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 比较值大于。
	 * 
	 * @param value 值
	 * @param conditionValue 条件值
	 * @param type 类型
	 * @param pattern 模式
	 * @param ignoreNull 是否忽略null
	 * @param actionContext 变量上下文
	 * @return 结果
	 * @throws ParseException 分析异常
	 */
	@SuppressWarnings("unchecked")
	private static boolean gt1(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext) throws ParseException{
		if(value == conditionValue){
			return false;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			

		if("date".equals(type)){
			DateFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new SimpleDateFormat(pattern);
			}else{
				sf = SimpleDateFormat.getInstance();
			}
			if(value instanceof Date && conditionValue instanceof String){
				Date v1 = (Date) value;
				Date v2 = sf.parse((String) conditionValue);				   
				return v1.getTime() > v2.getTime();
			}else if(value instanceof String && conditionValue instanceof Date){
				Date v1 = sf.parse((String) value);
				Date v2 = (Date) conditionValue;
				return v1.getTime() > v2.getTime();
			}
		}else if("number".equals(type)){
			NumberFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new DecimalFormat(pattern);
			}else{
				sf = NumberFormat.getInstance();
			}				

			if(value instanceof Number && conditionValue instanceof String){
			    Number v1 = (Number) value;
			    Number v2 = sf.parse((String) conditionValue);				   
				return v1.doubleValue() > v2.doubleValue();
			}else if(value instanceof String && conditionValue instanceof Number){
			    Number v1 = sf.parse((String) value);
			    Number v2 = (Number) conditionValue;
				return v1.doubleValue() > v2.doubleValue();
			}
		}else if(value instanceof Comparable){
			return ((Comparable) value).compareTo(conditionValue) > 0;
		}
		
		return false;
	}
	
	/**
	 * 比较两个值小于。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean lt(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		try{
			return UtilCondition.lt1(value, conditionValue, type, pattern, ignoreNull, actionContext);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 比较小于等于。
	 * 
	 * @param value
	 * @param conditionValue
	 * @param type
	 * @param pattern
	 * @param ignoreNull
	 * @param actionContext
	 * @return
	 */
	public static boolean lteq(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext){
		return UtilCondition.lt(value, conditionValue, type, pattern, ignoreNull, actionContext) ||
				UtilCondition.eq(value, conditionValue, type, pattern, ignoreNull, actionContext);
	}
	
	/**
	 * 比较两个值小于。
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private static boolean lt1(Object value, Object conditionValue, String type, String pattern, boolean ignoreNull, ActionContext actionContext) throws ParseException{
		if(value == conditionValue){
			return false;
		}
		if(conditionValue == null && ignoreNull){
			return true;
		}
		if(value != null && conditionValue == null){
			return false;
		}
		if(value == null && conditionValue != null){
			return false;
		}
			

		if("date".equals(type)){
			DateFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new SimpleDateFormat(pattern);
			}else{
				sf = SimpleDateFormat.getInstance();
			}
			if(value instanceof Date && conditionValue instanceof String){
				Date v1 = (Date) value;
				Date v2 = sf.parse((String) conditionValue);				   
				return v1.getTime() < v2.getTime();
			}else if(value instanceof String && conditionValue instanceof Date){
				Date v1 = sf.parse((String) value);
				Date v2 = (Date) conditionValue;
				return v1.getTime() < v2.getTime();
			}
		}else if("number".equals(type)){
			NumberFormat sf = null;
			if(pattern != null && !"".equals(pattern)){
				sf = new DecimalFormat(pattern);
			}else{
				sf = NumberFormat.getInstance();
			}				

			if(value instanceof Number && conditionValue instanceof String){
			    Number v1 = (Number) value;
			    Number v2 = sf.parse((String) conditionValue);				   
				return v1.doubleValue() < v2.doubleValue();
			}else if(value instanceof String && conditionValue instanceof Number){
			    Number v1 = sf.parse((String) value);
			    Number v2 = (Number) conditionValue;
				return v1.doubleValue() < v2.doubleValue();
			}
		}else if(value instanceof Comparable){
			return ((Comparable) value).compareTo(conditionValue) < 0;
		}
		
		return false;
	}
	
	public static String getOperator(Thing condition){
		byte operator = condition.getByte("operator");
        switch(operator){
            case UtilCondition.eq:
                return "=";
            case UtilCondition.uneq:
            	return "<>";
            case UtilCondition.gt:
            	return ">";
            case UtilCondition.gteq:
            	return ">=";
            case UtilCondition.lt:
            	return "<";
            case UtilCondition.lteq:
            	return "<=";
            case UtilCondition.like:
            	return "like";
            case UtilCondition.llike:
            	return "like";
            case UtilCondition.rlike:
            	return "like";
            case UtilCondition.lrlike:
            	return "like";
            case UtilCondition.in:
                return "in";
            case UtilCondition.notin:
            	return "not in";
            case UtilCondition.between:
                return "between";
            case UtilCondition.isnull:
            	return "is null";
            case UtilCondition.notnull:
            	return "is not null";
        }
        
        return "=";
	}
	
	/**
	 * 获取条件设置的查询条件的值。
	 * 
	 * @param condition
	 * @param actionContext
	 * @return
	 * @throws ParseException 
	 * @throws OgnlException 
	 */
	public static Object getConditionValue(Thing condition, ActionContext actionContext) throws OgnlException, ParseException{
		Object datas = actionContext.get("datas");
		
		return  ConditionCreator.getConditionValue(condition, condition.getString("dataName"), datas, actionContext);
	}
	
	/**
	 * 添加一个要执行的查询条件,这个值是要设置到查询中的。
	 * 
	 * @param condition
	 * @param value
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void addConditionValue(Thing condition, Object value, ActionContext actionContext){
		List<Map<String, Object>> cds = (List<Map<String, Object>>) actionContext.get("cds");
	    Map<String, Object> cd = UtilMap.toMap(new Object[]{"name",condition.getString("attributeName"), "value",value, "operator",getOperator(condition), "condition", condition});
        cds.add(cd);
	}
}