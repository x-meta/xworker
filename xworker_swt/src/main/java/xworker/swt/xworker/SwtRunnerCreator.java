/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.xworker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.TaskMonitor;
import xworker.swt.util.UtilSwt;

public class SwtRunnerCreator {
	private static final String TAG = SwtRunnerCreator.class.getName();

	public static void run(ActionContext context) {
		Thing shellThing = (Thing) context.get("self");
		
		//初始化
		shellThing.doAction("init", context);

		SwtRunnable swtRun = new SwtRunnable(shellThing, context);
		//用线程启动Max os下会报线程错误，由于SwtRunner通常在系统启动时使用，所以可以不用线程
		//Thread th = new Thread(swtRun);
		//th.start();
		swtRun.run();
	}

	/**
	 * 启动任务执行完毕后执行该方法。
	 * 
	 * @param actionContext
	 */
	public static void onTaskFinished(final ActionContext actionContext) {
		Display display = (Display) actionContext.get("display");
		display.asyncExec(new Runnable(){
			public void run(){
				Thing self = (Thing) actionContext.get("self");

				// 要运行的目标窗口
				Thing appObject = World.getInstance().getThing(
						self.getString("shellThingPath"));
				if (appObject != null) {
					try {
						Shell splashShell = (Shell) actionContext.get("splashShell");

						Bindings bindings = actionContext.push();
						bindings.put("parent", actionContext.get("display"));

						Shell shell = (Shell) appObject.doAction("create",
								actionContext);
						if (splashShell != null) {
							splashShell.dispose();
						}
						shell.open();
						
						try{
							//ActionContext pcontext = actionContext.getObject("parentContext");
							//for(String key : pcontext.keySet()){
							//	System.out.println(key + "=" + pcontext.get(key));
							//}
							self.doAction("onShellOpened", actionContext, UtilMap.toMap("shell", shell));
						}catch(Exception e){
							Executor.warn(TAG, "onShellOpened error", e);
						}
						if(!SwtUtils.isRWT()) {
							while (!shell.isDisposed()) {
								try {
									if (!shell.getDisplay().readAndDispatch())
										shell.getDisplay().sleep();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} finally {
						actionContext.pop();
					}
					
					if(self.getBoolean("exitOnClose")){
						System.exit(0);
					}
				}
			}
		});
		
	}

	static class SwtRunnable implements Runnable {
		ActionContext actionContext;
		Thing self;

		public SwtRunnable(Thing self, ActionContext actionContext) {
			this.self = self;
			this.actionContext = actionContext;
		}

		public void run() {
			try {
				// 新的变量上下文
				ActionContext ac = new ActionContext();
				ac.put("self", self);
				ac.put("parentContext", actionContext);

				// display
				Display dis = Display.getCurrent();
				if(dis == null) {
					dis = new Display();
				}
				final Display display = dis;
				
				ac.put("parent", display);
				ac.put("display", display);

				// 是否有启动窗口
				boolean splash = self.getBoolean("splash");

				// 启动任务
				List<Thing> tasks = new ArrayList<Thing>();
				Thing taskThing = self.getThing("Tasks@0");
				if (taskThing != null) {
					//onTaskFinished(ac);
					tasks.addAll(taskThing.getChilds());
				}
					//onTaskFinished(ac);
					
					
				if (!splash) {
					new TaskMonitor(self, tasks, null, null, ac);
				} else {
					String imageFile = self.getString("splashIcon");

					final Shell shell = new Shell(display,SWT.APPLICATION_MODAL);
					ac.put("parent", shell);
					shell.setSize(UtilSwt.getInt(400), UtilSwt.getInt(300));

					// 布局
					GridLayout shellGridLayout = new GridLayout();
					shellGridLayout.numColumns = 1;
					shellGridLayout.marginWidth = 0;
					shellGridLayout.marginHeight = 0;
					shellGridLayout.horizontalSpacing = 0;
					shellGridLayout.verticalSpacing = 0;
					shell.setLayout(shellGridLayout);

					// 背景图
					Image image = (Image) ResourceManager.createIamge(imageFile, ac);
					if (image != null) {
						shell.setBackgroundImage(image);
					}

					// 设置背景图的模式，使
					shell.setBackgroundMode(SWT.INHERIT_FORCE);
					
					// 设置一个空白的标签站位
					Label blankLabel = new Label(shell, SWT.NONE);
					blankLabel.setLayoutData(new GridData(GridData.FILL_BOTH));

					// 设置标签字符串
					Label label = new Label(shell, SWT.NONE);
					GridData labelGridData = new GridData(GridData.FILL_HORIZONTAL);
					labelGridData.horizontalIndent = 5;
					label.setLayoutData(labelGridData);
					String labeColor = self.getStringBlankAsNull("labelColor");
					if(labeColor != null){
						Color color= (Color) ResourceManager.createColor(label, self.getString("labelColor"), ac);
						label.setForeground(color);
					}

					// 进度条
					int progressBarStyle = SWT.NONE;
					progressBarStyle |= SWT.HORIZONTAL;
					ProgressBar progressBar = new ProgressBar(shell, progressBarStyle);
					progressBar.setMinimum(0);
					GridData progressBarGirdData = new GridData(GridData.FILL_HORIZONTAL);
					progressBar.setLayoutData(progressBarGirdData);

					progressBar.setBounds(10, 10, 200, 32);
					SwtUtils.centerShell(shell);
					shell.open();

					ac.put("splashShell", shell);

					new TaskMonitor(self, tasks, progressBar, label, ac);
					if(SwtUtils.isRWT()) {						
					}else {
						while (!shell.isDisposed()) {
							try {
								if (!shell.getDisplay().readAndDispatch())
									shell.getDisplay().sleep();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				
			} catch (Exception e) {
				Executor.error(TAG, "swtRunner exception", e);
			} catch (Error er){
				Executor.error(TAG, "swtRunner error", er);
			}
		}
	}
}