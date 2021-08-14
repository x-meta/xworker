package xworker.dataObject.db;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectIterator;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.QueryConfig;
import xworker.dataObject.utils.DbUtil;
import xworker.lang.executor.Executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DbDataObjectIterator implements DataObjectIterator {
    private static final String TAG = DbDataObjectIterator.class.getName();

    Thing self;
    List<Thing> attributes;
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    ActionContext actionContext;
    QueryConfig queryConfig;
    int count = 0;

    public DbDataObjectIterator(Thing self, List<Thing> attributes, QueryConfig queryConfig, Connection con, PreparedStatement pst, ResultSet rs, ActionContext actionContext) {
        this.self = self;
        this.attributes = attributes;
        this.con = con;
        this.pst = pst;
        this.rs = rs;
        this.actionContext = actionContext;
        this.queryConfig = queryConfig;
    }

    @Override
    public void close() throws Exception {
        if(rs != null){
            try{
                rs.close();
            }catch (Exception ignored){
            }
        }
        if(pst != null){
            try{
                pst.close();
            }catch(Exception ignored){
            }
        }
        if(con != null){
            try{
                con.close();
            }catch(Exception ignored){
            }
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return rs.next();
        }catch(Exception e){
            Executor.warn(TAG, "do hasNext() error", e);
            return false;
        }
    }

    @Override
    public DataObject next() {
        //构造对象
        DataObject data = new DataObject(self);
        data.setInited(false);
        //设置属性值
        for (Thing attribute : attributes) {
            try {
                data.put(attribute.getString("name"), DbUtil.getValue(rs, attribute));
            } catch (Exception e) {
                Executor.warn(TAG, "Get valueError, attribute=" + attribute.getMetadata().getPath(), e);
            }
        }

        data.setInited(true);

        count++;
        return data;
    }

    public Thing getSelf() {
        return self;
    }

    public List<Thing> getAttributes() {
        return attributes;
    }

    public Connection getCon() {
        return con;
    }

    public PreparedStatement getPst() {
        return pst;
    }

    public ResultSet getRs() {
        return rs;
    }

    @Override
    public Thing getDataObject() {
        return self;
    }

    @Override
    public QueryConfig getQueryConfig() {
        return null;
    }

    public ActionContext getActionContext() {
        return actionContext;
    }

    public int getCount() {
        return count;
    }
}
