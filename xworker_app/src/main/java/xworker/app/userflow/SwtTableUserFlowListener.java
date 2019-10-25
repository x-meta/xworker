package xworker.app.userflow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;

import xworker.dataObject.DataObject;

public class SwtTableUserFlowListener implements UserFlowListener, DisposeListener{
	Table table;
	
	public SwtTableUserFlowListener(Table table){
		this.table = table;
		
		this.table.addDisposeListener(this);
		
		UserFlowManager.addListener(this);
	}
	
	@Override
	public void started(final DataObject task) {
		if(!table.isDisposed()){
			table.getDisplay().asyncExec(new Runnable(){
				public void run(){
					try{
						boolean have = false;
						for(TableItem item : table.getItems()){
							DataObject data = (DataObject) item.getData();
							if(data.getLong("id") == task.getLong("id")){
								have = true;
								break;
							}
						}
						
						if(!have){
							TableItem item = new TableItem(table, SWT.None);
							item.setData(task);
							item.setText(new String[]{
									task.getString("id"),
									task.getString("label"),
									task.getString("thingPath"),
									task.getString("status"),
									"执行中",
									task.getString("daily"),
									task.getString("finishedCount"),
									task.getString("lastStartTime"),
									task.getString("lastFinishedTime"),
							});					
						}
					}catch(Exception e){						
					}
				}
			});
		}
	}

	@Override
	public void finished(final DataObject task) {
		if(!table.isDisposed()){
			table.getDisplay().asyncExec(new Runnable(){
				public void run(){
					try{
						ActionContext ac = (ActionContext) table.getData("actionContext");
						if(ac != null){
							((Button) ac.get("executeButton")).setEnabled(false);
							((Button) ac.get("skipButton")).setEnabled(false);
							((Button) ac.get("dailyButton")).setEnabled(false);
							((Button) ac.get("openThingButton")).setEnabled(false);			
							((Browser) ac.get("browser")).setText("");
						}
						
						for(TableItem item : table.getItems()){
							DataObject data = (DataObject) item.getData();
							if(data.getLong("id") == task.getLong("id")){
								item.dispose();
								break;
							}
						}	
							
						//自动检查任务，以便启动后续任务
						UserFlowActions.autoCheckTasks(ac);
					}catch(Exception e){						
					}
				}
			});
		}
	}

	@Override
	public void widgetDisposed(DisposeEvent arg0) {
		UserFlowManager.removeListener(this);
	}
}
