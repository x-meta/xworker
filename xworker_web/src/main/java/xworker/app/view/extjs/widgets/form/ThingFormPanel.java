package xworker.app.view.extjs.widgets.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class ThingFormPanel {
	//private static Logger log = LoggerFactory.getLogger(ThingFormPanel.class);
	
	public static Object toJavaScriptCode(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        //组件标识，子控件可能会用到
        actionContext.peek().put("cmpId", self.doAction("getComponentId", actionContext));
                
        String thingPath = self.getStringBlankAsNull("thingPath");
        Thing thing = null;
        if(thingPath != null){
        	if(thingPath.startsWith("ognl:")){
        		thingPath = thingPath.substring(5, thingPath.length());
        		Object obj = OgnlUtil.getValue(thingPath, actionContext);
        		if(obj instanceof Thing){
        			thing = (Thing) obj;
        		}else if(obj != null){
        			thing = World.getInstance().getThing(obj.toString());
        		}
        	}else{
        		thing = World.getInstance().getThing(thingPath);
        	}
        }
                
        if(thing == null){
        	thing = self.getThing("Thing@0");        	
        }
        if(thing ==  null){
        	throw new ActionException("ThingForm: thing is null, path=" + self.getMetadata().getPath());
        }
        
        //-----------------要用到的数据对象的相关属性列表---------
        String descPath = self.getStringBlankAsNull("descriptor");
        Thing descriptor = thing.getDescriptor();
        if(descPath != null){
        	Object obj = UtilData.getData(self, "descriptor", actionContext);
        	if(obj instanceof String){
        		Thing desc = World.getInstance().getThing((String) obj);
        		if(desc != null){
        			descriptor = desc;
        		}
        	}else if(obj instanceof Thing){
        		descriptor = (Thing) obj;
        	}
        }
        
        //-----------------创建编辑表单---------------
        Thing formPanel = (Thing) self.doAction("createFormPanel", actionContext);
        //formPanel.put("hideBorders", "true");
        formPanel.put("method", "'POST'");
        formPanel.put("forceLayout", "true");
        formPanel.put("hideMode", "'offsets'");
        if(self.getBoolean("thingFormShowTitle")){
        	formPanel.put("title", "'" + thing.getMetadata().getName() + "-" + descriptor.getMetadata().getLabel() + "(" + descriptor.getMetadata().getName() + ")'");
        }
        
        //自动加载的事物
        Thing listeners = new Thing("xworker.html.extjs.Ext.form.FormPanel/@listeners");
        listeners.put("name", "listeners");
        formPanel.addChild(listeners);
        
        Thing afterrender = new Thing("xworker.html.extjs.Ext.Component/@listeners/@afterrender");
        afterrender.put("name", "afterrender");
        afterrender.put("code", "Ext.getCmp(" + formPanel.getString("id") + ").getForm().load({url:'do?sc=xworker.app.view.extjs.server.ThingForm/@read&thingPath=" + thing.getMetadata().getPath() + "', waitMsg:'正在加载，请稍后...'})" );
        listeners.addChild(afterrender);
                
        //如果子节点有输入方式为file或filePath，那么把表单设置为提交文件的类型
        for(Thing attribute : descriptor.getAllChilds("attribute")){
        	String inputtype = attribute.getString("inputtype");
        	if("file".equals(inputtype) || "filePath".equals(inputtype)){
        		formPanel.put("fileUpload", "true");
        		break;
        	}
        }
        
        
        //------------------创建表单内容---------------   
        createFormItems(self, descriptor, thing, formPanel, actionContext);
        
        //创建更新的表单
        createUpdateFunction(self, formPanel, thing, actionContext);
        
        //清除自定义的属性
        for(String key : formPanel.getAttributes().keySet()){
            if(key.startsWith("df_")){
                formPanel.put(key, null);
            }
        }
        
        //---------------生成JavaScript代码-------------
        return formPanel.doAction("toJavaScriptCode", actionContext);
    }
	
   @SuppressWarnings("unchecked")
	public static void createFormItems(Thing self, Thing descriptor, Thing thing, Thing formPanel, ActionContext actionContext){
    	List<Thing> attributes = new ArrayList<Thing>();
    	//过滤重复的属性
    	Map<String, Thing> context = new HashMap<String, Thing>();
    	for(Thing attr : descriptor.getAllChilds("attribute")){
    		String name = attr.getMetadata().getName();
    		if(context.get(name) != null){
    			continue;
    		}else{
    			context.put(name, attr);
    			attributes.add(attr);
    			
    		}
    	}
    	    	
        
        //创建界面定义，如果没有定义items子事物或是追加模式
        List<Thing> oldItems = (List<Thing>) formPanel.get("items@");
        if(oldItems.size() == 0 || self.getBoolean("df_appendItems")){
            //----------------表单布局参数-------------
            //编辑列数
            int column = self.getInt("df_column", 0);
            if(column == 0){
                column = descriptor.getInt("editCols", 2);
            }
            if(column == 0){
                column = 2;
            }
            
            //标签默认对齐方式
            String columnAlign = self.getString("df_columnAlign");
            if(columnAlign == null || columnAlign == ""){
                columnAlign = descriptor.getString("columnAlign");
            }
            String columnVAlign = self.getString("df_columnVAlign");
            if(columnVAlign == null || columnVAlign == ""){
                columnVAlign = descriptor.getString("columnVAlign");
            }
            if(columnAlign == null){
                columnAlign = "right";
            }
            if(columnVAlign == null){
                columnVAlign = "baseline";
            }
            
            //------------整理隐藏字段和要编辑的字段---------
            Map<String, List<Thing>> fields = new HashMap<String, List<Thing>>();
            fields.put("hiddens", new ArrayList<Thing >());
            fields.put("rows", new ArrayList<Thing>());
            
            for(Thing attribute : attributes){
                if("hidden".equals(attribute.getString("inputtype"))){
                    fields.get("hiddens").add(attribute);
                }else{
                    fields.get("rows").add(attribute);
                }
            }
            
            //----------主表单使用fit布局，包含hiddenPanel和一个tablePanel----
            formPanel.put("layout", "'auto'");    
            Thing formItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            formItems.put("name", "items");
            formPanel.addChild(formItems);
            
            //创建隐藏字段用的面板
            Thing hiddenPanel = new Thing("xworker.html.extjs.Ext.Panel");
            //hiddenPanel.put("hidden", "true");    
            hiddenPanel.put("layout", "'table'");
            Thing hiddenItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            hiddenItems.put("name", "items");
            hiddenPanel.addChild(hiddenItems);
            formItems.addChild(hiddenPanel);
            //创建隐藏字段
            for(Thing row : fields.get("hiddens")){
                Thing hidden = new Thing("xworker.html.extjs.Ext.form.Hidden");
                hidden.put("name", "'" + ((Thing) row.get("attribute")).getString("name") + "'");
                String defaultValue = ((Thing) row.get("attribute")).getStringBlankAsNull("default");
                if(defaultValue != null){
                	hidden.put("value", defaultValue);
                }
                
                hiddenItems.addChild(hidden);
            }
            
            Map<String, List<Thing>> groups = toGroups(fields.get("rows"));
            //不具有分组的情况
            if(groups.size() <= 1){
            	createFormTablePanel(fields.get("rows"), formItems, oldItems, column, self, actionContext);
            }else{
            	//具有分组的情况，创建一个TabFolder
            	Thing tabFolder = new Thing("xworker.html.extjs.Items/@TabPanel");
            	tabFolder.put("autoScroll", "true");
            	tabFolder.put("activeTab", "0");
            	tabFolder.put("defaults", "{ autoScroll:true }");
            	tabFolder.put("hideBorders", "true");
            	tabFolder.put("forceLayout", "true");
            	tabFolder.put("deferredRender", "false");
            	tabFolder.put("layoutOnTabChange", "true");
            	formItems.addChild(tabFolder);
            	Thing tabItems = new Thing("xworker.html.extjs.Ext.Container/@24354");
            	tabItems.put("name", "items");
            	tabFolder.addChild(tabItems);
            	List<String> tabOrders = getGroupOrders(fields.get("rows"));
            	for(String group : tabOrders){
            		Thing panel = createFormTablePanel(groups.get(group), tabItems, oldItems, column, self, actionContext);
            		panel.set("title", "'" + group + "'");
            	}
            	
            }
        }
    }
 
    public static Map<String, List<Thing>> toGroups(List<Thing> attributes){
    	Map<String, List<Thing>> groups = new HashMap<String, List<Thing>>();    	
    	for(Thing attr : attributes){
    		String group = (String) attr.get("group");
    		if(group == null || "".equals(group)){
    			group = "默认";
    		}
    		
    		for(String g : group.split("[,]")){
    			List<Thing> attrs = groups.get(g);
    			if(attrs == null){
    				attrs = new ArrayList<Thing>();
    				groups.put(g, attrs);
    			}
    			
    			attrs.add(attr);
    		}
    	}
    	
    	return groups;
    }
    
    public static List<String> getGroupOrders(List<Thing> attributes){
    	List<String> orders = new ArrayList<String>(); 
    	
    	boolean haveDefault = false;
    	for(Thing attr : attributes){
    		String group = (String) attr.get("group");
    		if(group == null || "".equals(group)){
    			group = "默认";
    			haveDefault = true;
    		}
    		
    		for(String g : group.split("[,]")){
    			if(!orders.contains(g) && !"默认".equals(g)){
    				orders.add(g);
    			}
    		}
    	}
    	
    	if(haveDefault){
    		orders.add(0, "默认");
    	}
    	return orders;
    }
    
    /**
     * 创建一个form布局的面板。
     * 
     * @param rows
     * @param formItems
     * @param oldItems
     * @param column
     * @param self
     * @param actionContext
     */
    public static Thing createFormTablePanel(List<Thing> rows, Thing formItems, List<Thing> oldItems, int column, Thing self, ActionContext actionContext){
    	  //创建表格布局面板
        Thing layoutPanel = new Thing("xworker.html.extjs.Ext.Panel");
        layoutPanel.put("autoScroll", "true");
        layoutPanel.put("forceLayout", "true");
        layoutPanel.put("layout", "'table'");
        if(self.getStringBlankAsNull("df_height") != null){
        	layoutPanel.put("height", self.getString("df_height"));
        }
        if(self.getStringBlankAsNull("df_width") != null){
        	layoutPanel.put("width", self.getString("df_width"));
        }
        formItems.addChild(layoutPanel);
        Thing layoutConfig = new Thing("xworker.html.extjs.Ext.Container/@24356");
        layoutConfig.put("name", "layoutConfig");
        Thing tableLayout = new Thing("xworker.html.extjs.Ext.Container/@24356/@TableLayout");
        tableLayout.put("columns", "" + (column * 2));
        Thing tableAttrs = new Thing("xworker.html.extjs.Ext.layout.TableLayout/@25174");
        tableAttrs.put("name", "tableAttrs");
        tableAttrs.put("code", "cellspacing: 5");
    
        layoutPanel.addChild(layoutConfig);
        layoutConfig.addChild(tableLayout);
        tableLayout.addChild(tableAttrs);
        
        //条目
        Thing items = new Thing("xworker.html.extjs.Ext.Container/@24354");
        layoutPanel.addChild(items);
        items.put("name", "items");
        if(oldItems != null){
            //加入已定义的条目
            for(Thing item : oldItems){
                items.addChild(item);
            }
        }
        for(Thing row : rows){
        	
            String type = row.getString("inputtype");
            if(type == null || "".equals(type)){
                type = "text";
            }
            int rowspan = row.getInt("rowspan", 1);
            int colspan = row.getInt("colspan", 1);
            if(colspan > column){
            	colspan = column;
            }
            if(!row.getBoolean("showLabel", true)){
                colspan = 2 * colspan;
            }else{
                colspan = 2 * colspan - 1;
                //创建标签
                Thing label = new Thing("xworker.html.extjs.Items/@Label");
                label.put("text", "'" + row.getMetadata().getLabel() + ":'");
                label.put("cls", "'x-form-field'");
                label.put("id", "'" + row.getString("name") + "-" + row.getMetadata().getPath().hashCode() + "'");
                label.put("colspan", "1");
                label.put("rowspan",  "1");
                items.addChild(label);
            }    
         
            Map<String, Object> field = new HashMap<String, Object>();
            field.putAll(row.getAttributes());
            field.put("attribute", row);
            field.put("viewConfig", row);
            Thing childItem = (Thing) self.doAction("createExtFieldEditor", actionContext, UtilMap.toMap(new Object[]{"field", field}));
            if(childItem != null){
            	if(childItem.getInt("colspan") > column){
            		childItem.put("colspan", "1");
            	}
            	childItem.put("rowspan", "1");
                items.addChild(childItem, false);
            }
        }
        
        return layoutPanel;
    }
    
	public static void createUpdateFunction(Thing self, Thing formPanel, Thing thing, ActionContext actionContext){
        //创建
        Thing createFunction = new Thing("xworker.html.extjs.Function");
        createFunction.put("params", "");
        createFunction.put("name", "doUpdate");
        String createUrl = "do?sc=xworker.app.view.extjs.server.ThingForm/@update&thingPath=" + thing.getMetadata().getPath();
        String msg = "正在保存数据，请稍候...";        
        
        String code = "";
    
        code = "//提交数据\n" + 
"        Ext.getCmp(" + formPanel.get("id") + ").getForm().submit({\n" + 
"            url:'" + createUrl + "', \n" + 
"            params: Ext.getCmp(" + formPanel.get("id") + ").getForm().getFieldValues(true)," + 
"            waitMsg:'" + msg + "',\n" + 
"            success: function(form, action) {\n" +  
"               Ext.Msg.alert('成功', action.result.msg);\n" + 
"            },\n" + 
"            failure: function(form, action) {\n" + 
"                switch (action.failureType) {\n" + 
"                    case Ext.form.Action.CLIENT_INVALID:\n" + 
"                        Ext.Msg.alert('失败', '数据校验失败');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.CONNECT_FAILURE:\n" + 
"                        Ext.Msg.alert('失败', 'Ajax通讯错误');\n" + 
"                        break;\n" + 
"                    case Ext.form.Action.SERVER_INVALID:\n" + 
"                       Ext.Msg.alert('失败', action.result.msg);\n" + 
"                }\n" + 
"            }\n" + 
"        });\n";
        createFunction.put("code", code);
        
        formPanel.addChild(createFunction);
    }
}
