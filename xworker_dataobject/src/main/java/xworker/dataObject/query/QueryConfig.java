package xworker.dataObject.query;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.query.projections.SqlProjection;

import java.util.*;

public class QueryConfig {
    Condition condition;
    PageInfo pageInfo;
    List<Projection> projections = new ArrayList<>();
    List<Group> groups = new ArrayList<>();
    List<Order> orders = new ArrayList<>();

    public QueryConfig(){
        condition = new Condition();
        pageInfo = new PageInfo();
    }

    public QueryConfig(Condition condition){
        this.condition = condition;
        pageInfo = new PageInfo();
    }

    public QueryConfig(Thing queryCondition, Map<String, Object> queryValues, PageInfo pageInfo, ActionContext actionContext){
        condition = Condition.parse(queryCondition, queryValues, actionContext);
        if(pageInfo != null) {
            this.pageInfo = pageInfo;
            if (pageInfo.getSort() != null && !pageInfo.getSort().isEmpty()) {
                addOrder(pageInfo.getSort(), pageInfo.getDir());
            }
        }else{
            this.pageInfo = new PageInfo();
        }
    }

    public QueryConfig(Thing queryCondition, Map<String, Object> queryValues, ActionContext actionContext){
        condition = Condition.parse(queryCondition, queryValues, actionContext);
        pageInfo = new PageInfo();
    }

    public Condition condition(){
        return condition;
    }

    public PageInfo pageInfo(){
        return pageInfo;
    }

    public void addProjection(String sql){
        projections.add(new SqlProjection(sql));
    }

    public void addOrder(String name, String dir){
        orders.add(new Order(name, dir));
    }

    public void addGroup(String group){
        groups.add(new Group(group));
    }

    public String getConditionSql(){
        return condition.toSql();
    }

    public List<ConditionValue> getConditionValueList(){
        return condition.getConditionValueList();
    }

    public Map<String, Object> getConditionValueMap(){
        return condition.getConditionValues();
    }

    public String getProjetionsSql(){
        StringBuilder sb = new StringBuilder();
        for(Projection projection : projections){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append(projection.toSql());
        }

        return sb.toString();
    }

    public String getOrdersSql(){
        StringBuilder sb = new StringBuilder();
        for(Order order : orders){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append(order.toSql());
        }

        return sb.toString();
    }

    public String getGroupsSql(){
        StringBuilder sb = new StringBuilder();
        for(Group group : groups){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append(group.toSql());
        }

        return sb.toString();
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void setProjections(List<Projection> projections) {
        this.projections = projections;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Condition getCondition() {
        return condition;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public List<Projection> getProjections() {
        return projections;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Order> getOrders() {
        return orders;
    }

    /**
     * 设置查询结果，并且执行分页和排序等
     */
    public List<DataObject> setQueryResults(List<DataObject> datas){
        //是否排序
        if(orders.size() > 0){
            Collections.sort(datas, new Comparator<DataObject>(){
                @Override
                public int compare(DataObject o1, DataObject o2) {
                    for(Order order : orders) {
                        if(o1 == null && o2 == null){
                            continue;
                        }else if(o1 == null){
                            return 1;
                        }else if(o2 == null){
                            return -1;
                        }

                        String av = o1.getString(order.getName());
                        String bv = o2.getString(order.getName());
                        if (av == null && bv == null) {
                            continue;
                        } else if (av == null) {
                            return "DESC".equals(order.getDir()) ? 1 : -1;
                        } else if (bv == null) {
                            return "DESC".equals(order.getDir()) ? -1 : 1;
                        } else {
                            return "DESC".equals(pageInfo.getDir()) ? -av.compareTo(bv) : av.compareTo(bv);
                        }
                    }

                    return 0;
                }
            });
        }

        pageInfo.setTotalCount( datas.size());
        if(pageInfo.getLimit() > 0){
            if(pageInfo.getStart() > datas.size()){
                pageInfo.setStart(datas.size());
            }
            long toIndex = pageInfo.getStart() + pageInfo.getLimit();
            if(toIndex > datas.size()){
                toIndex = datas.size();
            }
            long startIndex = pageInfo.getStart();
            if(startIndex < 0){
                startIndex = 0;
            }
            pageInfo.setDatas(datas.subList((int) pageInfo.getStart(), (int) toIndex));
        }else{
            pageInfo.setDatas(datas);
        }
        return pageInfo.getDatas();
    }
}
