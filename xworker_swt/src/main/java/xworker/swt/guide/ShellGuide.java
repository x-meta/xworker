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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;

public class ShellGuide implements DisposeListener, ControlListener{
	/**
	 * 用于监控的Composite。
	 */
	Composite maskComposite;
	
	/** 用于搜索激活的控件的父容器 */
	Composite activeControlParent;
	
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
	/** 是否正在初始化GuideNode，检测自动结束线程用，避免未展示完就被终止了 */
	boolean initGuideNode = false;
	
	/** 当前激活的控件 */
	Control activeControl;
	
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
		this.actionContext = new ActionContext();
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
//		tipShell.addDisposeListener(new DisposeListener() {
//
//			@Override
//			public void widgetDisposed(DisposeEvent arg0) {
//				System.out.println("TipShell disposed");
//			}
//			
//		});
		
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
	 * @param maskActionContext
	 */
	public synchronized void setMaskComposite(Composite maskComposite, ActionContext maskActionContext) {
		init = true;
		Designer.setVisible(maskComposite);
		this.parentContext = maskActionContext;		
		if(this.maskComposite.getShell() != maskComposite.getShell()) {
			this.doClose();
			this.maskComposite = maskComposite;
			doInit(false);
			
			return;
		}else if(this.maskComposite != maskComposite) {
			this.maskComposite.removeControlListener(this);
			this.maskComposite.removeDisposeListener(this);
			
			this.maskComposite = maskComposite;
			this.maskComposite.addControlListener(this);
			this.maskComposite.addDisposeListener(this);
		}
		
		if(maskComposite instanceof Shell) {
			Shell shell = (Shell) maskComposite;
			maskShell.setLocation(shell.getLocation());
		}else {
			Point location = maskComposite.toDisplay(maskComposite.getLocation());
			maskShell.setLocation(location);
		}
		maskShell.setSize(maskComposite.getSize());
	}
	
	
	public long getNodeStartTime() {
		return nodeStartTime;
	}

	public void setGuideThing(Thing guideThing) {
		this.guideThing = guideThing;
		guideNodes = guideThing.getChilds("ShellGuideNode");
		guideIndex= -1;
	}
	
	public void next() {
		guideNodes = guideThing.getChilds("ShellGuideNode");
		if(guideIndex < guideNodes.size() - 1) {
			guideIndex++;
			
			showCurrentGuide();
		} else {
			close();
		}
	}
	
	public void pre() {
		guideNodes = guideThing.getChilds("ShellGuideNode");
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
			Long delay = doAction(guideNode, "getDelay");
			if(delay == null) {
				delay = 2000l;
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
		//正在初始化节点，不要判断
		if(initGuideNode) {
			return;
		}
		
		Button nextButton = actionContext.getObject("nextButton");
		if(nextButton == null || nextButton.isDisposed()) {
			return;
		}
		
		if(guideIndex >= 0 && guideIndex < guideNodes.size()) {
			Thing guideNode = guideNodes.get(guideIndex);
			if(guideNode.getBoolean("disableNextButton") == false) {
				Boolean finished = doAction(guideNode, "canNext");
				if(finished == null || UtilData.isTrue(finished)) {
					nextButton.setEnabled(true);
				}
			}
			long delay = getDelay();
			if(delay > 0) {
				if(System.currentTimeMillis() - nodeStartTime < delay) {
					//未到延迟时间
					return;
				}
			}						
			
			if(UtilData.isTrue(doAction(guideNode, "finished"))) {
				next();
			}
		}
	}
	
	protected <T> T doAction(Thing guideNode, String name) {
		try {
			datas.put("guide", this);
			datas.put("guideNode", guideNode);
			//datas.put("activeControl", getActiveControl(guideNode));
			return guideNode.doAction(name, parentContext, datas);
		}catch(Exception e) {
			throw new ActionException("Execute node action error, action=" + 
						name + ", node=" + guideNode.getMetadata().getPath(), e);
		}
	}
	
	protected Control getActiveControl(Thing guideNode) {
		Control control = doAction(guideNode, "getActiveControl");
		if(control != null) {
			return control;
		}
		
		Thing controlThing = doAction(guideNode, "getActiveControlThing");
		if(controlThing != null) {
			//获取激活的Control，即可以输入的Control
			Composite controlParent = doAction(guideNode, "getActiveControlParent");
			if(controlParent != null) {
				activeControlParent = controlParent;
			}else {
				controlParent = activeControlParent;
				
				if(controlParent == null) {
					controlParent = maskComposite;
				}
			}
			String activeControlClassName = doAction(guideNode, "getClassName");
			Boolean isAttribute = doAction(guideNode, "isAttribute");
			if(isAttribute == null) {
				isAttribute = false;
			}
			if(activeControlClassName != null) {
				control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute, activeControlClassName);
			} else {
				control = Designer.getControl(controlParent, controlThing.getMetadata().getPath(), isAttribute);
			}
			
		}
		
		return control;
	}
	
	public synchronized void showCurrentGuide() {
		if(isDisposed()) {
			return;
		}
		
		try {
			initGuideNode = true;
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
				Composite maskCompoiste = doAction(guideNode, "getMaskComposite");
				if(maskCompoiste != null && maskCompoiste != this.maskComposite && maskComposite.isDisposed() == false) {
					ActionContext maskActionContext = doAction(guideNode, "getMaskCompositeActionContext");
					if(maskActionContext == null) {
						maskActionContext = Designer.getActionContext(maskCompoiste);
					}
					this.setMaskComposite(maskCompoiste, maskActionContext);
				}
				
				//执行初始化方法
				doAction(guideNode, "init");
				
				maskComposite.getDisplay().asyncExec(new Runnable() {
					public void run() {
						//放到display中执行，解决init方法中aysncExec后执行的问题
						try {
							//tooltip
							
							Boolean tipVisible = guideNode.doAction("isTipVisible", actionContext);
							if(tipVisible == null || tipVisible == true) {
								//tipShell再设置了网页后在网页ready事件里被打开，见xworker.swt.guide.prototypes.ShellGuideTipShell/@browser/@BrowserFunction/@actions/@doFunction
								Shell tipShell = getTipShell();
								tipShell.setSize(640,480);
								String url = Designer.getUrlRoot() 
										+ "do?sc=xworker.swt.guide.prototypes.ShellGuideTipWeb&thing=" + guideNode.getMetadata().getPath();
								//System.out.println(url);
								((Browser) ShellGuide.this.actionContext.get("browser")).setUrl(url);
							}
													
							activeControl = getActiveControl(guideNode);
							ShellGuide.this.setData("activeControl", activeControl);
							actionContext.g().put("control", activeControl);
							if(activeControl != null) {
								//使控件可见
								Designer.setVisible(activeControl);
								
								Point controlLocation  = null;
								if(maskComposite instanceof Shell) {
									//Shell shell = (Shell) maskComposite;
									//Point location = control.getLocation();
									//controlLocation = control.getParent().toDisplay(location);
									//Rectangle rec = shell.getClientArea();
									controlLocation = activeControl.getDisplay().map(activeControl, maskShell, new Point(0, 0));
								}else {
									controlLocation = activeControl.getDisplay().map(activeControl, maskComposite, new Point(0, 0));								
								}
								
								Region region = new Region();
								region.add(0, 0, maskShell.getSize().x, maskShell.getSize().y);
								Point controlSize = activeControl.getSize();
								region.subtract(controlLocation.x, controlLocation.y, controlSize.x, controlSize.y);
								maskShell.setRegion(region);
								
								Designer.attachTo(tipShell, activeControl);
							}else{
								maskShell.setRegion(null);
								Point loc = maskShell.getLocation();
								loc.x = loc.x + (maskShell.getSize().x - tipShell.getSize().x) / 2;
								loc.y = loc.y + (maskShell.getSize().y - tipShell.getSize().y) / 2;
								tipShell.setLocation(loc);
							}
							
							maskShell.setFocus();
							tipShell.setFocus();
							
							doAction(guideNode, "afterInited");
						}catch(Exception e) {
							e.printStackTrace();
						}finally {
							initGuideNode = false;
						}
					}
				});			
			}else {
				initGuideNode = false;
			}
		}catch(Exception e) {
			initGuideNode = false;
			Executor.error(ShellGuide.class.getSimpleName(), "Show current node error, close guide", e);
			close();
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
		
		if(maskComposite != null && maskComposite.isDisposed() == false) {
			maskComposite.removeDisposeListener(this);
			maskComposite.removeControlListener(this);
			maskComposite.getShell().removeControlListener(this);
		}
		
		if(maskShell != null && maskShell.isDisposed() == false) {
			maskShell.dispose();
		}
		
		if(tipShell != null && tipShell.isDisposed() == false) {
			tipShell.dispose();
		}
	}
	
	public void close() {
		if(maskShell != null && maskShell.isDisposed() == false) {
			final Control control = maskComposite;
			maskShell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						if(maskShell.isDisposed() || control != ShellGuide.this.maskComposite) {
							//System.out.println("already closed!");
						}else {
							doClose();
						}
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
		ActionContext ac = null;
		if(maskComposite == null) {
			Thing shellThing = self.doAction("getShell", actionContext);
			if(shellThing != null) {
				ac = new ActionContext();
				ac.put("parentContext", actionContext);
				ac.put("parent", Display.getCurrent().getActiveShell());
				Shell shell = shellThing.doAction("create", ac);
				maskComposite = shell;
				shell.setVisible(true);
			}
		}
		
		if(maskComposite == null) {
			maskComposite = actionContext.getObject("parent");
		}
		if(maskComposite == null) {
			if(maskComposite == null) {
				throw new ActionException("Can not create ShellGuide, maskComposite is null, thing=" + self.getMetadata().getPath());
			}
		}
		
		if(ac == null) {
			ac = self.doAction("getActionContext", actionContext);
		}
		if(ac == null) {
			ac = Designer.getActionContext(maskComposite);
		}
		if(ac == null) {
			ac = actionContext;
		}
		ShellGuide guide = new ShellGuide(maskComposite, self, ac);
		actionContext.g().put(self.getMetadata().getName(), guide);
		return guide;
	}

	@Override
	public void controlMoved(ControlEvent e) {		
		if(maskComposite != null && maskComposite.isDisposed() == false && e.widget == maskComposite.getShell()) {
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

	public Control getActiveControl() {
		return activeControl;
	}
}
