package xworker.dataObject.iterators;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectIterator;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.QueryConfig;

import java.util.List;

/**
 * 使用分页进行迭代的数据对象迭代器。
 */
public class PageDataObjectIterator implements DataObjectIterator {
    DataObject dataObject;
    QueryConfig queryConfig;
    List<DataObject> datas;
    ActionContext actionContext;
    long index = 0;

    public PageDataObjectIterator(DataObject dataObject, QueryConfig queryConfig, ActionContext actionContext){
        this.dataObject = dataObject;
        this.queryConfig = queryConfig;
        this.actionContext = actionContext;

        //分到首页
        queryConfig.getPageInfo().setPage(0);
        queryConfig.getPageInfo().setPageSize(1000);

        //查询第一页的数据
        datas = dataObject.query(actionContext, queryConfig);
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean hasNext() {
        PageInfo pageInfo = queryConfig.getPageInfo();
        if(index >= pageInfo.getTotalCount()){
            //超出最大记录了，那么是迭代到了最后
            return false;
        }

        if((pageInfo.getPage() + 1) * pageInfo.getPageSize() <= index){
            //当前页取完了，取下一页
            pageInfo.setPage(pageInfo.getPage() + 1);
            datas = dataObject.query(actionContext, queryConfig);
        }

        if(index >= pageInfo.getTotalCount()){
            //超出最大记录了，那么是迭代到了最后
            return false;
        }else {
            return index % pageInfo.getPageSize() < datas.size();
        }
    }

    @Override
    public DataObject next() {
        DataObject data =  datas.get((int) (index % queryConfig.getPageInfo().getPageSize()));
        index++;
        return data;
    }

    @Override
    public Thing getDataObject() {
        return dataObject.getMetadata().getDescriptor();
    }

    @Override
    public QueryConfig getQueryConfig() {
        return queryConfig;
    }

    @Override
    public ActionContext getActionContext() {
        return actionContext;
    }
}
