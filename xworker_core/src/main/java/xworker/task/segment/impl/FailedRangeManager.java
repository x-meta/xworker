package xworker.task.segment.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.db.DbUtil;
import xworker.task.segment.Range;

public class FailedRangeManager extends DbRangeManager{
	String taskPath;
	
	public FailedRangeManager(Thing thing, ActionContext actionContext) throws SQLException{
		super(thing, actionContext);
		
		this.taskPath = (String) thing.doAction("getTaskPath", actionContext);
	}
	
	/**
	 * 初始化失败的段落。
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
			//先查询失败段落列表，即状态大于等于
			pst = con.prepareStatement("select id,taskPath,start,end,status,createDate,updateDate from tblTaskSegmentRanges where taskPath=? and status in (?,?,?) order by start");
			pst.setString(1, taskPath);
			pst.setByte(2, Range.FAILED);
			pst.setByte(3, Range.EXCEPTION);
			pst.setByte(4, Range.TIMEOUT);
			rs = pst.executeQuery();
			
			while(rs.next()){
				String id = rs.getString("id");
				long start = rs.getLong("start");
				long end = rs.getLong("end");
						
				Range range = new Range(start, end);
				this.addRange(range);
				range.setId(id);
			}			
		}finally{
			DbUtil.close(rs);
			DbUtil.close(pst);
			DbUtil.close(con);
		}
		
	}
	
	@Override
	public Range getNextRange() {
		if(getRangeSize() == 0){
			try {
				init();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		for(int i=0; i<getRangeSize(); i++){			
			Range range = this.getRange(i);
			
			//失败的是按照平均分割的
			long size = (range.getEnd() + 1 - range.getStart()) / stepSize;
			if(size < 1){
				size = 1;
			}
			Range r = range.getRange((int) size);
			if(range.isDropped()){
				removeRange(this.removeRange(0));
				i--;
			}else if(range.isChanged()){
				updateRange(range);
			}
			
			if(r != null){
				createRange(r);
			}
			return r;
		}
		
		return null;		
	}
	
	public void createRange(Range range){
		Connection con = this.getConnection();
		if(con == null){
			throw new ActionException("DataSource is null, thing=" + thing.getMetadata().getPath());
		}
		
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("insert into tblTaskSegmentRanges(taskPath,start,end,status,createDate,updateDate) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
		    pst.setString(1, taskPath);
		    pst.setLong(2, range.start);
		    pst.setLong(3, range.end);
		    pst.setInt(4, Range.WORKING);
		    
		    long d = System.currentTimeMillis();
		    pst.setTimestamp(5, new java.sql.Timestamp(d));
		    pst.setTimestamp(6, new java.sql.Timestamp(d));    
		    pst.executeUpdate();
		    
		    ResultSet keys = pst.getGeneratedKeys();
		    if(keys.next()){
		    	range.setId(keys.getString(1));
		    }
		    keys.close();
		    
		}catch(Exception e){
			throw new ActionException("UpdateRnage error, thing=" + taskThing.getMetadata().getPath(), e);
		}finally{
			DbUtil.close(pst);
			DbUtil.close(con);
		}

	}
		
	public static FailedRangeManager run(ActionContext actionContext) throws SQLException{
		Thing self = (Thing) actionContext.get("self");
		return new FailedRangeManager(self, actionContext);
	}

	@Override
	public void rangeFailed(Range range) {
		if(range.getEnd() - range.getStart() == 0){
			//如果分割到单位1了，通常之前已经失败过几次了，现在可以全部删除了
			range.setStatus(Range.FAILFAILE);
		}
		
		super.rangeFailed(range);
	}
}
