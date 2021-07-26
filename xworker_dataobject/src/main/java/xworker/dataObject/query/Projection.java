package xworker.dataObject.query;

import xworker.dataObject.DataObject;

public interface Projection {
    public String toSql();

    public void calc(DataObject dataObject);

    /**
     * 返回最后的计算结果。
     *
     * @return 聚合后的结果
     */
    public Object getValue();
}
