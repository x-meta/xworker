package xworker.dataObject.db;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectIterator;
import xworker.dataObject.PageInfo;
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
    PageInfo pageInfo;
    int count = 0;

    public DbDataObjectIterator(Thing self, List<Thing> attributes, PageInfo pageInfo, Connection con, PreparedStatement pst, ResultSet rs, ActionContext actionContext) {
        this.self = self;
        this.attributes = attributes;
        this.con = con;
        this.pst = pst;
        this.rs = rs;
        this.actionContext = actionContext;
        this.pageInfo = pageInfo;
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
            if(pageInfo != null && pageInfo.getLimit() > 0 && count > pageInfo.getLimit()){
                //超出了分页限制
                return false;
            }

            return rs.next();
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public DataObject next() {
        //构造对象
        DataObject data = new DataObject(self);
        data.setInited(false);
        //设置属性值
        for(int i=0; i<attributes.size(); i++){
            try {
                data.put(attributes.get(i).getString("name"), DbUtil.getValue(rs, attributes.get(i)));
            }catch(Exception e){
                Executor.warn(TAG, "Get valueError, attribute=" + attributes.get(i).getMetadata().getPath(), e);
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

    public ActionContext getActionContext() {
        return actionContext;
    }

    @Override
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public int getCount() {
        return count;
    }
}
