package xworker.dataObject.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.OgnlException;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import org.xmeta.util.UtilMap;
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
	Condition parent;
	Object conditionValue;
	List<Condition> childs = new ArrayList<>();
	
	public Condition() {
		conditionThing = new Thing("xworker.dataObject.query.Condition");
	}

	public Condition(Thing conditionThing){
		this.conditionThing = conditionThing;
	}

	public Condition(Condition parent, Thing conditionThing) {
		this.parent = parent;
		this.conditionThing = conditionThing;
	}

	/**
	 * 添加一个eq条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Condition eq(String name, Object value, Object ... params){
		this.sadd(name, Condition.eq, value, params);

		return this;
	}

	/**
	 * 添加一个uneq条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Condition uneq(String name, Object value, Object ... params){
		this.sadd(name, Condition.uneq, value, params);

		return this;
	}

	/**
	 * 添加一个gt条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public  Condition gt(String name, Object value, Object ... params){
		this.sadd(name, Condition.gt, value, params);

		return this;
	}

	public Condition gteq(String name, Object value, Object ... params){
		this.sadd(name, Condition.gteq, value, params);

		return this;
	}

	public Condition lt(String name, Object value, Object ... params){
		this.sadd(name, Condition.lt, value, params);

		return this;
	}

	public Condition lteq(String name, Object value, Object ... params){
		this.sadd(name, Condition.lteq, value, params);

		return this;
	}

	public Condition like(String name, Object value, Object ... params){
		this.sadd(name, Condition.like, value, params);
		return this;
	}

	public Condition llike(String name, Object value, Object ... params){
		this.sadd(name, Condition.llike, value, params);

		return this;
	}

	public Condition rlike(String name, Object value, Object ... params){
		this.sadd(name, Condition.rlike, value, params);

		return this;
	}

	public Condition lrlike(String name, Object value, Object ... params){
		this.sadd(name, Condition.lrlike, value, params);

		return this;
	}

	public Condition in(String name, Object ... values){
		this.sadd(name, Condition.in, values);

		return this;
	}

	public Condition in(String name, Map<String, Object> params,  Object ... values){
		this.sadd(name, Condition.in, values, params);

		return this;
	}

	public Condition regex(String name, String pattern, Object ... params){
		this.sadd(name, Condition.regex, pattern, params);

		return this;
	}

	public Condition equals(String name, Object value, Object ... params){
		this.sadd(name, Condition.equals, value, params);
		return this;
	}

	public Condition isnull(String name, Object value, Object ... params){
		this.sadd(name, Condition.isnull, value, params);

		return this;
	}

	public Condition notnull(String name, Object value, Object ... params){
		this.sadd(name, Condition.notnull, value, params);

		return this;
	}

	public Condition between(String name, Object value1, Object value2, Object ... params){
		this.sadd(name, Condition.between, new Object[]{value1, value2}, params);

		return  this;
	}

	public Condition notin(String name,  Object ... value){
		this.sadd(name, Condition.notin, value);

		return this;
	}

	public Condition notin(String name, Map<String, Object> params,  Object ... value){
		this.sadd(name, Condition.notin, value, params);

		return this;
	}

	/**
	 * 添加一个eq条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Condition oreq(String name, Object value, Object ... params){
		this.sadd(name, Condition.eq, value, params).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个uneq条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public Condition oruneq(String name, Object value, Object ... params){
		this.sadd(name, Condition.uneq, value, params).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个gt条件，并返回自己。
	 *
	 * @param name
	 * @param value
	 * @return
	 */
	public  Condition orgt(String name, Object value, Object ... params){
		this.sadd(name, Condition.gt, value, params).setJoin(OR);

		return this;
	}

	public Condition orgteq(String name, Object value, Object ... params){
		this.sadd(name, Condition.gteq, value, params).setJoin(OR);

		return this;
	}

	public Condition orlt(String name, Object value, Object ... params){
		this.sadd(name, Condition.lt, value, params).setJoin(OR);

		return this;
	}

	public Condition orlteq(String name, Object value, Object ... params){
		this.sadd(name, Condition.lteq, value, params).setJoin(OR);

		return this;
	}

	public Condition orlike(String name, Object value, Object ... params){
		this.sadd(name, Condition.like, value, params).setJoin(OR);
		return this;
	}

	public Condition orllike(String name, Object value, Object ... params){
		this.sadd(name, Condition.llike, value, params).setJoin(OR);

		return this;
	}

	public Condition orrlike(String name, Object value, Object ... params){
		this.sadd(name, Condition.rlike, value, params).setJoin(OR);

		return this;
	}

	public Condition orlrlike(String name, Object value, Object ... params){
		this.sadd(name, Condition.lrlike, value, params).setJoin(OR);

		return this;
	}

	public Condition orin(String name, Object ... values){
		this.sadd(name, Condition.in, values).setJoin(OR);

		return this;
	}

	public Condition orin(String name, Map<String, Object> params, Object ... values){
		this.sadd(name, Condition.in, values, params).setJoin(OR);

		return this;
	}

	public Condition orregex(String name, String pattern, Object ... params){
		this.sadd(name, Condition.regex, pattern, params).setJoin(OR);

		return this;
	}

	public Condition orequals(String name, Object value, Object ... params){
		this.sadd(name, Condition.equals, value, params).setJoin(OR);
		return this;
	}

	public Condition orisnull(String name, Object value, Object ... params){
		this.sadd(name, Condition.isnull, value, params).setJoin(OR);

		return this;
	}

	public Condition ornotnull(String name, Object value, Object ... params){
		this.sadd(name, Condition.notnull, value, params).setJoin(OR);

		return this;
	}

	public Condition orbetween(String name, Object value1, Object value2, Object ... params){
		this.sadd(name, Condition.between, new Object[]{value1, value2}, params).setJoin(OR);

		return  this;
	}

	public Condition ornotin(String name, Map<String, Object> params, Object ... value){
		this.sadd(name, Condition.notin, value, params).setJoin(OR);

		return this;
	}

	public Condition ornotin(String name, Object ... value){
		this.sadd(name, Condition.notin, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加并返回一个and子条件。
	 *
	 * @return
	 */
	public Condition and(){
		Thing con = DataObjectUtil.createConditionThing(null, null, eq, AND);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		childs.add(condition);
		return condition;
	}

	/**
	 * 添加并返回一个or子条件。
	 *
	 * @return
	 */
	public Condition or(){
		Thing con = DataObjectUtil.createConditionThing(null, null, eq, OR);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		childs.add(condition);
		return condition;
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

		Condition condition = new Condition(this, con);
		condition.setConditionValue(dataName, value);
		childs.add(condition);
		return condition;
	}

	public Condition add(String attrName, byte operator, Object value, Object ... params){
		Thing con = DataObjectUtil.createConditionThing(attrName, null, operator, null);
		conditionThing.addChild(con);

		Map<String, Object> ps = UtilMap.toMap(params);
		conditionThing.getAttributes().putAll(ps);

		String dataName = (String) ps.get("dataName");
		Condition condition = new Condition(this, con);
		condition.setConditionValue(dataName, value);
		childs.add(condition);
		return condition;
	}

	public Condition sadd(String attrName, byte operator, Object value, Object ... params){
		Thing con = DataObjectUtil.createConditionThing(attrName, null, operator, null);
		conditionThing.addChild(con);

		Map<String, Object> ps = UtilMap.toMap(params);
		conditionThing.getAttributes().putAll(ps);

		String dataName = (String) ps.get("dataName");
		Condition condition = new Condition(this, con);
		condition.setConditionValue(dataName, value);
		childs.add(condition);
		return this;
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

		Condition condition = new Condition(this, con);
		condition.setConditionValue(dataName, value);
		childs.add(condition);

		return this;
	}
	
	public void setJoin(String join) {
		conditionThing.set("join", join);
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
	public void setConditionValue(String dataName, Object value) {
		this.conditionThing.set("dataName", dataName);
		this.conditionValue = value;
	}

	public void setConditionValue(Object value){
		this.conditionValue = value;
	}

	public Thing getConditionThing() {
		return conditionThing;
	}

	/**
	 * 把条件的值，包括子节点的值保存到一个Map中并返回。
	 *
	 * @return
	 */
	public Map<String, Object> getConditionValues() {
		Map<String, Object> values = new HashMap<>();
		initConditionValuesMap(values);
		return values;
	}

	private void initConditionValuesMap(Map<String, Object> values){
		if(conditionValue != null){
			String dataName = getDataName();
			values.put(dataName, conditionValue);
		}

		for(Condition child : childs){
			child.initConditionValuesMap(values);
		}
	}

	/**
	 * 返回属性名称，一般是属性的实体的字段名。
	 *
	 * @return
	 */
	public String getAttributeName(){
		return conditionThing.getString("attributeName");
	}

	/**
	 * 返回数据名，数据名是条件参数Map中保存查询条件的值的key。
	 *
	 * @return
	 */
	public String getDataName(){
		String dataName = conditionThing.getString("dataName");
		if(dataName == null || dataName.isEmpty()){
			return getAttributeName();
		}else{
			return dataName;
		}
	}

	/**
	 * 返回条件的值。
	 * @return
	 */
	public Object getConditionValue(){
		return conditionValue;
	}

	/**
	 * 返回操作符。
	 *
	 * @return
	 */
	public byte getOperator(){
		return conditionThing.getByte("operator");
	}

	/**
	 * 返回条件的连接方式，AND或OR。
	 *
	 * @return
	 */
	public String getJoin(){
		String join = conditionThing.getString("join");
		if(join == null || join.isEmpty()){
			return AND;
		}else{
			return join;
		}
	}

	/**
	 * 返回条件是否生效。
	 * @return
	 */
	public boolean isActive(){
		boolean isActive = false;
		for(Condition child : childs){
			if(child.isActive()){
				isActive = true;
				break;
			}
		}

		if(isActive){
			return isActive;
		}else {
			Object conditionValue = getConditionValue();
			String attributeName = getAttributeName();
			if (attributeName == null || attributeName.isEmpty()) {
				return false;
			}

			if(conditionValue != null || !getConditionThing().getBoolean("ignoreNull")){
				return true;
			}else{
				return false;
			}
		}


	}

	/**
	 * 返回子节点列表。
	 *
	 * @return
	 */
	public List<Condition> getChilds(){
		return childs;
	}
	/**
	 * 从模型和已有的查询条件中分析生成查询条件对象。
	 *
	 * @param conditionThing
	 * @param values
	 * @param actionContext
	 * @return
	 */
	public static Condition parse(Thing conditionThing, Map<String, Object> values, ActionContext actionContext) {
		return parse(null, conditionThing, values, actionContext);
	}

	private static Condition parse(Condition parent, Thing conditionThing, Map<String, Object> values, ActionContext actionContext){
		Condition condition = new Condition(conditionThing);
		if(parent != null){
			parent.childs.add(condition);
		}

		try {
			condition.setConditionValue(ConditionCreator.getConditionValue(conditionThing, condition.getDataName(), values, actionContext));

			for(Thing childThing : conditionThing.getChilds()){
				parse(condition, childThing, values, actionContext);
			}

			return condition;
		}catch(Exception e){
			throw new ActionException(e);
		}
	}

	public static Condition create(){
		return new Condition();
	}
}
