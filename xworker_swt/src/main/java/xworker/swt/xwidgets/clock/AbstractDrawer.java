package xworker.swt.xwidgets.clock;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class AbstractDrawer {
	Boolean showSecond;
	Boolean showMinute;
	Boolean showHour;
	Boolean showArea;
	Boolean showAmPm = null;
	Thing thing;
	ActionContext actionContext;
	
	public AbstractDrawer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public Boolean getShowSecond() {
		return showSecond;
	}

	public void setShowSecond(Boolean showSecond) {
		this.showSecond = showSecond;
	}

	public Boolean getShowMinute() {
		return showMinute;
	}

	public void setShowMinute(Boolean showMinute) {
		this.showMinute = showMinute;
	}

	public Boolean getShowHour() {
		return showHour;
	}

	public void setShowHour(Boolean showHour) {
		this.showHour = showHour;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public boolean isShowSecond() {
		if(showSecond != null) {
			return showSecond;
		}else {
			return UtilData.isTrue(thing.doAction("isShowSecond", actionContext));
		}
	}
	
	public boolean isShowMinute() {
		if(showMinute != null) {
			return showMinute;
		}else {
			return UtilData.isTrue(thing.doAction("isShowMinute", actionContext));
		}
	}
	
	public boolean isShowHour() {
		if(showHour != null) {
			return showHour;
		}else {
			return UtilData.isTrue(thing.doAction("isShowHour", actionContext));
		}
	}

	public boolean isShowArea() {
		if(showArea != null) {
			return showArea;
		}else {
			return UtilData.isTrue(thing.doAction("isShowArea", actionContext));
		}
	}

	
	public Boolean getShowArea() {
		return showArea;
	}

	public void setShowArea(Boolean showArea) {
		this.showArea = showArea;
	}
	
	
	public Boolean getShowAmPm() {
		return showAmPm;
	}

	public void setShowAmPm(Boolean showAmPm) {
		this.showAmPm = showAmPm;
	}

	public boolean isShowAmPm() {
		if(showAmPm != null) {
			return showAmPm;
		}else {
			return UtilData.isTrue(thing.doAction("isShowAmPm", actionContext));
		}
	}
}
