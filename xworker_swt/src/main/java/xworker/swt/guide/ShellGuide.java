package xworker.swt.guide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	Composite maskComposite;
	
	/** 用于产生遮蔽效果的Shell */
	Shell maskShell;
	
	/** 用于显示提示消息的Shell */
	Shell tipShell;

	Thing guideThing;
	ActionContext actionContext;
	ActionContext parentContext;
	
	List<Thing> guideNodes = new ArrayList<Thing>();
	int guideIndex = -1;
	long nodeStartTime = -1;
	Map<String, Object> datas = new HashMap<String, Object>();
	
	ShellGuideThread checker = null;
	
	boolean init = true;
	
	public  ShellGuide(Composite maskComposite, Thing guideThing, ActionContext parentContext) {
		this.maskComposite = maskComposite;
		this.guideThing = guideThing;
		this.parentContext = parentContext;
				
		init(true);
	}
	
	public void init(final boolean first) {
		maskComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					doInit(first);
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					init = false;
				}
			}
		});
	}
	
	private void doInit(boolean first) {
		if(this.actionContext == null) {
			this.actionContext = new ActionContext();
		}
		actionContext.put("parentContext", parentContext);
		if(maskComposite instanceof Shell) {
			actionContext.put("parent", maskComposite);
		}else {
			actionContext.put("parent", maskComposite.getShell());
		}
		actionContext.put("guide", this);
		
		maskComposite.addDisposeListener(this);		
		
		World world = World.getInstance();
		//创建遮罩的Shell
		Thing maskShellThing = world.getThing("xworker.swt.guide.prototypes.ShellGuideShell");
		maskShell = maskShellThing.doAction("create", actionContext);
		
		
		Thing tipShellThing = world.getThing("xworker.swt.guide.prototypes.ShellGuideTipShell");
		tipShell = tipShellThing.doAction("create", actionContext, "parent", maskShell);	
		
		//启动检测线程
		checker = new ShellGuideThread(this);
		new Thread(checker).start();
		maskShell.setVisible(true);
		tipShell.setVisible(true);
		tipShell.setText(guideThing.getMetadata().getLabel());
		tipShell.setFocus();
		maskComposite.getShell().addControlListener(this);
		maskComposite.addControlListener(this);
		
		if(first) {
			//第一次启动时的初始化
			setGuideThing(guideThing);
			
			next();
		}else {
			tipShell.setText(guideThing.getMetadata().getLabel());
		}
		
		if(maskComposite instanceof Shell) {
			Shell shell = (Shell) maskComposite;
			maskShell.setLocation(shell.getLocation());
		}else {
			Point location = maskComposite.toDisplay(maskComposite.getLocation());
			maskShell.setLocation(location);
		}
		maskShell.setSize(maskComposite.getSize());
		init = false;
	}
	
	public void setData(String key, Object value) {
		datas.put(key, value);
	}
	
	public Object getData(String key) {
		return datas.get(key);
	}
	
	/**
	 * 切换到新的Composite上，即遮罩到新的Composite上。
	 * 
	 * @param maskComposite
	 * @param actionContext
	 */
	public synchronized void setMaskComposite(Composite maskComposite, ActionContext maskActionContext) {
		init = true;
		this.parentContext = maskActionContext;		
		if(this.maskComposite.getShell() != maskComposite.getShell()) {
			this.doClose();
			this.maskComposite = maskComposite;
			this.init(false);
			
			return;
		}else if(this.maskComposite != maskComposite) {
			this.maskComposite.removeControlListener(this);
			this.maskComposite.removeDisposeListener(this);
			
			this.maskComposite = maskComposite;
			this.maskComposite.addControlListener(this);
			this.maskComposite.addDisposeListener(this);
		}
		
		Point location = maskComposite.toDisplay(maskComposite.getLocation());
		maskShell.setLocation(location);
		maskShell.setSize(maskComposite.getSize());
	}
	
	
	public long getNodeStartTime() {
		return nodeStartTime;
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
	
	public Thing getCurrentNode() {
		if(guideNodes != null) {
			return guideNodes.get(guideIndex);
		}
		
		return null;
	}
	
	public long getDelay() {
		Thing guideNode = getCurrentNode();
		if(guideNode != null) {
			Long delay = guideNode.doAction("getDelay", parentContext,  "guide", this, "guideNode", guideNode);
			if(delay == null) {
				delay = 5000l;
			}
			
			return delay;
		}
		
		return -1;
	}
	/**
	 * 检查当前节点是否已经完成了。
	 * 
	 */
	public synchronized void checkCurrentNode() {		
		Button nextButton = actionContext.getObject("nextButton");
		if(nextButton == null || nextButton.isDisposed()) {
			return;
		}
		
		if(guideIndex >= 0 && guideIndex < guideNodes.size()) {
			Thing guideNode = guideNodes.get(guideIndex);
			Boolean finished = guideNode.doAction("canNext", parentContext);
			if(finished == null || UtilData.isTrue(finished)) {
				nextButton.setEnabled(true);
			}
			long delay = getDelay();
			if(delay > 0) {
				if(System.currentTimeMillis() - nodeStartTime < delay) {
					//未到延迟时间
					return;
				}
			}						
			
			if(UtilData.isTrue(guideNode.doAction("finished", parentContext))) {
				next();
			}
		}
	}
	
	protected Control getActiveControl(Thing guideNode) {
		Control control = guideNode.doAction("getActiveControl", parentContext,  "guide", this, "guideNode", guideNode);
		if(control != null) {
			return control;
		}
		
		Thing controlThing = guideNode.doAction("getActiveControlThing", parentContext,  "guide", this, "guideNode", guideNode);
		if(controlThing != null) {
			//获取激活的Control，即可以输入的Control
			Control controlParent = guideNode.doAction("getActiveControlParent", parentContext,  "guide", this, "guideNode", guideNode);
			if(controlParent == null) {
				controlParent = maskComposite;
			}
			String activeControlClassName = guideNode.doAction("getClassName", parentContext,  "guide", this, "guideNode", guideNode);
			boolean isAttribute = guideNode.doAction("isAttribute", parentContext,  "guide", this, "guideNode", guideNode);
			if(activeControlClassName != null) {
				control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute, activeControlClassName);
			} else {
				control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute);
			}
			
		}
		
		return control;
	}
	
	public void showCurrentGuide() {
		if(isDisposed()) {
			return;
		}
		
		Designer.setVisible(maskComposite);
		tipShell.setVisible(false);
		nodeStartTime = System.currentTimeMillis();
		if(maskComposite instanceof Shell) {
			Shell shell = (Shell) maskComposite;
			maskShell.setLocation(shell.getLocation());
			maskShell.setSize(maskComposite.getSize());
		}else {
			Point location = maskComposite.toDisplay(maskComposite.getLocation());		
			maskShell.setLocation(location);
			maskShell.setSize(maskComposite.getSize());
		}
		maskShell.setVisible(true);
		
		Button preButton = actionContext.getObject("preButton");
		Button nextButton = actionContext.getObject("nextButton");
		if(guideIndex <= 0 || guideNodes.size() <= 0) {			
			preButton.setEnabled(false);
		}else {
			Thing guideNode = guideNodes.get(guideIndex);
			if(guideNode.getBoolean("disablePreButton")) {
				preButton.setEnabled(false);
			}else {
				preButton.setEnabled(true);
			}
		}
		if(guideIndex == guideNodes.size() - 1) {
			nextButton.setText(UtilString.getString("lang:d=结束&en=End", actionContext));
		} else {
			nextButton.setText(UtilString.getString("lang:d=下一步&en=Next", actionContext));
		}
		nextButton.setEnabled(false);
		
		if(guideIndex < guideNodes.size()) {
			Thing guideNode = guideNodes.get(guideIndex);
			//是否设置新的遮罩
			Composite maskCompoiste = guideNode.doAction("getMaskComposite", parentContext,"guide", this, "guideNode", guideNode);
			if(maskCompoiste != null && maskCompoiste != this.maskComposite) {
				ActionContext maskActionContext = guideNode.doAction("getMaskCompositeActionContext", parentContext,"guide", this, "guideNode", guideNode);
				if(maskActionContext == null) {
					maskActionContext = Designer.getActionContext(maskCompoiste);
				}
				this.setMaskComposite(maskCompoiste, maskActionContext);
			}
			
			//执行初始化方法
			guideNode.doAction("init", parentContext, "guide", this, "guideNode", guideNode);
			
			maskComposite.getDisplay().asyncExec(new Runnable() {
				public void run() {
					//放到display中执行，解决init方法中aysncExec后执行的问题
					try {
						//tooltip
						
						tipShell.setSize(640,480);
						((Browser) ShellGuide.this.actionContext.get("browser")).setUrl(Designer.getUrlRoot() 
								+ "do?sc=xworker.swt.xworker.design.MarkTooltipControl&thing=" + guideNode.getMetadata().getPath());
												
						Control control = getActiveControl(guideNode);
						actionContext.g().put("control", control);
						if(control != null) {
							//使控件可见
							Designer.setVisible(control);
							
							Point controlLocation  = null;
							if(maskComposite instanceof Shell) {
								//Shell shell = (Shell) maskComposite;
								//Point location = control.getLocation();
								//controlLocation = control.getParent().toDisplay(location);
								//Rectangle rec = shell.getClientArea();
								controlLocation = control.getDisplay().map(control, maskShell, new Point(0, 0));
							}else {
								controlLocation = control.getDisplay().map(control, maskComposite, new Point(0, 0));								
							}
							
							Region region = new Region();
							region.add(0, 0, maskShell.getSize().x, maskShell.getSize().y);
							Point controlSize = control.getSize();
							region.subtract(controlLocation.x, controlLocation.y, controlSize.x, controlSize.y);
							maskShell.setRegion(region);
							
							Designer.attachTo(tipShell, control);
						}else{
							maskShell.setRegion(null);
							Point loc = maskShell.getLocation();
							loc.x = loc.x + (maskShell.getSize().x - tipShell.getSize().x) / 2;
							loc.y = loc.y + (maskShell.getSize().y - tipShell.getSize().y) / 2;
							tipShell.setLocation(loc);
						}
						
						maskShell.setFocus();
						tipShell.setFocus();
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} 
	}

	public synchronized boolean isDisposed() {
		if(init) {
			//还在初始化
			return false;
		}
		
		return maskShell == null || maskShell.isDisposed();
	}
	
	private void doClose() {
		checker.stop();
		
		maskComposite.removeDisposeListener(this);
		maskComposite.removeControlListener(this);
		maskComposite.getShell().removeControlListener(this);
		
		if(maskShell != null && maskShell.isDisposed() == false) {
			maskShell.dispose();
		}
		
		if(tipShell != null && tipShell.isDisposed() == false) {
			tipShell.dispose();
		}
	}
	
	public void close() {
		if(maskComposite != null && maskComposite.isDisposed() == false) {
			maskComposite.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						doClose();
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		close();
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Composite maskComposite = self.doAction("getMaskComposite", actionContext);
		if(maskComposite == null) {
			maskComposite = actionContext.getObject("parent");
		}
		ActionContext ac = self.doAction("getActionContext", actionContext);
		if(ac == null) {
			ac = actionContext;
		}
		ShellGuide guide = new ShellGuide(maskComposite, self, ac);
		actionContext.g().put(self.getMetadata().getName(), guide);
		return guide;
	}

	@Override
	public void controlMoved(ControlEvent e) {
		if(e.widget == maskComposite.getShell()) {
			Point location = maskComposite.toDisplay(maskComposite.getLocation());
			maskShell.setLocation(location);
			maskShell.setSize(maskComposite.getSize());
			
			showCurrentGuide();
		}
	}

	@Override
	public void controlResized(ControlEvent e) {
		Point location = maskComposite.toDisplay(maskComposite.getLocation());
		maskShell.setLocation(location);
		maskShell.setSize(maskComposite.getSize());
		
		showCurrentGuide();
	}

	public Shell getMaskShell() {
		return maskShell;
	}

	public Shell getTipShell() {
		return tipShell;
	}

	public Thing getGuideThing() {
		return guideThing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public ActionContext getParentContext() {
		return parentContext;
	}
	
	
}
