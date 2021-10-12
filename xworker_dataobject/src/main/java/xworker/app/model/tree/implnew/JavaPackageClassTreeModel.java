package xworker.app.model.tree.implnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.java.assist.JavaCacheItem;
import xworker.java.assist.JavaClassCache;
import xworker.util.XWorkerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JavaPackageClassTreeModel {
    public static TreeModelItem getRoot(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeModel treeModel = actionContext.getObject("treeModel");

        String root = self.doAction("getRootPath", actionContext);
        TreeModelItem rootItem = new TreeModelItem(treeModel, null);
        if(root == null || root.isEmpty())  {
            rootItem.setText("/");
            rootItem.setIcon("icons/brick.png");

            JavaCacheItem item = JavaClassCache.getRootItem();
            rootItem.setSource(item);
        }else{
            JavaCacheItem javaItem = JavaClassCache.getJavaCacheItem(root);
            if(javaItem == null){
                rootItem.setText("Not found: " + root);
            }else{
                rootItem = new TreeModelItem(treeModel, null);
                initItem(rootItem, javaItem);
            }
        }

        return rootItem;
    }

    public static Object getChilds(ActionContext actionContext) throws Exception{
        Thing self = actionContext.getObject("self");

        TreeModel treeModel = actionContext.getObject("treeModel");
        TreeModelItem parentItem = actionContext.getObject("parentItem");

        List<TreeModelItem> items = new ArrayList<>();
        Object source = parentItem.getSource();

        if(source instanceof JavaCacheItem){
            boolean flat = self.getBoolean("flat");
            boolean showClass = self.getBoolean("showClass");
            boolean showField = self.getBoolean("showField");
            boolean showMethod = self.getBoolean("showMethod");
            boolean showInnerClass = self.getBoolean("showInnerClasss");

            JavaCacheItem javaItem = (JavaCacheItem) source;
            if(javaItem.isRoot()){
                if(flat){
                    for(String packageName : JavaClassCache.getAllPackages()){
                        JavaCacheItem packageItem = JavaClassCache.getJavaCacheItem(packageName);
                        if(packageItem != null && packageItem.isPackage()){
                            TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                            initItem(item, packageItem);
                            items.add(item);
                        }
                    }

                    return items;
                }else{
                    for (JavaCacheItem childItem : javaItem.getItems()) {
                        if (childItem.isClass() && !showClass) {
                            continue;
                        }

                        if(childItem.isClass() && childItem.getName().contains("$")){
                            //默认不显示内部类，内部类在Class节点下显示
                            continue;
                        }

                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        initItem(item, childItem);
                        items.add(item);
                    }
                }
            }else  if(javaItem.isPackage()) {
                if(flat){
                    for(JavaCacheItem childItem : JavaClassCache.startsWith(javaItem.getName())){
                        if(parentItem.getParent() == null && childItem.isClass()){
                            //第一级水平节点全部是pacakge，不包含类
                            continue;
                        }

                        if(parentItem.getParent() != null && childItem.isPackage()){
                            //除了第一级子节点其它节点下不包含包，因此包全部显示在第一级子节点了
                            continue;
                        }

                        if (childItem.isClass() && !showClass) {
                            continue;
                        }

                        if(childItem.isClass() && childItem.getName().contains("$")){
                            //默认不显示内部类，内部类在Class节点下显示
                            continue;
                        }

                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        initItem(item, childItem);
                        if(childItem.isPackage()) {
                            item.setText(childItem.getName());
                        }else{
                            item.setText(childItem.getSimpleName());
                        }
                        items.add(item);
                    }
                }else {
                    for (JavaCacheItem childItem : javaItem.getItems()) {
                        if (childItem.isClass() && !showClass) {
                            continue;
                        }

                        if (childItem.isClass() && childItem.getName().contains("$")) {
                            //默认不显示内部类，内部类在Class节点下显示
                            continue;
                        }

                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        initItem(item, childItem);
                        items.add(item);
                    }
                }
            }else if(javaItem.isClass()){
                Class<?> cls = World.getInstance().getClassLoader().loadClass(javaItem.getName());

                if(showInnerClass){
                    for(JavaCacheItem childItem : JavaClassCache.indexOf(javaItem.getName())){
                        if(childItem.isClass() && childItem.getName().contains("$")){
                            TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                            initItem(item, childItem);
                            items.add(item);
                        }
                    }
                }
                if(showField){
                    for(Field field : cls.getDeclaredFields()){
                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        initItem(item, field);
                        items.add(item);
                    }
                }

                if(showMethod){
                    for(Method method : cls.getDeclaredMethods()){
                        TreeModelItem item = new TreeModelItem(treeModel, parentItem);
                        initItem(item, method);
                        items.add(item);
                    }
                }
            }
        }

        return items;
    }

    //xworker.app.model.tree.implnew.JavaPackageClassTreeModel/@actions/@createBySources
    public static List<TreeModelItem> createBySources(ActionContext actionContext){
        return Collections.emptyList();
    }

    public static TreeModelItem getItemById(ActionContext actionContext){
        TreeModel treeModel = actionContext.getObject("treeModel");
        String id = actionContext.getObject("id");

        String path;
        String methodName;
        String fieldName;
        if(id.contains("@")){
            int index = id.indexOf("@");
            path = id.substring(0, index);
            methodName = id.substring(index + 1, id.length());
        }else if(id.contains("#")){
            int index = id.indexOf("#");
            path = id.substring(0, index);
            fieldName = id.substring(index + 1, id.length());
        }else{
            path = id;
        }

        JavaCacheItem item = JavaClassCache.getJavaCacheItem(path);
        if(item != null){
            TreeModelItem treeModelItem = new TreeModelItem(treeModel, null);
            initItem(treeModelItem, item);
            return treeModelItem;
        }else{
            return null;
        }
    }

    public static void initItem(TreeModelItem item, Object object){
        item.setSource(object);
        String id = item.getTreeModel().getThing().getMetadata().getPath() + "|";

        if(object instanceof  Method){
            Method method = (Method) object;
            StringBuilder sb = new StringBuilder(method.getName());
            sb.append("(");
            Parameter[] params = method.getParameters();
            for(int i=0; i<params.length; i++){
                sb.append(params[i].getType().getSimpleName());
                if(i < params.length - 1){
                    sb.append(", ");
                }
            }
            sb.append("): ");
            sb.append(method.getReturnType().getSimpleName());
            item.setText(sb.toString());
            String path = method.getDeclaringClass().getName() + "@" + method.getName();
            item.setId(id + path);
            item.setDataId(path);
            item.setLeaf(true);
            item.setIcon("icons/bullet_green.png");
        }else if(object instanceof Field){
            Field field = (Field) object;
            item.setText(field.getName() + ": " + field.getDeclaringClass().getSimpleName());
            item.setIcon("icons/text_list_bullets.png");
            item.setLeaf(true);
            String path = field.getDeclaringClass().getName() + "#" + field.getName();
            item.setId(id + path);
            item.setDataId(path);
        }else if(object instanceof JavaCacheItem){
            JavaCacheItem cacheItem = (JavaCacheItem) object;
            item.setText(cacheItem.getSimpleName());
            if (cacheItem.isPackage()) {
                item.setIcon("icons/xworker/package.gif");
            } else {
                item.setIcon("icons/xworker/normalFile.gif");
            }
            item.setId(id + cacheItem.getName());
            item.setDataId(cacheItem.getName());
        }
    }
}
