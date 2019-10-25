package xworker.swt.custom.tableEditors;

import org.eclipse.swt.widgets.ProgressBar;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

import xworker.util.UtilData;

@ActionClass(creator="createInstance")
public class ProgressBarEditor extends AbstractTableEditor{
	ProgressBar progressBar;
	
	@Override
	public Object create(ActionContext actionContext) {
		Thing progressBarThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.ProgressBarEditor/@progressBar").detach();

		progressBarThing.set("INDETERMINATE", self.get("INDETERMINATE"));
		progressBarThing.set("maximum", self.get("maximum"));
		progressBarThing.set("minimum", self.get("minimum"));
		progressBar = progressBarThing.doAction("create", actionContext);
		
		return progressBar;
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		try {
			int selection = UtilData.getInt(value, 0);
			progressBar.setSelection(selection);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		return progressBar.getSelection();
	}

	@Override
	public void doDispose() {
		progressBar.dispose();
	}

	public static ProgressBarEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(ProgressBarEditor.class, actionContext);
	}

}
