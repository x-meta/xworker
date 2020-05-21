package xworker.swt.guide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class ShellGuide implements DisposeListener, ControlListener{
	/**
	 * 用于监控的Composite。
	 */
	Composite parent;
	
	/** 用于产生遮蔽效果的Shell */
	Shell maskShell;
	
	/** 用于显示提示消息的Shell */
	Shell tipShell;

	Thing guideThing;
	ActionContext actionContext;
	ActionContext parentContext;
	
	List<Thing> guideNodes = new ArrayList<Thing>();
	int guideIndex = -1;
	
	public  ShellGuide(Composite parent, Thing guideThing, ActionContext parentContext) {
		this.parent = parent;
		this.guideThing = guideThing;
		this.parentContext = parentContext;
		this.actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
		actionContext.put("parent", parent.getShell());
		actionContext.put("guide", this);
		
		parent.addDisposeListener(this);		
		
		World world = World.getInstance();
		//创建遮罩的Shell
		Thing maskShellThing = world.getThing("xworker.swt.guide.prototypes.ShellGuideShell");
		maskShell = maskShellThing.doAction("create", actionContext);
		
		
		Thing tipShellThing = world.getThing("xworker.swt.guide.prototypes.ShellGuideTipShell");
		tipShell = tipShellThing.doAction("create", actionContext, "parent", maskShell);	
		
		//启动检测线程
		//new Thread(new ShellGuideThread(this)).start();
		maskShell.setVisible(true);
		tipShell.setVisible(true);
		tipShell.setText(guideThing.getMetadata().getLabel());
		parent.getShell().addControlListener(this);
		parent.addControlListener(this);
		
		setGuideThing(guideThing);
		next();
	}
	
	public void setGuideThing(Thing guideThing) {
		this.guideThing = guideThing;
		guideNodes = guideThing.getChilds("Guide");
		guideIndex= -1;
	}
	
	public void next() {
		guideNodes = guideThing.getChilds("Guide");
		if(guideIndex < guideNodes.size() - 1) {
			guideIndex++;
			
			showCurrentGuide();
		} else {
			close();
		}
	}
	
	public void pre() {
		guideNodes = guideThing.getChilds("Guide");
		if(guideIndex > 0) {
			guideIndex --;
			
			showCurrentGuide();
		}
	}
	
	/**
	 * 检查当前节点是否已经完成了。
	 * 
	 */
	public void checkCurrentNode() {
		if(guideIndex >= 0 && guideIndex < guideNodes.size()) {
			Thing guideNode = guideNodes.get(guideIndex);
			if(UtilData.isTrue(guideNode.doAction("finished", parentContext))) {
				next();
			}
		}
	}
	
	public void showCurrentGuide() {
		if(isDisposed()) {
			return;
		}
		
		Point location = parent.toDisplay(parent.getLocation());
		maskShell.setLocation(location);
		maskShell.setSize(parent.getSize());
		maskShell.setVisible(true);
		
		Button preButton = actionContext.getObject("preButton");
		Button nextButton = actionContext.getObject("nextButton");
		if(guideIndex <= 0 || guideNodes.size() <= 0) {
			preButton.setEnabled(false);
		}else {
			preButton.setEnabled(true);
		}
		if(guideIndex == guideNodes.size() - 1) {
			nextButton.setText(UtilString.getString("lang:d=结束&en=End", actionContext));
		} else {
			nextButton.setText(UtilString.getString("lang:d=下一步&en=Next", actionContext));
		}
		
		if(guideIndex < guideNodes.size()) {
			Thing guideNode = guideNodes.get(guideIndex);
			guideNode.doAction("init", parentContext);
			
			parent.getDisplay().asyncExec(new Runnable() {
				public void run() {
					//放到display中执行，解决init方法中aysncExec后执行的问题
					try {
						//tooltip
						((Browser) actionContext.get("browser")).setUrl(Designer.getUrlRoot() 
								+ "do?sc=xworker.swt.xworker.design.MarkTooltipControl&thing=" + guideNode.getMetadata().getPath());
						Thing controlThing = World.getInstance().getThing(guideNode.getString("activeControlThing"));
						Control control = null;
						if(controlThing != null) {
							//获取激活的Control，即可以输入的Control
							Control controlParent = guideNode.doAction("getActiveControlParent", parentContext);
							if(controlParent == null) {
								controlParent = parent;
							}
							String activeControlClassName = guideNode.getStringBlankAsNull("activeControlClassName");
							boolean isAttribute = guideNode.getBoolean("activeControlIsAttribute");
							if(activeControlClassName != null) {
								control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute, activeControlClassName);
							} else {
								control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute);
							}
							if(control != null) {
								Point controlLocation  = control.getDisplay().map(control, parent, new Point(0, 0));
								
								Region region = new Region();
								region.add(0, 0, maskShell.getSize().x, maskShell.getSize().y);
								Point controlSize = control.getSize();
								region.subtract(controlLocation.x, controlLocation.y, controlSize.x, controlSize.y);
								maskShell.setRegion(region);
								
								Designer.attachTo(tipShell, control);
							}
						}
						if(control == null){
							maskShell.setRegion(null);
							Point loc = maskShell.getLocation();
							loc.x = loc.x + (maskShell.getSize().x - tipShell.getSize().x) / 2;
							loc.y = loc.y + (maskShell.getSize().y - tipShell.getSize().y) / 2;
							tipShell.setLocation(loc);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} 
	}

	public boolean isDisposed() {
		return maskShell == null || maskShell.isDisposed();
	}
	
	public void close() {
		parent.removeDisposeListener(this);
		parent.removeControlListener(this);
		parent.getShell().removeControlListener(this);
		
		if(maskShell != null && maskShell.isDisposed() == false) {
			maskShell.dispose();
		}
		
		if(tipShell != null && tipShell.isDisposed() == false) {
			tipShell.dispose();
		}
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		close();
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite parent = actionContext.getObject("parent");
		ActionContext ac = self.doAction("getActionContext", actionContext);
		if(ac == null) {
			ac = actionContext;
		}
		ShellGuide guide = new ShellGuide(parent, self, ac);
		actionContext.g().put(self.getMetadata().getName(), guide);
		return null;
	}

	@Override
	public void controlMoved(ControlEvent e) {
		if(e.widget == parent.getShell()) {
			Point location = parent.toDisplay(parent.getLocation());
			maskShell.setLocation(location);
			maskShell.setSize(parent.getSize());
			
			showCurrentGuide();
		}
	}

	@Override
	public void controlResized(ControlEvent e) {
		Point location = parent.toDisplay(parent.getLocation());
		maskShell.setLocation(location);
		maskShell.setSize(parent.getSize());
		
		showCurrentGuide();
	}
}
