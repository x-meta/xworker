package xworker.util.codeassist.objectassists;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.swt.xworker.CodeAssitContent;
import xworker.swt.xworker.codeassist.TextAssistor;

public class SqlAssistor implements TextAssistor{
	List<CodeAssitContent> keyWords = new ArrayList<CodeAssitContent>();
	Map<String, SqlAssistorCache> caches = new HashMap<String, SqlAssistorCache>();
	
	public SqlAssistor() {
        keyWords.add(new CodeAssitContent("add", "add", CodeAssitContent.IMAGE_NORMAL).setDocument("在现有表中添加一列"));
        keyWords.add(new CodeAssitContent("add constraint", "add constraint", CodeAssitContent.IMAGE_NORMAL).setDocument("在创建表后添加约束"));
        keyWords.add(new CodeAssitContent("alter", "alter", CodeAssitContent.IMAGE_NORMAL).setDocument("添加，删除或修改表中的列，或更改表中列的数据类型"));
        keyWords.add(new CodeAssitContent("alter column", "alter column", CodeAssitContent.IMAGE_NORMAL).setDocument("更改表中列的数据类型"));
        keyWords.add(new CodeAssitContent("alter table", "alter table", CodeAssitContent.IMAGE_NORMAL).setDocument("添加，删除或修改表中的列"));
        keyWords.add(new CodeAssitContent("all", "all", CodeAssitContent.IMAGE_NORMAL).setDocument("如果所有子查询值都满足条件，则返回true"));
        keyWords.add(new CodeAssitContent("and", "and", CodeAssitContent.IMAGE_NORMAL).setDocument("仅包含两个条件都为真的行"));
        keyWords.add(new CodeAssitContent("any", "any", CodeAssitContent.IMAGE_NORMAL).setDocument("如果任何子查询值满足条件，则返回true"));
        keyWords.add(new CodeAssitContent("as", "as", CodeAssitContent.IMAGE_NORMAL).setDocument("用别名重命名列或表"));
        keyWords.add(new CodeAssitContent("asc", "asc", CodeAssitContent.IMAGE_NORMAL).setDocument("将结果集按升序排序"));
        keyWords.add(new CodeAssitContent("backup database", "backup database", CodeAssitContent.IMAGE_NORMAL).setDocument("创建现有数据库的备份"));
        keyWords.add(new CodeAssitContent("between", "between", CodeAssitContent.IMAGE_NORMAL).setDocument("选择给定范围内的值"));
        keyWords.add(new CodeAssitContent("case", "case", CodeAssitContent.IMAGE_NORMAL).setDocument("根据条件创建不同的输出"));
        keyWords.add(new CodeAssitContent("check", "check", CodeAssitContent.IMAGE_NORMAL).setDocument("限制可以放置在列中的值的约束"));
        keyWords.add(new CodeAssitContent("column", "column", CodeAssitContent.IMAGE_NORMAL).setDocument("更改列的数据类型或删除表中的列"));
        keyWords.add(new CodeAssitContent("constraint", "constraint", CodeAssitContent.IMAGE_NORMAL).setDocument("添加或删除约束"));
        keyWords.add(new CodeAssitContent("create", "create", CodeAssitContent.IMAGE_NORMAL).setDocument("创建数据库，索引，视图，表或过程"));
        keyWords.add(new CodeAssitContent("create database", "create database", CodeAssitContent.IMAGE_NORMAL).setDocument("创建一个新的SQL数据库"));
        keyWords.add(new CodeAssitContent("create index", "create index", CodeAssitContent.IMAGE_NORMAL).setDocument("在表上创建索引（允许重复值）"));
        keyWords.add(new CodeAssitContent("create or replace view", "create or replace view", CodeAssitContent.IMAGE_NORMAL).setDocument("更新视图"));
        keyWords.add(new CodeAssitContent("create table", "create table", CodeAssitContent.IMAGE_NORMAL).setDocument("在数据库中创建一个新表"));
        keyWords.add(new CodeAssitContent("create procedure", "create procedure", CodeAssitContent.IMAGE_NORMAL).setDocument("创建一个存储过程"));
        keyWords.add(new CodeAssitContent("create unique index", "create unique index", CodeAssitContent.IMAGE_NORMAL).setDocument("在表上创建唯一索引（无重复值）"));
        keyWords.add(new CodeAssitContent("create view", "create view", CodeAssitContent.IMAGE_NORMAL).setDocument("根据SELECT语句的结果集创建视图"));
        keyWords.add(new CodeAssitContent("database", "database", CodeAssitContent.IMAGE_NORMAL).setDocument("创建或删除SQL数据库"));
        keyWords.add(new CodeAssitContent("default", "default", CodeAssitContent.IMAGE_NORMAL).setDocument("为列提供默认值的约束"));
        keyWords.add(new CodeAssitContent("delete", "delete", CodeAssitContent.IMAGE_NORMAL).setDocument("从表中删除行"));
        keyWords.add(new CodeAssitContent("desc", "desc", CodeAssitContent.IMAGE_NORMAL).setDocument("对结果集按降序排序"));
        keyWords.add(new CodeAssitContent("distinct", "distinct", CodeAssitContent.IMAGE_NORMAL).setDocument("仅选择不同的（不同的）值"));
        keyWords.add(new CodeAssitContent("drop", "drop", CodeAssitContent.IMAGE_NORMAL).setDocument("删除列，约束，数据库，索引，表或视图"));
        keyWords.add(new CodeAssitContent("drop column", "drop column", CodeAssitContent.IMAGE_NORMAL).setDocument("删除表中的列"));
        keyWords.add(new CodeAssitContent("drop constraint", "drop constraint", CodeAssitContent.IMAGE_NORMAL).setDocument("删除UNIQUE，PRIMARY KEY，FOREIGN KEY或CHECK约束"));
        keyWords.add(new CodeAssitContent("drop database", "drop database", CodeAssitContent.IMAGE_NORMAL).setDocument("删除现有的SQL数据库"));
        keyWords.add(new CodeAssitContent("drop default", "drop default", CodeAssitContent.IMAGE_NORMAL).setDocument("删除默认约束"));
        keyWords.add(new CodeAssitContent("drop index", "drop index", CodeAssitContent.IMAGE_NORMAL).setDocument("删除表中的索引"));
        keyWords.add(new CodeAssitContent("drop table", "drop table", CodeAssitContent.IMAGE_NORMAL).setDocument("删除数据库中的现有表"));
        keyWords.add(new CodeAssitContent("drop view", "drop view", CodeAssitContent.IMAGE_NORMAL).setDocument("删除视图"));
        keyWords.add(new CodeAssitContent("exec", "exec", CodeAssitContent.IMAGE_NORMAL).setDocument("执行存储过程"));
        keyWords.add(new CodeAssitContent("exists", "exists", CodeAssitContent.IMAGE_NORMAL).setDocument("测试子查询中是否存在任何记录"));
        keyWords.add(new CodeAssitContent("foreign key", "foreign key", CodeAssitContent.IMAGE_NORMAL).setDocument("约束是用于将两个表链接在一起的键"));
        keyWords.add(new CodeAssitContent("from", "from", CodeAssitContent.IMAGE_NORMAL).setDocument("指定要从中选择或删除数据的表"));
        keyWords.add(new CodeAssitContent("full outer join", "full outer join", CodeAssitContent.IMAGE_NORMAL).setDocument("当左表或右表中存在匹配项时，返回所有行"));
        keyWords.add(new CodeAssitContent("group by", "group by", CodeAssitContent.IMAGE_NORMAL).setDocument("对结果集进行分组（与汇总函数一起使用：COUNT，MAX，MIN，SUM，AVG）"));
        keyWords.add(new CodeAssitContent("having", "having", CodeAssitContent.IMAGE_NORMAL).setDocument("使用汇总功能代替WHERE"));
        keyWords.add(new CodeAssitContent("in", "in", CodeAssitContent.IMAGE_NORMAL).setDocument("允许您在WHERE子句中指定多个值"));
        keyWords.add(new CodeAssitContent("index", "index", CodeAssitContent.IMAGE_NORMAL).setDocument("创建或删除表中的索引"));
        keyWords.add(new CodeAssitContent("inner join", "inner join", CodeAssitContent.IMAGE_NORMAL).setDocument("返回两个表中具有匹配值的行"));
        keyWords.add(new CodeAssitContent("insert into", "insert into", CodeAssitContent.IMAGE_NORMAL).setDocument("在表格中插入新行"));
        keyWords.add(new CodeAssitContent("insert into select", "insert into select", CodeAssitContent.IMAGE_NORMAL).setDocument("将数据从一个表复制到另一个表"));
        keyWords.add(new CodeAssitContent("is null", "is null", CodeAssitContent.IMAGE_NORMAL).setDocument("测试空值"));
        keyWords.add(new CodeAssitContent("is not null", "is not null", CodeAssitContent.IMAGE_NORMAL).setDocument("测试非空值"));
        keyWords.add(new CodeAssitContent("join", "join", CodeAssitContent.IMAGE_NORMAL).setDocument("联接表"));
        keyWords.add(new CodeAssitContent("left join", "left join", CodeAssitContent.IMAGE_NORMAL).setDocument("返回左表中的所有行，以及右表中的匹配行"));
        keyWords.add(new CodeAssitContent("like", "like", CodeAssitContent.IMAGE_NORMAL).setDocument("在列中搜索指定的模式"));
        keyWords.add(new CodeAssitContent("limit", "limit", CodeAssitContent.IMAGE_NORMAL).setDocument("指定要在结果集中返回的记录数"));
        keyWords.add(new CodeAssitContent("not", "not", CodeAssitContent.IMAGE_NORMAL).setDocument("仅包含条件不成立的行"));
        keyWords.add(new CodeAssitContent("not null", "not null", CodeAssitContent.IMAGE_NORMAL).setDocument("强制列不接受NULL值的约束"));
        keyWords.add(new CodeAssitContent("or", "or", CodeAssitContent.IMAGE_NORMAL).setDocument("包括其中任一条件为真的行"));
        keyWords.add(new CodeAssitContent("order by", "order by", CodeAssitContent.IMAGE_NORMAL).setDocument("对结果集按升序或降序排序"));
        keyWords.add(new CodeAssitContent("outer join", "outer join", CodeAssitContent.IMAGE_NORMAL).setDocument("当左表或右表中存在匹配项时，返回所有行"));
        keyWords.add(new CodeAssitContent("primary key", "primary key", CodeAssitContent.IMAGE_NORMAL).setDocument("唯一标识数据库表中每个记录的约束"));
        keyWords.add(new CodeAssitContent("procedure", "procedure", CodeAssitContent.IMAGE_NORMAL).setDocument("存储过程"));
        keyWords.add(new CodeAssitContent("right join", "right join", CodeAssitContent.IMAGE_NORMAL).setDocument("返回右表中的所有行，以及左表中的匹配行"));
        keyWords.add(new CodeAssitContent("rownum", "rownum", CodeAssitContent.IMAGE_NORMAL).setDocument("指定要在结果集中返回的记录数"));
        keyWords.add(new CodeAssitContent("select", "select", CodeAssitContent.IMAGE_NORMAL).setDocument("从数据库中选择数据"));
        keyWords.add(new CodeAssitContent("select distinct", "select distinct", CodeAssitContent.IMAGE_NORMAL).setDocument("仅选择不同的（不同的）值"));
        keyWords.add(new CodeAssitContent("select into", "select into", CodeAssitContent.IMAGE_NORMAL).setDocument("将数据从一个表复制到新表中"));
        keyWords.add(new CodeAssitContent("select top", "select top", CodeAssitContent.IMAGE_NORMAL).setDocument("指定要在结果集中返回的记录数"));
        keyWords.add(new CodeAssitContent("set", "set", CodeAssitContent.IMAGE_NORMAL).setDocument("指定表中应更新的列和值"));
        keyWords.add(new CodeAssitContent("table", "table", CodeAssitContent.IMAGE_NORMAL).setDocument("创建表，或添加，删除或修改表中的列，或删除表或表中的数据"));
        keyWords.add(new CodeAssitContent("top", "top", CodeAssitContent.IMAGE_NORMAL).setDocument("指定要在结果集中返回的记录数"));
        keyWords.add(new CodeAssitContent("truncate table", "truncate table", CodeAssitContent.IMAGE_NORMAL).setDocument("删除表中的数据，但不删除表本身"));
        keyWords.add(new CodeAssitContent("union", "union", CodeAssitContent.IMAGE_NORMAL).setDocument("合并两个或多个SELECT语句的结果集（仅不同的值）"));
        keyWords.add(new CodeAssitContent("union all", "union all", CodeAssitContent.IMAGE_NORMAL).setDocument("合并两个或多个SELECT语句的结果集（允许重复值）"));
        keyWords.add(new CodeAssitContent("unique", "unique", CodeAssitContent.IMAGE_NORMAL).setDocument("确保列中所有值唯一的约束"));
        keyWords.add(new CodeAssitContent("update", "update", CodeAssitContent.IMAGE_NORMAL).setDocument("更新表中的现有行"));
        keyWords.add(new CodeAssitContent("values", "values", CodeAssitContent.IMAGE_NORMAL).setDocument("指定INSERT INTO语句的值"));
        keyWords.add(new CodeAssitContent("view", "view", CodeAssitContent.IMAGE_NORMAL).setDocument("创建，更新或删除视图"));
        keyWords.add(new CodeAssitContent("where", "where", CodeAssitContent.IMAGE_NORMAL).setDocument("筛选结果集以仅包含满足指定条件的记录"));
	}
	@Override
	public List<CodeAssitContent> getContents(String codeType, String code, int cursorIndex, Thing thing, ActionContext actionContext){
		if(codeType == null || "".equals(codeType)) {
			return Collections.emptyList();
		}
		if(!"sql".equals(codeType.toLowerCase())) {
			return Collections.emptyList();
		}
		
		List<CodeAssitContent> vars = new ArrayList<CodeAssitContent>();
		vars.addAll(keyWords);
		
		//获取Connection对象，通过Connection解析内容
		Connection con = null;
		boolean closeCon = true;
		try {
			Thing dataSource = null; //通过数据源获取
			if(thing != null) {
				dataSource = thing.doAction("getDataSource", actionContext);
				if(dataSource != null) {
					con = dataSource.doAction("getConnection", actionContext);
				}
			}
			
			
			if(con != null && con.isClosed() == false) {
				DatabaseMetaData metaData = con.getMetaData();
				String url = metaData.getURL();
				SqlAssistorCache cache = caches.get(url);
				if(cache == null) {
					cache = new SqlAssistorCache();
					cache.init(con);
					caches.put(url, cache);
				}else {
					cache.init(con);
				}
				
				vars.addAll(cache.getVariables());
			}
			
		}catch(Exception e){
			Executor.info(SqlAssistor.class.getName(), "Get variables error", e);
		}finally {
			if(closeCon && con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return vars;
	}
}
