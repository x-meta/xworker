package xworker.swt.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.util.UtilData;

public class KeyTravel {
	private static Logger logger = LoggerFactory.getLogger(KeyTravel.class);
	
	public static final int CR = 13;
	public static final int DOWN = 16777218;
	public static final int LEFT = 16777219;
	public static final int RIGHT = 16777220;
	public static final int UP = 16777217;
		
	public static void run(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		Event event = actionContext.getObject("event");
		if(event == null){
			return;
		}
		
		Object widget = UtilData.getObject(self, "widget", actionContext);
		
		if(widget instanceof Table){
			handleTable(event, (Table) widget, self, actionContext);
		}else if(widget instanceof Tree){
			handleTree(event, (Tree) widget, self, actionContext);
		}else if(widget instanceof List){
			handleList(event, (List) widget, self, actionContext);
		}else{
			logger.warn("widget is unknown type, widget=" + widget + ", action=" + self.getMetadata().getPath());
		}
	}
	
	public static void handleList(Event event, List list, Thing self, ActionContext actionContext){
		if(list.getItemCount() <= 0){
			return;
		}
		
		//如果没有选择，选择第一个
		if(list.getSelectionIndex() == -1){
			if(list.getItems().length > 0){
				list.select(0);
				handeSelection(self, list, list.getSelection()[0], actionContext);
			}			
			
			return;
		}
		
		int index = list.getSelectionIndex();
		if(event.keyCode == UP){
			if(index > 0){
				list.select(index - 1);				
			}else{
				list.select(list.getItemCount() - 1);
			}
			list.showSelection();
			handeSelection(self, list, list.getSelectionIndex(), actionContext);
			event.doit = false;
		}else if(event.keyCode == DOWN){
			if(index < list.getItems().length - 1){				
				list.select(index + 1);
			}else{
				list.select(0);				
			}
			
			list.showSelection();
			handeSelection(self, list, list.getSelectionIndex(), actionContext);
		}else if(event.keyCode == CR){
			handleDefaultSelection(self, list, list.getSelectionIndex(), actionContext);
		}
	}
	
	public static void handleTable(Event event, Table table, Thing self, ActionContext actionContext){
		TableItem[] items = table.getSelection();
		
		//如果没有选择，选择第一个
		if(items == null || items.length == 0){
			if(table.getItems().length > 0){
				table.select(0);
				handeSelection(self, table, table.getSelection()[0], actionContext);
			}			
			
			return;
		}
		
		int index = table.getSelectionIndex();
		if(event.keyCode == UP){
			if(index > 0){
				table.select(index - 1);
				table.showSelection();
				handeSelection(self, table, table.getSelection()[0], actionContext);
			}
		}else if(event.keyCode == DOWN){
			if(index < table.getItems().length - 1){
				table.select(index + 1);
				table.showSelection();
				handeSelection(self, table, table.getSelection()[0], actionContext);
				//table.
			}
		}else if(event.keyCode == CR){
			if(items.length > 0){
				handleDefaultSelection(self, table, items[0], actionContext);
			}
		}
	}
	
	public static void handleTree(Event event, Tree tree, Thing self, ActionContext actionContext){
		TreeItem[] items = tree.getSelection();
		//如果没有选择选择第一个
		if(items == null || items.length == 0){
		    if(tree.getItems().length > 0){
		    	tree.select(tree.getItems()[0]);
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }
		    
		    return;
		}

		//设第一个
		TreeItem item = items[0];
		if(event.keyCode == DOWN){
		    //下
		    TreeItem nextItem = getNextItem(tree, item);
		    if(nextItem != null){
		    	tree.select(nextItem);
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }else if(item.getItems().length > 0){
		    	tree.select(item.getItems()[0]);
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }
		}else if(event.keyCode == RIGHT){
		    //右
		    if(item.getItems().length > 0){
		    	tree.select(item.getItems()[0]);
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }
		}else if(event.keyCode == UP){
		    //上
		    TreeItem nextItem = getPreItem(tree, item);
		    if(nextItem != null){
		    	tree.select(nextItem);
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }   
		}else if(event.keyCode == LEFT){
		    //左
		    if(item.getParentItem() != null){
		    	tree.select(item.getParentItem());
		    	handeSelection(self, tree, tree.getSelection()[0], actionContext);
		    }
		}else if(event.keyCode == CR){
		    //回车
			if(items.length > 0){
				handleDefaultSelection(self, tree, items[0], actionContext);
			}
		}
	}
	
	public static void handeSelection(Thing self,  Object widget, Object item, ActionContext actionContext){
		Action defaultSelection = World.getInstance().getAction(self.getString("selectionListener"));
		if(defaultSelection != null){
			defaultSelection.run(actionContext, UtilMap.toMap("widget", widget, "item", item));
		}
	}
	
	public static void handleDefaultSelection(Thing self, Object widget, Object item, ActionContext actionContext){
		Action defaultSelection = World.getInstance().getAction(self.getString("defaultSelection"));
		if(defaultSelection != null){
			defaultSelection.run(actionContext, UtilMap.toMap("widget", widget, "item", item));
		}
	}
	
	public static TreeItem getNextItem(Tree thingTree, TreeItem item){
	    if(item == null){
	        return null;
	    }
	    
	    Object parent = item.getParentItem();
	    if(parent == null){
	        parent = thingTree;
	    }
	    
	    boolean next = false;
	    TreeItem[] items = null;
	    if(parent instanceof Tree){
	    	items = ((Tree) parent).getItems();
	    }else{
	    	items = ((TreeItem) parent).getItems();
	    }
	    for(TreeItem child : items){
	        if(next){
	            return child;
	        }else if(child == item){
	            next = true;
	        }
	    }
	    
	    return getNextItem(thingTree, item.getParentItem());
	}

	public static TreeItem getPreItem(Tree thingTree, TreeItem item){
	    if(item == null){
	        return null;
	    }
	    
	    Object parent = item.getParentItem();
	    if(parent == null){
	        parent = thingTree;
	    }
	    
	    TreeItem[] items = null;
	    if(parent instanceof Tree){
	    	items = ((Tree) parent).getItems();
	    }else{
	    	items = ((TreeItem) parent).getItems();
	    }
	    
	    TreeItem preItem = null;
	    for(TreeItem child : items){
	        if(child == item){
	            if(preItem == null){
	                return item.getParentItem();
	            }else{
	                return preItem;
	            }
	        }else{
	            preItem = child;
	        }
	    }
	    
	    return getPreItem(thingTree, item.getParentItem());
	}
}
