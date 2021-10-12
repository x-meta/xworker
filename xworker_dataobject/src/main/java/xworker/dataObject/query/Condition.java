package xworker.dataObject.query;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.executor.Executor;

import java.util.*;

/**
 * 用于匹配数据对象的查询条件。可以用于sql和非sql，一些sql的条件在非sql下可能会不生效，一些用于非sql的条件可能也不适用于sql。
 *
 * 条件是树形结构的，一个条件下可以添加子条件。
 *
 * 和属性相关的条件，如果没有设置条件值，那么该条件一般不生效。
 */
public class Condition {
	private static final String TAG = Condition.class.getName();

	/** 当条件为字符串时，如果字符串为空，是否忽略 */
	private static boolean global_ignore_blank_string = true;
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
    /** not between */
	public static final byte notbetween = 18;
    /** 自定义 */
    public static final byte selfDefine = 100;
	public static final String AND = "and";
	public static final String OR = "or";
	
	Thing conditionThing;
	Condition parent;
	Object conditionValue;
	List<Condition> childs = new ArrayList<>();
	Map<String, Integer> dataIdMap;
	boolean ingoreBlankString = global_ignore_blank_string;

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

	public static boolean isGlobalIngoreBlankString() {
		return global_ignore_blank_string;
	}

	public static void setGlobalIngoreBlankString(boolean ingoreBlankString) {
		Condition.global_ignore_blank_string = ingoreBlankString;
	}

	public boolean isIngoreBlankString() {
		return ingoreBlankString;
	}

	public void setIngoreBlankString(boolean ingoreBlankString) {
		this.ingoreBlankString = ingoreBlankString;
	}

	/**
	 * 添加一个等于子条件，并返回当前条件。
	 */
	public Condition eq(String name, Object value){
		this.sadd(name, Condition.eq, value);

		return this;
	}

	/**
	 * 添加一个不等于子条件，并返回当前条件。
	 */
	public Condition uneq(String name, Object value){
		this.sadd(name, Condition.uneq, value);

		return this;
	}

	/**
	 * 添加一个大于子条件，并返回当前条件。
	 */
	public  Condition gt(String name, Object value){
		this.sadd(name, Condition.gt, value);

		return this;
	}

	/**
	 * 添加一个大于等于子条件，并返回当前条件。
	 */
	public Condition gteq(String name, Object value){
		this.sadd(name, Condition.gteq, value);

		return this;
	}

	/**
	 * 添加一个小于子条件，并返回当前条件。
	 */
	public Condition lt(String name, Object value){
		this.sadd(name, Condition.lt, value);

		return this;
	}

	/**
	 * 添加一个小于等于子条件，并返回当前条件。
	 */
	public Condition lteq(String name, Object value){
		this.sadd(name, Condition.lteq, value);

		return this;
	}

	/**
	 * 添加一个like子条件，并返回当前条件。
	 * 如果是sql，那么在条件的值中加%，如:name like '%abc'，值是'%abc'。如果是其它，使用字符串的contains(value)来判断。
	 */
	public Condition like(String name, Object value){
		this.sadd(name, Condition.like, value);
		return this;
	}

	/**
	 * 添加一个like子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值前加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'abc%'。如果是其它，使用字符串的startsWith(vlaue)判断。
	 */
	public Condition llike(String name, Object value){
		this.sadd(name, Condition.llike, value);

		return this;
	}

	/**
	 * 添加一个like子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值后加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'%abc'。如果时其它，使用字符串的endsWith(value)判断。
	 */
	public Condition rlike(String name, Object value){
		this.sadd(name, Condition.rlike, value);

		return this;
	}

	/**
	 * 添加一个like子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值前后加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'%abc%'。如果是其它，使用字符串的contains(value)判断。
	 */
	public Condition lrlike(String name, Object value){
		this.sadd(name, Condition.lrlike, value);

		return this;
	}

	/**
	 * 添加一个in子条件，返回当前条件。
	 */
	public Condition in(String name, Object ... values){
		this.sadd(name, Condition.in, values);

		return this;
	}

	/**
	 * 添加一个正则表达式子条件，返回当前条件。
	 *
	 * 注意sql不支持regex。
	 */
	public Condition regex(String name, String pattern){
		this.sadd(name, Condition.regex, pattern);

		return this;
	}

	/**
	 * 添加一个等于子条件，返回当前条件。
	 *
	 * 如果不是sql，使用对象的equlas(value)来判断。
	 */
	public Condition equals(String name, Object value){
		this.sadd(name, Condition.equals, value);
		return this;
	}

	/**
	 * 添加一个是否为null的子条件，返回当前条件。
	 */
	public Condition isnull(String name){
		this.sadd(name, Condition.isnull, null);

		return this;
	}

	/**
	 * 添加一个是否不为null的子条件，返回当前条件。
	 */
	public Condition notnull(String name){
		this.sadd(name, Condition.notnull, null);

		return this;
	}

	/**
	 * 添加一个between子条件，返回当前条件。
	 */
	public Condition between(String name, Object value1, Object value2){
		this.sadd(name, Condition.between, new Object[]{value1, value2});

		return  this;
	}

	/**
	 * 添加一个不在指定的值里的条件，返回当前条件。
	 */
	public Condition notin(String name,  Object ... value){
		this.sadd(name, Condition.notin, value);

		return this;
	}

	/**
	 * 添加一个或者等于的子条件，并返回当前条件。
	 */
	public Condition oreq(String name, Object value){
		this.sadd(name, Condition.eq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者不等于条件，并返当前条件。
	 */
	public Condition oruneq(String name, Object value){
		this.sadd(name, Condition.uneq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者大于子条件，并返回当前条件。
	 */
	public  Condition orgt(String name, Object value){
		this.sadd(name, Condition.gt, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者大于等于子条件，并返回当前条件。
	 */
	public Condition orgteq(String name, Object value){
		this.sadd(name, Condition.gteq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者小于子条件，并返回当前条件。
	 */
	public Condition orlt(String name, Object value){
		this.sadd(name, Condition.lt, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者小于等于子条件，并返回当前条件。
	 */
	public Condition orlteq(String name, Object value){
		this.sadd(name, Condition.lteq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者like子条件，并返回当前条件。
	 * 如果是sql，那么在条件的值中加%，如:name like '%abc'，值是'%abc'。如果是其它，使用字符串的contains(value)来判断。
	 */
	public Condition orlike(String name, Object value){
		this.sadd(name, Condition.like, value).setJoin(OR);
		return this;
	}

	/**
	 * 添加一个或者like子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值前加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'abc%'。如果是其它，使用字符串的startsWith(vlaue)判断。
	 */
	public Condition orllike(String name, Object value){
		this.sadd(name, Condition.llike, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个like或者子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值后加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'%abc'。如果时其它，使用字符串的endsWith(value)判断。
	 */
	public Condition orrlike(String name, Object value){
		this.sadd(name, Condition.rlike, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者like子条件，返回当前条件。
	 *
	 * 如果是sql，那么会在条件的值前后加%。如条件的值是'abc'，那么在设置sql的查询条件时改为'%abc%'。如果是其它，使用字符串的contains(value)判断。
	 */
	public Condition orlrlike(String name, Object value){
		this.sadd(name, Condition.lrlike, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者in子条件，返回当前条件。
	 */
	public Condition orin(String name, Object ... values){
		this.sadd(name, Condition.in, values).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者正则表达式子条件，返回当前条件。
	 *
	 * 注意sql不支持regex。
	 */
	public Condition orregex(String name, String pattern){
		this.sadd(name, Condition.regex, pattern).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者等于子条件，返回当前条件。
	 *
	 * 如果不是sql，使用对象的equlas(value)来判断。
	 */
	public Condition orequals(String name, Object value){
		this.sadd(name, Condition.equals, value).setJoin(OR);
		return this;
	}

	/**
	 * 添加一个或者是否为null的子条件，返回当前条件。
	 */
	public Condition orisnull(String name, Object value){
		this.sadd(name, Condition.isnull, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者是否不为null的子条件，返回当前条件。
	 */
	public Condition ornotnull(String name, Object value){
		this.sadd(name, Condition.notnull, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个或者between子条件，返回当前条件。
	 */
	public Condition orbetween(String name, Object value1, Object value2){
		this.sadd(name, Condition.between, new Object[]{value1, value2}).setJoin(OR);

		return  this;
	}

	/**
	 * 添加一个或者不在指定的值里的条件，返回当前条件。
	 */
	public Condition ornotin(String name, Object ... value){
		this.sadd(name, Condition.notin, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个and子条件，并返回该子条件。
	 */
	public Condition and(){
		Thing con = DataObjectUtil.createConditionThing(null, null, eq, AND);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		childs.add(condition);
		return condition;
	}

	/**
	 * 添加一个or子条件，并返回该子条件。
	 */
	public Condition or(){
		Thing con = DataObjectUtil.createConditionThing(null, null, eq, OR);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		childs.add(condition);
		return condition;
	}

	/**
	 * 添加一个sql语句子条件，返回当前条件。
	 *
	 * 不管有没有值，该sql语句总是会添加到条件sql里。如果sql语句中有占位符?，那么一定要设置匹配的值。
	 */
	public Condition sql(String name, String sql, Object ... value){
		Condition con = this.sadd(name, Condition.eq, value);
		con.setJoin(AND);
		con.set("selfDefineSql", sql);
		con.set("alwaysAddClause", "true");

		return this;
	}

	/**
	 * 添加一个或者连接的sql语句子条件，返回当前条件。
	 *
	 * 不管有没有值，该sql语句总是会添加到条件sql里。如果sql语句中有占位符?，那么一定要设置匹配的值。
	 */
	public Condition orsql(String name, String sql, Object ... value){
		Condition con = this.sadd(name, Condition.eq, value);
		con.setJoin(OR);
		con.set("selfDefineSql", sql);
		con.set("alwaysAddClause", "true");

		return this;
	}

	/**
	 * 添加一个和字段相关相关的子条件，并返回该子条件。
	 *
	 * 比如要生成:id in (select id from xxx where name=?)这样的条件语句，那么：clause("id", Condition.in, "select id from xxx").eq("name", avalue);
	 */
	public Condition clause(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(AND);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);

		return con;
	}

	/**
	 * 添加一个或者连接的和字段相关相关的子条件，并返回该子条件。
	 *
	 * 比如要生成:or id in (select id from xxx where name=?)这样的条件语句，那么：orclause("id", Condition.in, "select id from xxx").eq("name", avalue);
	 */
	public Condition orclause(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(OR);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);

		return con;
	}

	/**
	 * 添加一个和字段相关相关的子条件，并返回该子条件。
	 *
	 * 比如要生成: id in (select id from xxx where name=? and age=10)这样的条件语句，
	 * 那么：clauseTemplate("id", Condition.in, "select id from xxx where %%SQL%% and age =10").eq("name", avalue);
	 */
	public Condition clauseTemplate(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(AND);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);
		con.set("isClauseTemplate", "true");

		return con;
	}

	/**
	 * 添加一个或者连接的和字段相关相关的子条件，并返回该子条件。
	 *
	 * 比如要生成:or id in (select id from xxx where name=? and age=10)这样的条件语句，
	 * 那么：orclauseTemplate("id", Condition.in, "select id from xxx where %%SQL%% and age =10").eq("name", avalue);
	 */
	public Condition orclauseTemplate(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(OR);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);
		con.set("isClauseTemplate", "true");

		return con;
	}

	/**
	 * 第一个符合名字的条件，可能是自己或子节点。
	 * @param name 条件的名字
	 * @return 条件
	 */
	public Condition getCondition(String name){
		if(name == null){
			return null;
		}

		if(name.equals(conditionThing.getMetadata().getName())){
			return this;
		}

		for(Condition child : childs){
			Condition con = child.getCondition(name);
			if(con != null){
				return con;
			}
		}

		return null;
	}

	private String createDataName(String name){
		//使用根节点的
		Condition con = this;
		while(con.parent != null){
			con = con.parent;
		}

		Map<String, Integer> dataIdMap = con.dataIdMap;
		if(dataIdMap == null){
			dataIdMap = new HashMap<>();
			con.dataIdMap = dataIdMap;
		}

		Integer id = dataIdMap.get(name);
		if(id == null){
			dataIdMap.put(name, 0);
			return name;
		}else{
			id = id + 1;
			dataIdMap.put(name, id);
			return name + id;
		}
	}

	private Condition sadd(String attrName, byte operator, Object value){
		String dataName = createDataName(attrName);
		Thing con = DataObjectUtil.createConditionThing(attrName, dataName, operator, null);
		con.put("name", dataName);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		condition.setConditionValue(dataName, value);
		childs.add(condition);

		return condition;
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
	 * 设置查询条件的参数值。如果没有本条件的值，那么本条件的值将设置为null。
	 */
	public void setConditionValues(Map<String, Object> values){
		String dataName = this.getDataName();
		if(dataName != null && !dataName.isEmpty()) {
			this.conditionValue = values.get(dataName);
		}else{
			this.conditionValue = null;
		}

		for(Condition condition : childs){
			condition.setConditionValues(values);
		}
	}

	/**
	 * 把条件的值，包括子节点的值保存到一个Map中并返回。
	 */
	public Map<String, Object> getConditionValues() {
		Map<String, Object> values = new HashMap<>();
		initConditionValuesMap(values);
		return values;
	}

	private void initConditionValuesMap(Map<String, Object> values){
		if(isSelfActive()){
			if(conditionValue instanceof Object[]) {
				if(((Object[]) conditionValue).length > 0){
					String dataName = getDataName();
					values.put(dataName, conditionValue);
				}
			}else if(conditionValue instanceof List<?>){
				if(((List<?>) conditionValue).size() > 0){
					String dataName = getDataName();
					values.put(dataName, conditionValue);
				}
			}else if(conditionValue != null){
				String dataName = getDataName();
				values.put(dataName, conditionValue);
			}
		}

		for(Condition child : childs){
			child.initConditionValuesMap(values);
		}
	}

	public List<ConditionValue> getConditionValueList(){
		return getConditionValueList((Thing) null);
	}

	public List<ConditionValue> getConditionValueList(Thing dataObject){
		List<ConditionValue> values = new ArrayList<>();
		initConditionValueList(values, dataObject == null ? Collections.emptyList() : dataObject.getChilds("attribute"));

		return values;
	}

	public List<ConditionValue> getConditionValueList(List<Thing> attributes){
		List<ConditionValue> values = new ArrayList<>();
		initConditionValueList(values, attributes == null ? Collections.emptyList() : attributes);

		return values;
	}

	public List<ConditionValue> getSelfConditionValueList(List<Thing> attributes){
		List<ConditionValue> values = new ArrayList<>();
		Thing attribute = getAttribute(attributes);

		if(isSelfActive()){
			if(conditionValue != null){
				if(conditionValue instanceof Object[]){
					Object[] objects = (Object[]) conditionValue;
					for(Object value : objects){
						values.add(new ConditionValue(this, checkValue(value), attribute));
					}
				}else if(conditionValue instanceof List<?>){
					for(Object value : (List<?>) conditionValue){
						values.add(new ConditionValue(this, checkValue(value), attribute));
					}
				}else{
					values.add(new ConditionValue(this, checkValue(conditionValue), attribute));
				}
			}//else {
				//values.add(new ConditionValue(this, null, attribute));
			//}
		}

		return values;
	}

	private void initConditionValueList(List<ConditionValue> values, List<Thing> attributes){
		values.addAll(getSelfConditionValueList(attributes));

		for(Condition child : childs){
			child.initConditionValueList(values, attributes);
		}
	}

	/**
	 * 从属性定义列表中获取对用的定义。
	 *
	 * @param attributes 属性定义列表
	 * @return 属性定义，不存在返回null
	 */
	public Thing getAttribute(List<Thing> attributes){
		if(attributes == null){
			return null;
		}

		String attributeName = this.getAttributeName();
		for(Thing attribute : attributes){
			if(attribute.getMetadata().getName().equals(attributeName)){
				return attribute;
			}
		}

		return null;
	}

	private Object checkValue(Object object){
		if(object  == null){
			return null;
		}

		if(!(object instanceof  String)){
			return object;
		}

		String value = (String) object;
		switch (getOperator()){
			case Condition.llike:
				if(!value.startsWith("%")){
					value = "%" + value;
				}
				break;
			case Condition.rlike:
				if(!value.endsWith("%")){
					value = value + "%";
				}
				break;
			case Condition.lrlike:
				if(!value.startsWith("%")){
					value = "%" + value;
				}
				if(!value.endsWith("%")){
					value = value + "%";
				}
				break;
		}

		return value;
	}

	public boolean isIgnoreNull(){
		return conditionThing.getBoolean("ignoreNull");
	}

	public void ignoreNul(boolean ignoreNull){
		conditionThing.put("ignoreNull", ignoreNull);
	}

	/**
	 * 返回属性名称，一般是属性的实体的字段名。
	 */
	public String getAttributeName(){
		String attributeName = conditionThing.getString("attributeName");
		if(attributeName != null && !attributeName.isEmpty()){
			return attributeName;
		}else{
			return conditionThing.getMetadata().getName();
		}
	}

	/**
	 * 返回数据名，数据名是条件参数Map中保存查询条件的值的key。
	 */
	public String getDataName(){
		return conditionThing.getStringBlankAsNull("dataName");
		/*
		String dataName = conditionThing.getString("dataName");
		if(dataName == null || dataName.isEmpty()){
			return getAttributeName();
		}else{
			return dataName;
		}*/
	}

	/**
	 * 返回条件的值。
	 */
	public Object getConditionValue(){
		return conditionValue;
	}

	/**
	 * 返回操作符。
	 */
	public byte getOperator(){
		return conditionThing.getByte("operator");
	}

	/**
	 * 返回条件的连接方式，AND或OR。
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
			return true;
		}else {
			return isSelfActive();
		}
	}

	public boolean isSelfActive(){
		Object conditionValue = getConditionValue();
		String attributeName = getAttributeName();
		if (attributeName == null || attributeName.isEmpty()) {
			return false;
		}

		byte opeartor = this.getOperator();
		if(opeartor == Condition.isnull || opeartor == Condition.notnull){
			//总是为真
			return true;
		}

		if(conditionThing.getBoolean("isClause") && conditionThing.getBoolean("alwaysAddClause")){
			return true;
		}

		if("".equals(conditionValue) && ingoreBlankString){
			return false;
		}

		if(conditionValue == null){
			return false;
		}

		if(conditionValue instanceof  Object[]){
			return ((Object[]) conditionValue).length != 0;
		}else if(conditionValue instanceof List<?>){
			return ((List<?>) conditionValue).size() != 0;
		}

		return true;
	}

	/**
	 * 返回子节点列表。
	 */
	public List<Condition> getChilds(){
		return childs;
	}

	/**
	 * 从模型和已有的查询条件中分析生成查询条件对象。
	 *
	 */
	public static Condition parse(Thing conditionThing, Map<String, Object> values, ActionContext actionContext) {
		return parse(null, conditionThing, values, actionContext);
	}

	private static Condition parse(Condition parent, Thing conditionThing, Map<String, Object> values, ActionContext actionContext){
		if(conditionThing == null){
			return new Condition();
		}

		Condition condition = new Condition(conditionThing);
		if(parent != null){
			condition.parent = parent;
			parent.childs.add(condition);
		}

		try {
			Object value = ConditionCreator.getConditionValue(conditionThing, condition.getDataName(), values, actionContext);
			if(value instanceof  String && conditionThing.getBoolean("multiple")){
				condition.setConditionValue(((String) value).split("[,]"));
			}else {
				condition.setConditionValue(value);
			}

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

	/**
	 * 返回sql的字段名。默认取sqlColumn，不存在取attributeName，最后取节点的名字。
	 */
	public String getColumnName(){
		String column = conditionThing.getStringBlankAsNull("sqlColumn");
		if(column == null){
			column = conditionThing.getStringBlankAsNull("attributeName");
		}
		if(column == null){
			column = conditionThing.getMetadata().getName();
		}

		return column;
	}

	/**
	 * 设置指定属性名的所有条件节点的sqlColumn属性的值。
	 */
	public void setColumnName(String attributeName, String columnName){
		if(attributeName.equals(this.getAttributeName())){
			conditionThing.put("sqlColumn", columnName);
		}

		for(Condition condition : childs){
			condition.setColumnName(attributeName, columnName);
		}
	}

	public String toSql(){
		return toSql(false);
	}

	/**
	 * 返回条件SQl语句。
	 */
	public String toSql(boolean hasValue){
		StringBuilder sb = new StringBuilder();

		if(childs.size() > 0){
			//如果有子节点，那么先生成子节点的SQL
			int childCount = 0; //childCount用来控制是否加()
			for(Condition child : childs){
				String sql = child.toSql(hasValue);
				if(sql != null && !sql.isEmpty()){
					childCount++;
					if(sb.length() > 0){
						sb.append(" ");
						sb.append(child.getJoin());
						sb.append(" ");
						sb.append(sql);
					}else{
						sb.append(sql);
					}
				}
			}

			if((sb.length() > 0 || conditionThing.getBoolean("alwaysAddClause")) && conditionThing.getBoolean("isClause")){
				String clause = conditionThing.getStringBlankAsNull("clauseSQL");
				if(clause != null){
					if(conditionThing.getBoolean("isClauseTemplate")){
						String sql = clause.replace("%%SQL%%", sb.toString());
						sb = new StringBuilder();
						addClause(sb, getColumnName(), getOperator(), sql);
					}else{
						StringBuilder sb1 = new StringBuilder(clause);
						if(!clause.toLowerCase().contains("where")){
							sb1.append(" where ");
						}
						sb1.append(sb);
						sb = new StringBuilder();
						addClause(sb, getColumnName(), getOperator(), sb1.toString());
					}
					childCount = 1;
				}else{
					sb.setLength(0);
				}
			}

			if(parent != null && sb.length() > 0 && childCount > 1){
				sb.insert(0, "(");
				sb.append(")");
			}
		} else {
			if(conditionThing.getBoolean("dummySql")){
				//只加条件不加sql
				return null;
			}

			String selfDefineSql = conditionThing.getStringBlankAsNull("selfDefineSql");
			if(isSelfActive()){
				if(selfDefineSql != null){
					return selfDefineSql;
				}

				if(conditionThing.getBoolean("isClause")){
					String clause = conditionThing.getStringBlankAsNull("clauseSQL");
					if(clause != null){
						addClause(sb, getColumnName(), getOperator(), clause);
					}
				}else {
					addSql(sb, getColumnName(), getOperator(), conditionThing.getString("multiValueJoin"), hasValue);
				}
			}else {
				if(conditionThing.getBoolean("alwaysAddClause") && selfDefineSql != null){
					return selfDefineSql;
				}
			}
		}

		if(sb.length() > 0){
			return sb.toString();
		}else{
			return null;
		}
	}

	private void addOperator(StringBuilder sb, byte operator){
		switch (operator){
			case Condition.eq:
				sb.append(" = ");
				break;
			case Condition.between:
				sb.append(" between ");
				break;
			case Condition.notbetween:
				sb.append(" not between ");
				break;
			case Condition.gt:
				sb.append(" > ");
				break;
			case Condition.in:
				sb.append(" in ");
				break;
			case Condition.gteq:
				sb.append(" >= ");
				break;
			case Condition.isnull:
				sb.append(" is null");
				break;
			case Condition.like:
			case Condition.llike:
			case Condition.rlike:
			case Condition.lrlike:
				sb.append(" like ");
				break;
			case Condition.lt:
				sb.append(" < ");
				break;
			case Condition.lteq:
				sb.append(" <= ");
				break;
			case Condition.notin:
				sb.append(" not in ");
				break;
			case Condition.notnull:
				sb.append(" is not null");
				break;
			case Condition.uneq:
				sb.append(" <> ");
				break;
			default:
				Executor.warn(TAG, "Not supported operator (" + operator + ")");
		}
	}

	private void addClause(StringBuilder sb, String column, byte operator, String clause){
		sb.append(column);
		addOperator(sb, operator);
		if(!clause.startsWith("(")){
			sb.append("(");
		}
		sb.append(clause);
		if(!clause.startsWith("(")){
			sb.append(")");
		}
	}

	private void addSql(StringBuilder sb, String column, byte operator, String multiValueJoin, boolean hasValue) {
		List<ConditionValue> values = getSelfConditionValueList(null);
		if (operator == Condition.between) {
			sb.append(column);
			addOperator(sb, operator);
			if(hasValue) {
				for(int i=0; i<values.size(); i++){
					if(i > 0){
						sb.append(" and ");
					}
					append(sb, values.get(i));
				}
			}else{
				sb.append("? and ?");
			}
		} else if (operator == Condition.in || operator == Condition.notin) {
			sb.append(column);
			addOperator(sb, operator);
			sb.append("(");
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				if(hasValue){
					append(sb, values.get(i));
				}else {
					sb.append("?");
				}
			}

			sb.append(")");
		} else if (!(operator == Condition.isnull || operator == Condition.notnull)) {
			if (multiValueJoin == null || multiValueJoin.isEmpty()) {
				multiValueJoin = "and";
			}

			if(values.size() > 1){
				sb.append("(");
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						sb.append(" ");
						sb.append(multiValueJoin);
						sb.append(" ");
					}
					sb.append(column);
					addOperator(sb, operator);
					if(hasValue){
						append(sb, values.get(i));
					}else {
						sb.append("?");
					}
				}
				sb.append(")");
			}else{
				sb.append(column);
				addOperator(sb, operator);
				if(hasValue){
					append(sb, values.get(0));
				}else {
					sb.append("?");
				}
			}
		}
	}

	private static void append(StringBuilder sb, ConditionValue value){
		if("string".equals(value.getType())){
			sb.append("'");
		}
		sb.append(value.getValue());
		if("string".equals(value.getType())){
			sb.append("'");
		}
	}
	/**
	 * 返回指定的数据对象是否符合条件。
	 *
	 * @param dataObject 数据对象
	 *
	 * @return 是否符合
	 */
	public boolean matches(DataObject dataObject){
		if(dataObject == null){
			return false;
		}

		byte operator = this.getOperator();
		String name = this.getAttributeName();
		if(conditionValue != null || operator == Condition.isnull || operator == Condition.notnull){
			if(!UtilCondition.check(dataObject, operator, name, this.conditionValue, conditionThing.getString("multiValueJoin"))){
				return false;
			}
		}

		boolean ok = true;
		for(int i=0; i<childs.size(); i++){
			Condition child = childs.get(i);
			ok = child.matches(dataObject);
			if(i < childs.size() - 1){
				if(AND.equals(childs.get(i + 1).getJoin())){
					if(!ok){
						return false;
					}
				}else{
					if(ok){
						return true;
					}
				}
			}
		}

		return ok;
	}
}
