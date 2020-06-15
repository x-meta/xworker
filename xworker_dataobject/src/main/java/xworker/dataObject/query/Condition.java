package xworker.dataObject.query;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.Thing;

import xworker.dataObject.utils.DataObjectUtil;

public class Condition {
	/** = */
	public static final byte eq = 1;
	/** ！= */
	public static final byte uneq = 2;
	/** &gt; */
	public static final byte gt = 3;
	/** &gt;= */
	public static final byte gteq = 4;
	/** &lt; */
	public static final byte lt = 5;
	/** &lt;= */
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
	
	Thing conditionThing = null;
	Map<String, Object> conditionValues = null;
	Condition parent;
	
	public Condition() {
		conditionThing = new Thing("xworker.dataObject.query.Condition");
		conditionValues = new HashMap<String, Object>();	}
	
	public Condition(Condition parent, Thing conditionThing) {
		this.parent = parent;
		this.conditionThing = conditionThing;
	}
	
	/**
	 * 添加一个空的查询节点，返回新建的查询节点。
	 * 
	 * @return
	 */
	public Condition add() {
		return add(null, null, (byte) 0, null, "string", UtilCondition.AND);
	}
	
	/**
	 * 添加一个查询节点，返回新建的查询节点。
	 * 
	 * @param name 属性和条件名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @return
	 */
	public Condition add(String name, byte operator, Object value) {
		return add(name, name, operator, value, "string", UtilCondition.AND);
	}
	
	/**
	 * 添加一个查询节点，返回新建的查询节点。
	 * 
	 * @param name 属性和条件名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @param type 属性的值类型
	 * @return
	 */
	public Condition add(String name, byte operator, Object value, String type) {
		return add(name, name, operator, value, type, UtilCondition.AND);
	}
	
	/**
	 * <p>添加一个查询节点，返回新建的查询节点。</p>
	 * 
	 * <p>对于同一个字段有多个条件，不要使用此方法，要使用能够设置条件名字的方法。</p>
	 * 
	 * @param attrName 属性名
	 * @param dataName 条件值的变量名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @param type 属性的值类型
	 * @param join 连接类型，and或or
	 * @return
	 */
	public Condition add(String attrName, String dataName , byte operator, Object value, String type, String join) {
		Thing con = DataObjectUtil.createConditionThing(attrName, dataName, operator, join);
		con.set("type", type);
		conditionThing.addChild(con);
		
		setDataValue(dataName, value);
		
		return new Condition(this,con);
	}
	
	/**
	 * 添加一个空的查询节点，返回当前查询节点。
	 * 
	 * @return
	 */
	public Condition sadd() {
		return sadd(null, null, (byte) 0, null, "string", UtilCondition.AND);
	}
	
	/**
	 * 添加一个查询节点，返回当前查询节点。
	 * 
	 * @param name 属性和条件名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @return
	 */
	public Condition sadd(String name, byte operator, Object value) {
		return sadd(name, name, operator, value, "string", UtilCondition.AND);
	}
	
	/**
	 * 添加一个查询节点，返回当前查询节点。
	 * 
	 * @param name 属性和条件名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @param type 属性的值类型
	 * @return
	 */
	public Condition sadd(String name, byte operator, Object value, String type) {
		return sadd(name, name, operator, value, type, UtilCondition.AND);
	}
	
	/**
	 * 添加一个查询节点，返回当前查询节点。
	 * 
	 * @param attrName 属性名
	 * @param dataName 条件值的变量名
	 * @param operator 比较操作符
	 * @param value 条件值
	 * @param type 属性的值类型
	 * @param join 连接类型，and或or
	 * @return
	 */
	public Condition sadd(String attrName, String dataName , byte operator, Object value, String type, String join) {
		Thing con = DataObjectUtil.createConditionThing(attrName, dataName, operator, join);
		con.set("type", type);
		conditionThing.addChild(con);
		
		setDataValue(dataName, value);
		
		new Condition(this,con);
		
		return this;
	}
	
	public void setJoin(String join) {
		conditionThing.set("join", join);
	}
	
	public void setData(String dataName, Object value) {
		setDataValue(dataName, value);
	}
	
	public void setOperator(byte operator) {
		conditionThing.put("operator", String.valueOf(operator));
	}
	
	public void setAttributeName(String attributeName) {
		conditionThing.set("attributeName", attributeName);
	}
	
	public void setType(String type) {
		conditionThing.set("type", type);
	}
	
	public void set(String name, Object value) {
		conditionThing.set(name, value);
	}
	/**
	 * 设置查询条件的值。
	 * 
	 * @param dataName
	 * @param value
	 */
	public void setDataValue(String dataName, Object value) {
		if(parent != null) {
			parent.setDataValue(dataName, value);
		}else {
			conditionValues.put(dataName, value);
		}
	}

	public Thing getConditionThing() {
		return conditionThing;
	}

	public Map<String, Object> getConditionValues() {
		return conditionValues;
	}
}
