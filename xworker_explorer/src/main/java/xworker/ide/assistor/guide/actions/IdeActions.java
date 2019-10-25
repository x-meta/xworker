package xworker.ide.assistor.guide.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.events.SwtListener;
import xworker.swt.util.SwtDialog;

public class IdeActions {
	/** 所有项目树的菜单集合，string[]第一位是菜单变量名，第二位是菜单动作变量名 */
	static Map<String, String[]> menuItems = new HashMap<String, String[]>();
	static{
		menuItems.put("addThingManagerItem", new String[]{"addThingManagerItem", "addThingManagerItemSelection"});
		menuItems.put("workingSetMenuItem", new String[]{"workingSetMenuItem", "workingSetMenuItemSelection"});
		menuItems.put("refreshMenuItem", new String[]{"refreshMenuItem", "refreshMenuItemSelection"});
		menuItems.put("editMenuItem", new String[]{"editMenuItem", "editMenuItemSelection"});
		menuItems.put("newThingMenuItem", new String[]{"newThingMenuItem", "newThingMenuItemSelection"});
		menuItems.put("newCategoryMenuItem", new String[]{"newCategoryMenuItem", "newCategoryMenuItemSelection"});
		menuItems.put("thingManagerDeleteMenuItem", new String[]{"thingManagerDeleteMenuItem", "thingManagerDeleteMenuItemSelection"});
		menuItems.put("rebuildIndexMenuItem", new String[]{"rebuildIndexMenuItem", "rebuildIndexMenuItemSelection"});
		menuItems.put("deleteCategoryMenuItem", new String[]{"deleteCategoryMenuItem", "deleteCategoryMenuItemSelection"});
	}
	
	public static Object openProjectTreeMenu(ActionContext actionContext){
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
		
		//初始化菜单
		SwtListener projectTreeMenudetect = (SwtListener) explorerContext.get("projectTreeMenudetect");
		projectTreeMenudetect.handleEvent(null);
		
		//显示菜单
		Menu projectTreeMenu = (Menu) explorerContext.get("projectTreeMenu");
		Tree projectTree = (Tree) explorerContext.get("projectTree");
		TreeItem treeItem = projectTree.getSelection()[0];
		Rectangle rec = treeItem.getBounds();
		
		Point pt = new Point(rec.x, rec.y + rec.height);
		pt = treeItem.getParent().toDisplay(pt);
		projectTreeMenu.setLocation(pt.x, pt.y);
		//projectTreeMenu.update();
		projectTreeMenu.setVisible(true);
		
		return projectTreeMenu.getData("menuContext");
	}
	
	public static void selectProjectTreeMenuItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
				
		//显示菜单
		Menu projectTreeMenu = (Menu) explorerContext.get("projectTreeMenu");
		ActionContext menuContext = (ActionContext) projectTreeMenu.getData("menuContext");
		String[] menuItem = menuItems.get(self.getString("menuItem")); 
		if(menuItem != null){
			GuideCommonActions.showMessageBox((Shell) explorerContext.get("shell"), "项目树菜单" + self.getString("menuItem") + "不存在！");
		}
		MenuItem item = (MenuItem) menuContext.get(menuItem[0]);
		if(item == null || item.isDisposed()){
			GuideCommonActions.showMessageBox((Shell) explorerContext.get("shell"), "项目树菜单" + self.getString("menuItem") + "不存在或已经Disposed了！");
		}
		item.setSelection(true);
	}
	
	public static void execProjectTreeMenuItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
				
		//显示菜单
		Menu projectTreeMenu = (Menu) explorerContext.get("projectTreeMenu");
		ActionContext menuContext = (ActionContext) projectTreeMenu.getData("menuContext");
		String[] menuItem = menuItems.get(self.getString("menuItem")); 
		if(menuItem != null){
			GuideCommonActions.showMessageBox((Shell) explorerContext.get("shell"), "项目树菜单" + self.getString("menuItem") + "不存在！");
		}
		SwtListener itemListener = (SwtListener) menuContext.get(menuItem[1]);
		if(itemListener == null){
			GuideCommonActions.showMessageBox((Shell) explorerContext.get("shell"), "项目树菜单" + self.getString("menuItem") + "事件" + menuItem[1] + "不存在！");
		}
		itemListener.handleEvent(null);
	}
	
	public static ActionContext getNewThingDialogActionContext(){
		return (ActionContext) World.getInstance().getData("__xworker_ide_newThingDialog__");
	}
	
	/**
	 * 打开新建事物的窗口。
	 * 
	 * @param actionContext
	 */
	public static void openNewThingDialog(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("data");
		
		ActionContext explorerContext = Designer.getExplorerActions().getActionContext();//(ActionContext) actionContext.get("explorerContext");
		ActionContext newThingContext = getNewThingDialogActionContext();
		
		Tree projectTree = (Tree) explorerContext.get("projectTree");
		TreeItem treeItem = projectTree.getSelection()[0];
		
		Shell dialogShell = null;
		if(newThingContext != null){
			dialogShell = (Shell) newThingContext.get("shell");
			if(dialogShell != null && dialogShell.isDisposed()){
				dialogShell = null;
			}
		}
		
		if(dialogShell == null){
			World world = World.getInstance();
			Thing dialogObject = world.getThing("xworker.ide.worldExplorer.swt.dialogs.NewThingDialog/@shell");
			
			Shell parentShell = treeItem.getParent().getShell();

			ActionContext newContext = new ActionContext();
			newContext.put("treeItem", treeItem);
			newContext.put("explorerContxt", explorerContext);
			newContext.put("explorerActions", actionContext.get("explorerActions"));
			newContext.put("categoryPath", ((Index) treeItem.getData()).getPath());
			newContext.put("parent", parentShell);
			
			Shell newShell = (Shell) dialogObject.doAction("create", newContext);

			String descriptorPath = self.doAction("getDescriptorPath", actionContext);//self.getStringBlankAsNull("descriptorPath");
			if(descriptorPath != null){
				((Text) newContext.get("descriptorText")).setText(descriptorPath);
			}
			world.setData("__xworker_ide_newThingDialog__", newContext);
			SwtDialog dialog = new SwtDialog(parentShell, newShell, newContext);
			dialog.open(null);
			//dialog.open();
		}
	}
	
}
