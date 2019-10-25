package xworker.ide.tools.autodemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.custom.StyledTextProxy;
import xworker.swt.events.SwtListener;
import xworker.util.GlobalConfig;
import xworker.util.XWorkerUtils;

public class AutoDemo implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(AutoDemo.class);
	
	public static void menu_run(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("currentThing");
		Thing shellThing = World.getInstance().getThing("xworker.ide.tools.autodemo.AutoDemoShell");
		ActionContext ac = new ActionContext();
		Shell shell = (Shell) shellThing.doAction("create", ac);
		shell.setText(thing.getMetadata().getLabel());
		shell.setVisible(true);
		
		AutoDemo demo = new AutoDemo(thing, shell, ac);
		ac.getScope(0).put("demo", demo);
		
		//自动演示
		((Button) ac.get("autoButton")).setSelection(true);
		((SwtListener) ac.get("autoSelection")).handleEvent(null);
	}	
	
	Composite composite;
	Thing thing;
	ActionContext actionContext;
	boolean auto = false;
	Shell shell;
	int index = 0;
	/** 子自动演示 */
	AutoDemo subDemo;
	AutoDemo parent;
	/** 播放速度 */
	float speed = 1;
	
	public AutoDemo(Thing thing, Composite composite, ActionContext actionContext){
		this.actionContext =actionContext;
		this.thing = thing;
		this.composite = composite;			
		this.shell = composite.getShell();
		
		String referencePath = thing.getStringBlankAsNull("referencePath");
		if(referencePath != null){
			Thing refThing = World.getInstance().getThing(referencePath);
			if(refThing != null){
				//创建一个子演示
				new AutoDemo(this, shell, refThing);
			}
		}
		//this.showHtml(XWorkerUtils.getThingDescUrl(thing), false);
	}
	
	public AutoDemo(AutoDemo parent, Thing thing){
		this(parent, parent.shell, thing);
	}
	
	public AutoDemo(AutoDemo parent, Shell shell, Thing thing){
		this.actionContext = parent.actionContext;
		this.thing = thing;
		this.shell = shell;
		this.composite = parent.composite;
		this.parent = parent;
		parent.subDemo = this;
		

		String referencePath = thing.getStringBlankAsNull("referencePath");
		if(referencePath != null){
			Thing refThing = World.getInstance().getThing(referencePath);
			if(refThing != null){
				//创建一个子演示
				new AutoDemo(this, shell, refThing);
			}
		}
	}
	
	public void setIndex(int index){
		this.index = index;
		
		if(index == 0){
			subDemo = null;
		}
	}
	
	public boolean hasPre(){
		if(subDemo != null && subDemo.hasPre()){
			return true;
		}else{
			return index > 0;
		}
	}
	
	public boolean hasNext(){
		if(subDemo != null && subDemo.hasNext()){
			return true;
		}else{
			return index < thing.getChilds().size();
		}
	}
	
	public void show(){
		show(false);
	}
	
	public void setSpeed(float speed) {
		if(speed < 0) {
			speed = 1;
		}
		this.speed = speed;
	}
	
	public void show(boolean wait){
		if(subDemo != null && subDemo.hasNext()){
			subDemo.show(wait);
		}else{
			subDemo = null;
			if(index <= thing.getChilds().size() - 1){
				//有下一个
				Thing child = thing.getChilds().get(index);
				if(isSubDemo(child)){
					show(wait);
				}else{					
					runChild(child);
					if(subDemo != null){
						show(wait);
					}
					
					if(wait){
						long time = child.getLong("waitTime");
						if(time > 0){
							try {
								final Label timeLabel = (Label) actionContext.get("timeLabel");
								if(timeLabel != null && !timeLabel.isDisposed()){
									long start = System.currentTimeMillis();
									float sped = 1f;
									AutoDemo parent = this.parent;
									while(parent != null) {
										sped = parent.speed;
										parent = parent.parent;
									}
									while(System.currentTimeMillis() - start < time * sped){
										String sec = String .valueOf(((sped * time * 1d - (System.currentTimeMillis() - start)) / 1000));
										if(sec.getBytes().length > 3){
											sec = sec.substring(0, 3);
										}
										final String second = sec;
										if(timeLabel.isDisposed()){
											break;
										}
										timeLabel.getDisplay().asyncExec(new Runnable(){
											public void run(){
												try{
													timeLabel.setText(second);
												}catch(Exception e){													
												}
											}
										});
										
										Thread.sleep(100);
										//重新获取速度
										parent = this.parent;
										while(parent != null) {
											sped = parent.speed;
											parent = parent.parent;
										}
									}
								}else{
									Thread.sleep(time);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				index++;
			}else{
				//没有下一个，调用父的下一个
				if(parent != null){
					parent.index ++;
					parent.subDemo = null;
					parent.show(wait);
				}
			}
		}
	}
	
	public void showPre(){
		if(subDemo != null && subDemo.hasPre()){
			subDemo.showPre();
		}else{
			subDemo = null;
			if(hasPre()){
				index--;
				
				final Thing child = thing.getChilds().get(index);
				if(isSubDemo(child)){
					showPre();
				}else{
					if(child.getBoolean("isChapter")){
						//显示第一个						
						executeRunnable(new Runnable(){
							public void run(){
								((Browser) actionContext.get("sysBrowser")).setText("");
								StyledTextProxy.setText(actionContext.get("inputTxt"), "");
								showHtml(XWorkerUtils.getThingDescUrl(getThing()), false);
								
								runChild(child);
							}
						});						
					}else{
						showPre();
					}					
				}
			}else{
				if(parent == null){
					//显示第一个
					executeRunnable(new Runnable(){
						public void run(){
							((Browser) actionContext.get("sysBrowser")).setText("");
							StyledTextProxy.setText(actionContext.get("inputTxt"), "");
							showHtml(XWorkerUtils.getThingDescUrl(getThing()), false);
							show();
						}
					});
					
				}else{
					parent.index --;
					parent.subDemo = null;
					parent.showPre();
				}
			}
		}
	}
	
	/**
	 * 从指定的节点开始演示。
	 * 
	 * @param node
	 */
	public void show(Thing node){
		//节点的父子顺序
		List<Thing> paths = new ArrayList<Thing>();
		paths.add(node);
		Thing thing = node;
		Thing parent = thing.getParent();
		while(parent != null && parent != this.thing){
			paths.add(0, parent);
			parent = parent.getParent();
		}
		
		//设置演示节点
		AutoDemo demo = this;
		Thing parentThing = this.getThing();
		for(int i=0; i<paths.size(); i++){
			Thing nodeThing = paths.get(i);
			demo.index = parentThing.getChilds().indexOf(nodeThing);
			demo.subDemo = null;
			
			if(i < paths.size() - 1 && nodeThing.getThingName().equals("AutoDemo")){
				//如果是一个演示节点
				demo = new AutoDemo(demo, shell, nodeThing);
				parentThing = nodeThing;
			}
		}
		
		executeRunnable(new Runnable(){
			public void run(){
				((Browser) actionContext.get("sysBrowser")).setText("");
				StyledTextProxy.setText(actionContext.get("inputTxt"), "");
				showHtml(XWorkerUtils.getThingDescUrl(getThing()), false);
				show();
			}
		});
	}
	
	public boolean isSubDemo(Thing child){
		Object obj = child.doAction("isDemo", actionContext);
		if(obj instanceof Boolean && (Boolean) obj && child.getChilds().size() > 0){
			subDemo = new AutoDemo(this, shell, child);
			subDemo.parent = this;
			return true;
		}else{
			return false;
		}
	}

	public void setAuto(boolean auto){
		this.auto = auto;
		
		if(auto){
			new Thread(this).start();
		}
	}
	
	public void editThing(final Thing ething){
		executeRunnable(new Runnable(){
			public void run(){
				ActionContext thingEditor = (ActionContext) actionContext.get("thingEditor");
				ActionContainer ac = (ActionContainer) thingEditor.get("editorActions");
				ac.doAction("setThing", (ActionContext) thingEditor.get("thingContext"), UtilMap.toMap("thing", ething.getRoot()));				
				ac.doAction("openThing", (ActionContext) thingEditor.get("thingContext"), 
						UtilMap.toMap("thing", ething.getMetadata().getPath()));
				layout("editorComposite");				
			}
		});
	}
	
	public void showComposite(final String path){
		final AutoDemo demo = this;
		executeRunnable(new Runnable(){
			public void run(){
				Composite contentComposite = (Composite) actionContext.get("contentComposite");
				for(Control control : contentComposite.getChildren()){
					control.dispose();
				}
				
				ActionContext ac = new ActionContext();
				ac.put("parent", contentComposite);
				ac.put("demo", demo);
				ac.put("parentContext", actionContext);
				Thing thing = World.getInstance().getThing(path);
				if(thing != null){
					thing.doAction("create", ac);
					contentComposite.layout();
					
					actionContext.getScope(0).put("contentActionContext", ac);
				}
				
				layout("contentComposite");
			}
		});
	}
	
	public void showComposite(final String path, final Map<String, Object> params){
		final AutoDemo demo = this;
		executeRunnable(new Runnable(){
			public void run(){
				Composite contentComposite = (Composite) actionContext.get("contentComposite");
				for(Control control : contentComposite.getChildren()){
					control.dispose();
				}
				
				ActionContext ac = new ActionContext();
				if(params != null){
					ac.putAll(params);
				}
				ac.put("parent", contentComposite);
				ac.put("demo", demo);
				ac.put("parentContext", actionContext);
				Thing thing = World.getInstance().getThing(path);
				if(thing != null){
					thing.doAction("create", ac);
					contentComposite.layout();
					
					actionContext.getScope(0).put("contentActionContext", ac);
				}
				
				layout("contentComposite");
			}
		});
	}
	
	public void showCode(final String codeType, final String code){
		executeRunnable(new Runnable(){
			public void run(){
				Composite contentComposite = (Composite) actionContext.get("contentComposite");
				for(Control control : contentComposite.getChildren()){
					control.dispose();
				}
				
				Thing txt = new Thing("xworker.swt.Widgets/@StyledText");
				txt.put("name", "text");
				txt.put("style", "MULTI");
				txt.put("H_SCROLL", "true");
				txt.put("V_SCROLL", "true");

				Thing colorer = new Thing("xworker.swt.custom.StyledText/@Colorer");
				colorer.put("name", "colorer");
				colorer.put("codeName", codeType);
				colorer.put("codeType", codeType);
				txt.addChild(colorer);
				
				ActionContext ac = new ActionContext();
				ac.put("parent", contentComposite);
				txt.doAction("create", ac);
				contentComposite.layout();
				
				actionContext.getScope(0).put("contentActionContext", ac);
				
				if(code!= null){
					StyledTextProxy.setText(actionContext.get("inputTxt"), code);					
				}
				
				layout("contentComposite");
			}
		});
		
	}
	
	public void showThingDescription(String thingPath){
		showHtml(XWorkerUtils.getThingDescUrl(thingPath), false);
	}
	
	public void showHtml(final String url, final boolean isWebControl){
		executeRunnable(new Runnable(){
			public void run(){
				Browser browser = (Browser) actionContext.get("browser");
				if(isWebControl){
					browser.setUrl(GlobalConfig.getWebUrl() + "do?sc=" + url);
				}else{
					browser.setUrl(url);
				}

				layout("browserComposite");
			}
		});
	}
	
	public void showXml(final String xml){
		executeRunnable(new Runnable(){
			public void run(){
				StyledTextProxy.setText(actionContext.get("xmlText"), xml);					
				
				layout("xmlComposite");
			}
		});
	}
	
	public void layout(String name){
		StackLayout stackLayout = (StackLayout) actionContext.get("stackLayout");
		Composite mainComposite = (Composite) actionContext.get("mainComposite");
		Composite content = (Composite) actionContext.get(name);
		stackLayout.topControl = content;
		mainComposite.layout();
	}
	
	public void sysmResponse(final String thingPath){
		executeRunnable(new Runnable(){
			public void run(){
				Browser sysBrowser = (Browser) actionContext.get("sysBrowser");
				if(!sysBrowser.isDisposed()){
					sysBrowser.setUrl(XWorkerUtils.getThingDescUrl(thingPath) + "&fade=true");
				}
			}
		});
	}
	
	public void inputText(String text){
		clearInputText();
		
		if(text != null){
			for(char c : text.toCharArray()){
				appendText(new String(new char[]{c}));
				sleep(100);
			}
		}
	}
	
	public void clearInputText(){
		executeRunnable(new Runnable(){
			public void run(){
				Object txt = getInputText();
				StyledTextProxy.setText(txt, "");
			}
		});
	}
	
	public void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void appendText(final String text){
		executeRunnable(new Runnable(){
			public void run(){
				Object txt = getInputText();
				if(!StyledTextProxy.isDisposed(txt)){
					StyledTextProxy.append(txt, text);
					StyledTextProxy.setCaretOffset(txt, StyledTextProxy.getText(txt).length());					
				}
			}
		});
	}
	
	public Object getInputText(){
		return actionContext.get("inputTxt");
	}
	
	public Object getSysText(){
		return actionContext.get("sysText");
	}
	
	public void executeRunnable(Runnable run){		
		if(composite.isDisposed()){ 
			//窗口已关闭，结束
			auto = false;
			return;
		}
				
		shell.getDisplay().asyncExec(run);
	}
	
	public void run(){
		while(hasNext()){
			if(composite.isDisposed()){
				break;
			}
			
			if(auto == false){
				break;
			}else{
				show(true);
			}
		}
		
		executeRunnable(new Runnable(){
			public void run(){
				((Button) actionContext.get("autoButton")).setSelection(false);
				((Button) actionContext.get("preButton")).setEnabled(true);
				((Button) actionContext.get("nextButton")).setEnabled(true);
			}
		});
		
	}
	
	public void runChild(Thing child){
		try{
			if(child.getStringBlankAsNull("description") != null && !"ShowThingDescription".equals(child.getThingName())){
				this.sysmResponse(child.getMetadata().getPath());
			}
			child.doAction("run", actionContext, UtilMap.toMap("demo", this));
		}catch(Exception e){
			logger.error("Excuete demo child error, path=" + child.getMetadata().getPath(), e);
		}
	}

	public Composite getComposite() {
		return composite;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public boolean isAuto() {
		return auto;
	}

	public int getIndex() {
		return index;
	}

	public void go(int step){
		if(subDemo != null){
			subDemo.go(step);
		}else{
			index = index + step;
			if(index < 0){
				index = 0;
			}
		}
	}
	
	public AutoDemo getSubDemo() {
		return subDemo;
	}
	
}
