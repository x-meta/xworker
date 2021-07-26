package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.dataObject.DataObject;
import xworker.dataObject.query.Condition;
import xworker.lang.executor.Executor;

import java.util.ArrayList;
import java.util.List;

public class DataObjectTreeModel {
    private static final String TAG = DataTreeModel.class.getName();

    public static Object getRoot(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        TreeModel treeModel = actionContext.getObject("treeModel");

        //数据对象
        Thing dataObject = self.doAction("getDataObject", actionContext);

        if(dataObject == null){
            Executor.warn(TAG, "DataObject is null, can not create root, path=" + self.getMetadata().getPath());
            return null;
        }else{
            String idField = self.getString("idField");
            Object rootId = self.get("rootId");

            DataObject rootData = new DataObject(dataObject);
            Condition condition = new Condition();
            condition.eq(idField, rootId);

            self.doAction("initCondition", actionContext, "treeModel", treeModel, "parentItem", null, "condition", condition);
            rootData = rootData.load(actionContext, condition);
            if(rootData == null){
                rootData = new DataObject(dataObject);
                rootData.put(idField, rootId);
            }

            return dataObjectToNode(treeModel, null, self, rootData, actionContext);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getChilds(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        Thing dataObject = self.doAction("getDataObject", actionContext);

        if(dataObject == null){
            Executor.warn(TAG, "DataObject is null, can not create root, path=" + self.getMetadata().getPath());
            return null;
        }

        //查询配置
        DataObject parentData = (DataObject) parentItem.getSource();
        Condition condition = new Condition();
        condition.eq(self.getString("parentIdField"), parentData.get(self.getString("idField")));
        self.doAction("initCondition", actionContext, "treeModel", treeModel, "parentItem", parentItem, "condition", condition);

        //查询子节点
        List<DataObject> datas = parentData.query(actionContext, condition);
        List<TreeModelItem> items = new ArrayList<>();
        for(DataObject data : datas){
            items.add(dataObjectToNode(treeModel, parentItem, self, data, actionContext));
        }

        return datas;
    }

    public static TreeModelItem dataObjectToNode(TreeModel treeModel, TreeModelItem parentItem, Thing self, DataObject data, ActionContext actionContext) {
        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
        item.setSource(data);
        String id = data.getString(self.getString("idField"));
        item.setId(treeModel.getThing().getMetadata().getPath() + "|" + id);
        item.setDataId(id);
        item.setText(data.getString(self.getString("textField")));
        item.setIcon(data.getString(self.getString("iconField")));
        item.setCls(data.getString(self.getString("clsField")));
        item.setLeaf(data.getBoolean(self.getString("isLeafField")));

        self.doAction("init", actionContext, "item", item, "data", data);

        return item;
    }

    public static TreeModelItem getItemById(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeModel treeModel = actionContext.getObject("model");

        //数据对象
        Thing dataObject = self.doAction("getDataObject", actionContext);

        if(dataObject == null){
            Executor.warn(TAG, "DataObject is null, can not create root, path=" + self.getMetadata().getPath());
            return null;
        }else{
            String idField = self.getString("idField");

            String rootId = actionContext.getObject("id");
            DataObject rootData = new DataObject(dataObject);
            Condition condition = new Condition();
            condition.eq(idField, rootId);

            self.doAction("initCondition", actionContext, "treeModel", treeModel, "parentItem", null, "condition", condition);
            rootData = rootData.load(actionContext, condition);
            if(rootData == null){
                return null;
            }

            return dataObjectToNode(treeModel, null, self, rootData, actionContext);
        }
    }
}
