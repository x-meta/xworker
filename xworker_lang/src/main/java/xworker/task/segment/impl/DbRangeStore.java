package xworker.task.segment.impl;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.db.DbUtil;
import xworker.task.segment.Range;
import xworker.task.segment.RangeStore;
import xworker.task.segment.SegmentTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbRangeStore implements RangeStore {
    Thing dataSource;
    ActionContext actionContext;
    SegmentTask task;
    Long offset;
    Thing thing;

    public DbRangeStore(Thing thing, Thing dataSource, ActionContext actionContext){
        this.thing = thing;
        this.dataSource = dataSource;
        this.actionContext = actionContext;

        //检查数据库，如果没有相应的表那么创建
        checkDatabase();
    }

    private void checkDatabase(){
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        try{
            con = dataSource.doAction("getConnection", actionContext);
            if(con == null){
                throw new ActionException("Can not get connection, dataSource=" + dataSource.getMetadata().getPath());
            }
            pst = con.prepareStatement("select count(*) from tblSegmentTask");
            resultSet = pst.executeQuery();
        }catch(Exception e){
            //失败，表不存在，创建数据库表
            Thing database = World.getInstance().getThing("xworker.lang.task.impls.DbSegmentDatabase");
            database.doAction("createTables", actionContext, "dataSource", dataSource);
        }finally {
            DbUtil.close(con, pst, resultSet);
        }
    }

    /**
     * 获取数据库连接。
     *
     * @return
     */
    public Connection getConnection(){
        return (Connection) dataSource.doAction("getConnection", actionContext);
    }

    @Override
    public void saveRange(SegmentTask task, Range range) {
        Connection con = null;
        PreparedStatement pst = null;

        try{
            con = getConnection();
            pst = con.prepareStatement("insert into tblSegmentTaskRanges(rangeId, taskId, start, end, status, runCount)" +
                    " values(?,?,?,?,?,?)");
            String rangeId = task.getTaskId().hashCode() + "_" + range.start;
            range.setId(rangeId);

            pst.setString(1, range.getId());
            pst.setString(2, task.getTaskId());
            pst.setLong(3, range.getStart());
            pst.setLong(4, range.getEnd());
            pst.setByte(5, range.getStatus());
            pst.setInt(6, range.getRunCount());
            pst.execute();
        }catch(Exception e){
            throw new ActionException("Insert range error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst);
        }
    }

    @Override
    public void updateRange(SegmentTask task, Range range) {
        Connection con = null;
        PreparedStatement pst = null;

        try{
            con = getConnection();
            pst = con.prepareStatement("update tblSegmentTaskRanges set status=?, runCount=? where rangeId=?");
            pst.setByte(1, range.getStatus());
            pst.setInt(2, range.getRunCount());
            pst.setString(3, range.getId());
            pst.executeUpdate();
        }catch(Exception e){
            throw new ActionException("Update range error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst);
        }
    }

    @Override
    public void removeRange(SegmentTask task, Range range) {
        Connection con = null;
        PreparedStatement pst = null;

        try{
            con = getConnection();
            pst = con.prepareStatement("delete from tblSegmentTaskRanges where rangeId=? and taskId=?");
            pst.setString(1, range.getId());
            pst.setString(2, task.getTaskId());
            pst.execute();
        }catch(Exception e){
            throw new ActionException("Remove range error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst);
        }
    }

    @Override
    public List<Range> geAllRanges(SegmentTask task) {
        List<Range> ranges = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pst = con.prepareStatement("select * from tblSegmentTaskRanges where taskId=?");
            pst.setString(1, task.getTaskId());
            rs = pst.executeQuery();
            while(rs.next()){
                String rangeId = rs.getString("rangeId");
                long start = rs.getLong("start");
                long end = rs.getLong("end");
                byte status = rs.getByte("status");
                int runCount = rs.getByte("runCount");

                Range range = new Range(task, start, end);
                range.setId(rangeId);
                range.setStatus(status);
                range.setRunCount(runCount);

                ranges.add(range);
            }
        }catch(Exception e){
            throw new ActionException("Get all ranges error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst, rs);
        }
        return ranges;
    }

    @Override
    public List<Range> getUnfinishedRanges(SegmentTask task) {
        List<Range> ranges = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pst = con.prepareStatement("select * from tblSegmentTaskRanges where taskId=? and status <> 2");
            pst.setString(1, task.getTaskId());
            rs = pst.executeQuery();
            while(rs.next()){
                String rangeId = rs.getString("rangeId");
                long start = rs.getLong("start");
                long end = rs.getLong("end");
                byte status = rs.getByte("status");
                int runCount = rs.getByte("runCount");

                Range range = new Range(task, start, end);
                range.setStatus(status);
                range.setRunCount(runCount);

                ranges.add(range);
            }
        }catch(Exception e){
            throw new ActionException("Get all ranges error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst, rs);
        }
        return ranges;
    }

    @Override
    public Long getRangeOffset() {
        return offset;
    }

    @Override
    public void updateRangeOffset(long offset) {
        this.offset = offset;

        Connection con = null;
        PreparedStatement pst = null;

        try{
            con = getConnection();
            pst = con.prepareStatement("update tblSegmentTask set offset =? where taskId=?");
            pst.setLong(1, offset);
            pst.setString(2, task.getTaskId());
            if(pst.executeUpdate() == 0){
                pst.close();

                pst = con.prepareStatement("insert into tblSegmentTask(taskId, offset) values(?, ?)");
                pst.setString(1, task.getTaskId());
                pst.setLong(2, offset);
                pst.execute();
            }
        }catch(Exception e){
            throw new ActionException("Init error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst);
        }
    }

    @Override
    public void init(SegmentTask task) {
        this.task = task;

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pst = con.prepareStatement("select offset from tblSegmentTask where taskId=?");
            pst.setString(1, task.getTaskId());
            rs = pst.executeQuery();
            if(rs.next()){
                offset = rs.getLong("offset");
            }
        }catch(Exception e){
            throw new ActionException("Init error, thing=" + thing.getMetadata().getPath(), e);
        }finally {
            DbUtil.close(con, pst, rs);
        }
    }

    //动作的实现：xworker.lang.task.impls.DbRangeStore/@actions/@create
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Thing dataSource = self.doAction("getDataSource", actionContext);
        if(dataSource == null){
            throw new ActionException("DataSource can not be null, thing=" + self.getMetadata().getPath());
        }

        return new DbRangeStore(self, dataSource, actionContext);
    }
}
