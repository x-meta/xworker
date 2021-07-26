package xworker.dataObject.query.projections;

import xworker.dataObject.DataObject;
import xworker.dataObject.query.Projection;

public class SqlProjection implements Projection {
    String sql;

    public SqlProjection(String sql){
        this.sql = sql;
    }

    @Override
    public String toSql() {
        return sql;
    }

    @Override
    public void calc(DataObject dataObject) {

    }

    @Override
    public Object getValue() {
        return null;
    }
}
