package xworker.task.segment.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.db.DbUtil;
import xworker.task.segment.Range;
import xworker.task.segment.SegmentTask;

public class DbRangeManager extends AbstractRnageManager{
	
	Thing dataSource;
	int stepSize;
	Thing taskThing;
	
	public DbRangeManager(Thing thing, ActionContext actionContext) throws SQLException{
		super(thing, actionContext);
		
		this.dataSource = (Thing) thing.doAction("getDataSource", actionContext);
		this.taskThing = (Thing) thing.doAction("getTaskThing", actionContext);
		this.stepSize = (Integer) thing.doAction("getSize", actionContext);
		
		init();
	}
	
	/**
	 * 获取数据库连接。
	 * 
	 * @return
	 */
	public Connection getConnection(){
		return (Connection) dataSource.doAction("getConnection", actionContext);
	}
	
	/**
	 * 初始化未完成的段落。
	 * @throws SQLException 
	 * 
	 */
	public void init() throws SQLException{
		Connection con = this.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + thing.getMetadata().getPath());
		}
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			//先查询未完成的段落列表
			pst = con.prepareStatement("select id,taskPath,startIndex,endIndex,status,createDate,updateDate from tblTaskSegmentRanges where taskPath=? and status in (?, ?) order by startIndex");
			pst.setString(1, taskThing.getMetadata().getPath());
			pst.setByte(2, Range.WORKING);
			pst.setByte(3, Range.WAITTING);
			rs = pst.executeQuery();
			
			while(rs.next()){
				String id = rs.getString("id");
				long start = rs.getLong("startIndex");
				long end = rs.getLong("endIndex");
						
				Range range = new Range(start, end);
				addRange(range);
				range.setId(id);
				
				if(range.getStatus() == Range.WAITTING){
					//endRnag数据库中应该只有一个，如果有多个那么应该是异常
					this.endRange = range;
				}
			}
			
			if(getRangeSize() == 0){
				//还没有记录，创建一个				
				rs.close();
				pst.close();
				
				long start = getLong("getStart");
				long end = getLong("getEnd");
				//取long的最大值为结束，这样分段应该永远不会结束
				Range range = new Range(start, end);
				createRange(range);
			    this.endRange = range;
			    addRange(range);
			}
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
		
	}
	
	/**
	 * 返回数据库所有记录数。
	 * 
	 * @return
	 */
	public long getLong(String name){
		return (Long) thing.doAction(name, actionContext, UtilMap.toMap("rangeManager", this));
	}
		
	public void removeRange(Range range){
		Connection con = this.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + thing.getMetadata().getPath());
		}
		
		PreparedStatement pst = null;
		try{
			//先查询未完成的段落列表
			pst = con.prepareStatement("delete from tblTaskSegmentRanges where id=?");
			pst.setLong(1, Long.parseLong(range.id));
			pst.executeUpdate();
			
		}catch(Exception e){
			throw new ActionException("RemoveRnage error, thing=" + taskThing.getMetadata().getPath(), e);
		}finally{
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}
	
	public void updateRange(Range range){
		Connection con = this.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + thing.getMetadata().getPath());
		}
		
		PreparedStatement pst = null;
		try{
			//先查询未完成的段落列表
			pst = con.prepareStatement("update tblTaskSegmentRanges set startIndex=?,endIndex=?,status=?,updateDate=? where id=?");
			pst.setLong(1, range.getStart());
			pst.setLong(2, range.getEnd());
			pst.setByte(3, range.getStatus());
			pst.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.setLong(5, Long.parseLong(range.id));
			pst.executeUpdate();
			
		}catch(Exception e){
			throw new ActionException("UpdateRnage error, thing=" + taskThing.getMetadata().getPath(), e);
		}finally{
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}
	
	public void createRange(Range range){
		Connection con = this.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + thing.getMetadata().getPath());
		}
		
		PreparedStatement pst = null;
		try {
			boolean createSeqId = false;
			long id = -1;
			String dbType = dataSource.getString("dbType");
			if(dbType != null && dbType.toLowerCase().startsWith("oracle")){
				createSeqId = true; 
			}
			if(createSeqId){
				//获取键值
				pst = con.prepareStatement("select HIBERNATE_SEQUENCE.nextval from dual");
				ResultSet rs = pst.executeQuery();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into tblTaskSegmentRanges(taskPath,startIndex,endIndex,status,createDate,updateDate,id) values(?,?,?,?,?,?,?)");
			}else{
				pst = con.prepareStatement("insert into tblTaskSegmentRanges(taskPath,startIndex,endIndex,status,createDate,updateDate) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			}
			
		    pst.setString(1, taskThing.getMetadata().getPath());
		    pst.setLong(2, range.start);
		    pst.setLong(3, range.end);
		    pst.setInt(4, Range.WORKING);
		    
		    long d = System.currentTimeMillis();
		    pst.setTimestamp(5, new java.sql.Timestamp(d));
		    pst.setTimestamp(6, new java.sql.Timestamp(d));
		    if(createSeqId){
		    	pst.setLong(7, id);
		    }
		    pst.executeUpdate();
		    
		    if(createSeqId){
		    	range.setId(String.valueOf(id));
		    }else{
			    ResultSet keys = pst.getGeneratedKeys();
			    if(keys.next()){
			    	range.setId(keys.getString(1));
			    }
			    keys.close();
		    }
		   
		    
		}catch(Exception e){
			throw new ActionException("UpdateRnage error, thing=" + taskThing.getMetadata().getPath(), e);
		}finally{
			DbUtil.close(pst);
			DbUtil.close(con);
		}

	}

	public static Thing getTaskThing(ActionContext actionContext){
		SegmentTask task = actionContext.getObject("task");
		return task.getThing();
	}
	
	public static DbRangeManager run(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		return new DbRangeManager(self, actionContext);
	}
	
	public static Long getCount(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		DbRangeManager manager = (DbRangeManager) actionContext.get("rangeManager");
		
		Connection con = manager.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + manager.thing.getMetadata().getPath());
		}
		
		String sql = self.getString("countSql");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			if(rs.next()){
				return rs.getLong(1);
			}else{
				return 0L;
			}
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
	}

	@Override
	protected long getNewEnd() {
		return  getLong("getEnd");
	}

	@Override
	protected int getStepSize() {
		return stepSize;
	}

}
