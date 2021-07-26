package xworker.app.model.tree.implnew;

import ognl.OgnlException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.lang.executor.Executor;

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

    public static TreeModelItem dataToItem(TreeModel treeModel, TreeModelItem parentItem, Thing self, Object data){
        String id = getValue(data, self.getString("idName"));
        if(id == null){
            return null;
        }

        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
        item.setText(getValue(data, self.getString("textName")));
        item.setIcon(getValue(data, self.getString("iconName")));
        item.setCls(getValue(data, self.getString("clsName")));
        item.setId(treeModel.getThing().getMetadata().getPath() + "|" + id);
        item.setDataId(id);
        item.setSource(data);

        return item;
    }

    public static String getValue(Object data, String name){
        try{
            if(name == null || "".equals(name)){
                return null;
            }

            Object v = OgnlUtil.getValue(name, data);
            if(v != null){
                return v.toString();
            }else{
                return null;
            }
        }catch(Exception e){
            Executor.warn(TAG, "DataTreeModel getRoot: getValueError, name=" + name, e);
            return null;
        }
    }
}
