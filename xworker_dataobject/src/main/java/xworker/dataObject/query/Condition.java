package xworker.dataObject.query;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.executor.Executor;

import java.util.*;

public class Condition {
	private static final String TAG = Condition.class.getName();

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
	int dataId = 0;
	
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
	 */
	public Condition eq(String name, Object value){
		this.sadd(name, Condition.eq, value);

		return this;
	}

	/**
	 * 添加一个uneq条件，并返回自己。
	 */
	public Condition uneq(String name, Object value){
		this.sadd(name, Condition.uneq, value);

		return this;
	}

	/**
	 * 添加一个gt条件，并返回自己。
	 */
	public  Condition gt(String name, Object value){
		this.sadd(name, Condition.gt, value);

		return this;
	}

	public Condition gteq(String name, Object value){
		this.sadd(name, Condition.gteq, value);

		return this;
	}

	public Condition lt(String name, Object value){
		this.sadd(name, Condition.lt, value);

		return this;
	}

	public Condition lteq(String name, Object value){
		this.sadd(name, Condition.lteq, value);

		return this;
	}

	public Condition like(String name, Object value){
		this.sadd(name, Condition.like, value);
		return this;
	}

	public Condition llike(String name, Object value){
		this.sadd(name, Condition.llike, value);

		return this;
	}

	public Condition rlike(String name, Object value){
		this.sadd(name, Condition.rlike, value);

		return this;
	}

	public Condition lrlike(String name, Object value){
		this.sadd(name, Condition.lrlike, value);

		return this;
	}

	public Condition in(String name, Object ... values){
		this.sadd(name, Condition.in, values);

		return this;
	}

	public Condition regex(String name, String pattern){
		this.sadd(name, Condition.regex, pattern);

		return this;
	}

	public Condition equals(String name, Object value){
		this.sadd(name, Condition.equals, value);
		return this;
	}

	public Condition isnull(String name){
		this.sadd(name, Condition.isnull, null);

		return this;
	}

	public Condition notnull(String name){
		this.sadd(name, Condition.notnull, null);

		return this;
	}

	public Condition between(String name, Object value1, Object value2){
		this.sadd(name, Condition.between, new Object[]{value1, value2});

		return  this;
	}

	public Condition notin(String name,  Object ... value){
		this.sadd(name, Condition.notin, value);

		return this;
	}

	/**
	 * 添加一个eq条件，并返回自己。
	 */
	public Condition oreq(String name, Object value){
		this.sadd(name, Condition.eq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个uneq条件，并返回自己。
	 */
	public Condition oruneq(String name, Object value){
		this.sadd(name, Condition.uneq, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加一个gt条件，并返回自己。
	 */
	public  Condition orgt(String name, Object value){
		this.sadd(name, Condition.gt, value).setJoin(OR);

		return this;
	}

	public Condition orgteq(String name, Object value){
		this.sadd(name, Condition.gteq, value).setJoin(OR);

		return this;
	}

	public Condition orlt(String name, Object value){
		this.sadd(name, Condition.lt, value).setJoin(OR);

		return this;
	}

	public Condition orlteq(String name, Object value){
		this.sadd(name, Condition.lteq, value).setJoin(OR);

		return this;
	}

	public Condition orlike(String name, Object value){
		this.sadd(name, Condition.like, value).setJoin(OR);
		return this;
	}

	public Condition orllike(String name, Object value){
		this.sadd(name, Condition.llike, value).setJoin(OR);

		return this;
	}

	public Condition orrlike(String name, Object value){
		this.sadd(name, Condition.rlike, value).setJoin(OR);

		return this;
	}

	public Condition orlrlike(String name, Object value){
		this.sadd(name, Condition.lrlike, value).setJoin(OR);

		return this;
	}

	public Condition orin(String name, Object ... values){
		this.sadd(name, Condition.in, values).setJoin(OR);

		return this;
	}

	public Condition orregex(String name, String pattern){
		this.sadd(name, Condition.regex, pattern).setJoin(OR);

		return this;
	}

	public Condition orequals(String name, Object value){
		this.sadd(name, Condition.equals, value).setJoin(OR);
		return this;
	}

	public Condition orisnull(String name, Object value){
		this.sadd(name, Condition.isnull, value).setJoin(OR);

		return this;
	}

	public Condition ornotnull(String name, Object value){
		this.sadd(name, Condition.notnull, value).setJoin(OR);

		return this;
	}

	public Condition orbetween(String name, Object value1, Object value2){
		this.sadd(name, Condition.between, new Object[]{value1, value2}).setJoin(OR);

		return  this;
	}

	public Condition ornotin(String name, Object ... value){
		this.sadd(name, Condition.notin, value).setJoin(OR);

		return this;
	}

	/**
	 * 添加并返回一个and子条件。
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
	 */
	public Condition or(){
		Thing con = DataObjectUtil.createConditionThing(null, null, eq, OR);
		conditionThing.addChild(con);

		Condition condition = new Condition(this, con);
		childs.add(condition);
		return condition;
	}

	public Condition sql(String name, String sql, Object ... value){
		Condition con = this.sadd(name, Condition.eq, value);
		con.setJoin(AND);
		con.set("selfDefineSql", sql);

		return this;
	}

	public Condition orsql(String name, String sql, Object ... value){
		Condition con = this.sadd(name, Condition.eq, value);
		con.setJoin(OR);
		con.set("selfDefineSql", sql);

		return this;
	}

	public Condition clause(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(AND);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);

		return con;
	}

	public Condition orclause(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(OR);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);

		return con;
	}

	public Condition clauseTemplate(String name, byte operator, String clauseSql, Object ... value){
		Condition con = this.sadd(name, operator, value);
		con.setJoin(AND);
		con.set("isClause", "true");
		con.set("clauseSQL", clauseSql);
		con.set("isClauseTemplate", "true");

		return con;
	}

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

	private String createDataName(){
		Condition con = this;
		while(con.parent != null){
			con = con.parent;
		}
		con.dataId++;
		return "data_" + con.dataId;
	}

	private Condition sadd(String attrName, byte operator, Object value){
		String dataName = createDataName();
		Thing con = DataObjectUtil.createConditionThing(attrName, dataName, operator, null);
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
	 * 把条件的值，包括子节点的值保存到一个Map中并返回。
	 */
	public Map<String, Object> getConditionValues() {
		Map<String, Object> values = new HashMap<>();
		initConditionValuesMap(values);
		return values;
	}

	private void initConditionValuesMap(Map<String, Object> values){
		if(conditionValue != null){
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
			}else{
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

	private void initConditionValueList(List<ConditionValue> values, List<Thing> attributes){
		Thing attribute = getAttribute(attributes);

		if(conditionValue != null || !isIgnoreNull()){
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
			}else {
				values.add(new ConditionValue(this, null, attribute));
			}
		}

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
		String dataName = conditionThing.getString("dataName");
		if(dataName == null || dataName.isEmpty()){
			return getAttributeName();
		}else{
			return dataName;
		}
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
			Object conditionValue = getConditionValue();
			String attributeName = getAttributeName();
			if (attributeName == null || attributeName.isEmpty()) {
				return false;
			}

			return conditionValue != null || !getConditionThing().getBoolean("ignoreNull");
		}
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

	public void setColumnName(String attributeName, String columnName){
		if(attributeName.equals(this.getAttributeName())){
			conditionThing.put("sqlColumn", columnName);
		}

		for(Condition condition : childs){
			condition.setColumnName(attributeName, columnName);
		}
	}

	/**
	 * 返回条件SQl语句。
	 */
	public String toSql(){
		StringBuilder sb = new StringBuilder();

		if(childs.size() > 0){
			//如果有子节点，那么先生成子节点的SQL
			int childCount = 0; //childCount用来控制是否加()
			for(Condition child : childs){
				String sql = child.toSql();
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
			if(conditionValue != null){
				String selfDefineSql = conditionThing.getStringBlankAsNull("selfDefineSql");
				if(selfDefineSql != null){
					return selfDefineSql;
				}

				if(conditionThing.getBoolean("isClause")){
					String clause = conditionThing.getStringBlankAsNull("clauseSQL");
					if(clause != null){
						addClause(sb, getColumnName(), getOperator(), clause);
					}
				}else {
					addSql(sb, conditionValue, getColumnName(), getOperator(), conditionThing.getString("multiValueJoin"));
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

	private void addSql(StringBuilder sb, Object value, String column, byte operator, String multiValueJoin) {
		if (operator == Condition.between) {
			sb.append(column);
			addOperator(sb, operator);
			sb.append("? and ?");
		} else if (operator == Condition.in || operator == Condition.notin) {
			sb.append(column);
			addOperator(sb, operator);
			sb.append("(");
			if (value instanceof Object[]) {
				Object[] values = (Object[]) value;
				for (int i = 0; i < values.length; i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append("?");
				}
			} else if (value instanceof List<?>) {
				List<?> values = (List<?>) value;
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append("?");
				}
			} else {
				sb.append("?");
			}
			sb.append(")");
		} else if (!(operator == Condition.isnull || operator == Condition.notnull)) {
			if (multiValueJoin == null || multiValueJoin.isEmpty()) {
				multiValueJoin = "and";
			}
			if (value instanceof Object[]) {
				sb.append("(");
				Object[] values = (Object[]) value;
				if(values.length == 0){
					return;
				}
				for (int i = 0; i < values.length; i++) {
					if (i > 0) {
						sb.append(" ");
						sb.append(multiValueJoin);
						sb.append(" ");
					}
					sb.append(column);
					addOperator(sb, operator);
					sb.append("?");
				}
				sb.append(")");
			} else if (value instanceof List<?>) {
				List<?> values = (List<?>) value;
				if(values.size() == 0){
					return;
				}
				sb.append("(");
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						sb.append(" ");
						sb.append(multiValueJoin);
						sb.append(" ");
					}
					sb.append(column);
					addOperator(sb, operator);
					sb.append("?");
				}
				sb.append(")");
			} else {
				sb.append(column);
				addOperator(sb, operator);
				sb.append("?");
			}
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
