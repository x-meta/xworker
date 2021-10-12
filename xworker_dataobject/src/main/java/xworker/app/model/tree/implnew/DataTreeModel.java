package xworker.app.model.tree.implnew;

import ognl.OgnlException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelUtils;
import xworker.lang.executor.Executor;
import xworker.util.XWorkerUtils;

import java.util.ArrayList;
import java.util.List;

public class DataTreeModel {
    private static final String TAG =  DataTreeModel.class.getName();

    public static Object getRoot(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");

        Object data = actionContext.get(self.getString("varName"));
        if(data == null){
            return null;
        }else{
            TreeModel treeModel = actionContext.getObject("treeModel");
            TreeModelItem parentItem = actionContext.getObject("parentItem");

            return dataToItem(treeModel, parentItem, self, data);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getChilds(ActionContext actionContext) throws OgnlException {
        Thing self = (Thing) actionContext.get("self");

        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        Object parentData = parentItem.getSource();
        Iterable<Object> childDatas = (Iterable<Object>) OgnlUtil.getValue(self.getString("childsName"), parentData);
        List<TreeModelItem> items = new ArrayList<>();
        if(childDatas != null){
            for(Object data : childDatas){
                items.add(dataToItem(treeModel, parentItem, self, data));
            }
        }

        return items;
    }

    //xworker.app.model.tree.implnew.DataTreeModel/@actions/@createBySources
    public static List<TreeModelItem> createBySources(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");
        Object[] sources = actionContext.getObject("sources");

        List<TreeModelItem> items = new ArrayList<>();

        for(Object obj : sources){
            items.add(dataToItem(treeModel, parentItem, self, obj));
        }

        return items;
    }

    public static TreeModelItem dataToItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, Object data){
        return TreeModelUtils.toItem(treeModel, parentItem, self, data);
    }


}
