package xworker.ide.assistor.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.dataObject.utils.DbUtil;
import xworker.ide.assistor.utils.AssistorQueueTask;
import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;

/**
 * 每次用户操作后会通知辅助者，辅助者则搜集相关辅助信息，由于用户操作可能很快且统计且搜集辅助信息是辅助性的，
 * 可以有延迟，因此需要有过滤功能，总是搜集用户最后的搜索信息。
 * 
 * @author Administrator
 *
 */
public class GetAssistThingStaticTask implements AssistorQueueTask{
	private static final String TAG = GetAssistThingStaticTask.class.getName();
	
	/** 获取事物本身、事物的所有描述者以及继承者所注册的事物，但不包含元事物的注册 */
	public static final byte ALL = 0;
	/** 只是获取当前事物的注册事物 */
	public static final byte THING_ONLY = 1;
	
	public static final byte DESCRIPTOR_ONLY = 2;
	public static final byte EXTENDS_ONLY = 3;
	
	/** 需要回调的动作集合 */
	ActionContainer actions;
	/** 回调者的变量上下文 */
	ActionContext actionContext;
	/** 需要查找的统计 */
	Thing thing;
	/** 查找类型 */
	byte type;
	List<StaticInfo> infos = null;
	Map<String, Object> datas = null;
	boolean executed = false;
	
	public GetAssistThingStaticTask(Thing thing, ActionContainer actions, ActionContext actionContext, Map<String, Object> datas ) {
		this(thing, actions, actionContext, datas, ALL);
	}
	
	public GetAssistThingStaticTask(Thing thing, ActionContainer actions, ActionContext actionContext, Map<String, Object> datas ,byte type) {
		this.thing = thing; 
		this.actions = actions;
		this.actionContext = actionContext;
		this.type = type; 
		this.datas = datas;
	}

	@Override
	public void doTask() {
		if(executed){
			return;
		}
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			if(thing == null){
				return;
			}
			
			String sql = "select count(distinct thingPath) as cnt, registType from tblThingRegists where (registThingPath=?";
			List<Thing> registThings = new ArrayList<Thing>();
			registThings.add(thing);
			
			if(type == ALL || type == DESCRIPTOR_ONLY){
				List<Thing> descriptors = thing.getAllDescriptors();
				for(int i=0; i<descriptors.size() - 1; i++){
					sql = sql + " or registThingPath=? ";
					registThings.add(descriptors.get(i));
				}
			}
			if(type == ALL || type == EXTENDS_ONLY){
				List<Thing> extendss = thing.getAllExtends();
				for(int i=0; i<extendss.size(); i++){
					sql = sql + " or registThingPath=? ";
					registThings.add(extendss.get(i));
				}
			}
			
			sql = sql  + ") group by registType";
			//XWorker的IDE数据库链接
			Thing dataSourceThing = World.getInstance().getThing("xworker.ide.db.datasource.XWorkerDataSource");
			con = (Connection) dataSourceThing.doAction("getConnection");
			pst = con.prepareStatement(sql);
			for(int i=0; i<registThings.size(); i++){				
				pst.setString(i + 1, registThings.get(i).getMetadata().getPath());
			}
			rs = pst.executeQuery();
			infos = new ArrayList<StaticInfo>();
			while(rs.next()){
				infos.add(new StaticInfo(rs.getString("registType"), rs.getInt("cnt")));
			}
			
		}catch(Exception e){
			Executor.info(TAG, "get thing regist static error", e);
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}

	@Override
	public void doNotify() { 
		actions.doAction("staticInfoChanged", actionContext, UtilMap.toMap(new Object[]{"infos", infos, "datas", datas}));
	}

	@Override
	public void setExecuted() {
		executed = true;
	}
}
