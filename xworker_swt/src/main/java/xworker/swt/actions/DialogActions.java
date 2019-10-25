package xworker.swt.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.util.DialogCallback;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class DialogActions {
	public static Object openColorDialog(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		final ColorDialog dialog = new ColorDialog(shell);
		if(self.getStringBlankAsNull("text") != null){
			dialog.setText(self.getString("text"));
		}
		if(SwtUtils.isRWT()) {
			SwtUtils.openDialog(dialog, new DialogCallback() {
				@Override
				public void dialogClosed(int returnCode) {
					if(returnCode == SWT.OK) {
						self.doAction("open", actionContext, UtilMap.toMap("color", dialog.getRGB()));
					}else {
						self.doAction("open", actionContext, "color", null);
					}
				}
				
			}, actionContext);
			return null;
		}else {
			RGB rgb = dialog.open();
			self.doAction("open", actionContext, UtilMap.toMap("color", rgb));
			
			return rgb;
		}
	}
	
	public static Object openFontDialog(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		final FontDialog dialog = new FontDialog(shell);
		if(self.getStringBlankAsNull("text") != null){
			dialog.setText(self.getString("text"));
		}
		if(SwtUtils.isRWT()) {
			SwtUtils.openDialog(dialog, new DialogCallback() {
				@Override
				public void dialogClosed(int returnCode) {
					if(returnCode == SWT.OK) {
						FontData[] fds = dialog.getFontList();
						if(fds != null && fds.length > 0) {
							self.doAction("open", actionContext, UtilMap.toMap("font", fds[0]));
						}else {
							self.doAction("open", actionContext, UtilMap.toMap("font", null));
						}
					}else {
						self.doAction("open", actionContext, "color", null);
					}
				}
				
			}, actionContext);
			return null;
		}else {
			FontData data = dialog.open();
			self.doAction("open", actionContext, UtilMap.toMap("font", data));
			
			return data;
		}
	}
	
	public static Object openPrintDialog(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		if(SwtUtils.isRWT()) {
			XWorkerUtils.ideShowMessageBox("PrintDialog", "PrintDialog not supported!", SWT.ICON_WARNING | SWT.OK);
			return null;
		}else {
			Shell shell = (Shell) self.doAction("getShell", actionContext);
			
			PrintDialog dialog = new PrintDialog(shell);
			if(self.getStringBlankAsNull("text") != null){
				dialog.setText(self.getString("text"));
			}
			if(self.getStringBlankAsNull("startPage") != null){
				dialog.setStartPage(self.getInt("startPage"));
			}
			if(self.getStringBlankAsNull("endPage") != null){
				dialog.setEndPage(self.getInt("endPage"));
			}
			if(self.getStringBlankAsNull("printToFile") != null){
				dialog.setPrintToFile(self.getBoolean("printToFile"));
			}
			
			PrinterData pdata = UtilData.getObjectByType(self, "printData", PrinterData.class, actionContext);
			if(pdata != null){
				dialog.setPrinterData(pdata);
			}
			if(self.getStringBlankAsNull("scope") != null){
				dialog.setScope(self.getInt("scope"));
			}
			
			PrinterData data = dialog.open();
			self.doAction("open", actionContext, UtilMap.toMap("printData", data));
			
			return data;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object selectThingFromListDialog(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		Shell shell = (Shell) self.doAction("getShell", actionContext);
		List<Object> list = (List<Object>) self.doAction("getThings", actionContext);
		
		List<Thing> things = new ArrayList<Thing>();
		for(Object obj : list){
			if(obj instanceof Thing){
				things.add((Thing) obj);
			}else if(obj != null){
				String path = String.valueOf(obj);
				Thing thing = World.getInstance().getThing(path);
				if(thing != null){
					things.add(thing);
				}
			}
		}
		
		ActionContext ac = new ActionContext();
		ac.put("parent", shell);
		ac.put("things", things);
		
		Thing dialogThing = World.getInstance().getThing("xworker.swt.actions.prototypes.SelectThingFromListDialog");
		Shell dshell = (Shell) dialogThing.doAction("create", ac);
		
		String title = (String) self.doAction("getTitle", actionContext);
		if(title != null){
			dshell.setText(title);
		}
		
		SwtDialog dialog = new SwtDialog(dshell, ac);
		return dialog.open(new SwtDialogCallback() {

			@Override
			public void disposed(Object result) {
				if(result != null) {
					self.doAction("ok", actionContext, "result", result);
				}else {
					self.doAction("cancel", actionContext);
				}
			}
			
		});
		//return SwtDialog.open(dshell, ac);
	}
}
