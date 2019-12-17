package xworker.swt.xwidgets.dataitems;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.security.EnviromentChecker;
import xworker.security.PathChecker;
import xworker.security.PermissionActionChecker;
import xworker.security.PermissionChecker;
import xworker.security.SecurityHandler;
import xworker.swt.xwidgets.DataItem;
import xworker.swt.xwidgets.DataItemContainer;

public class SecurityDataItem  extends DataItem{
	boolean dynamic;
	
	public SecurityDataItem(DataItemContainer dataItemContainer, DataItem parentItem, boolean createChilds,
			Thing thing, ActionContext actionContext) {
		super(dataItemContainer, parentItem, createChilds, thing, actionContext);

		boolean noRoot = thing.getBoolean("noRoot");
		if(noRoot) {
			this.visible = false;
		}
		
		List<String> envs = xworker.security.SecurityManager.getEnvs();
		for(String env : envs) {
			Thing itemThing = new Thing();
			itemThing.put("label", env);
			
			EnviromentChecker envChecker = xworker.security.SecurityManager.getEnviromentChecker(env);
			EnvItem evnItem = new EnvItem(envChecker, dataItemContainer, noRoot ? parentItem : this, itemThing, actionContext);
			if(noRoot) {
				if(parentItem  != null) {
					parentItem.addChild(evnItem);
				}else {
					dataItemContainer.addItem(evnItem);
				}
			}else {
				this.addChild(evnItem);
			}
		}
				
	}
	
	@Override
	public boolean isDynamic() {
		return dynamic;
	}

	@Override
	public Object getData() {
		return null;
	}

	public static DataItem create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		DataItemContainer dataItemContainer = actionContext.getObject("dataItemContainer");
		DataItem parentItem = actionContext.getObject("parentItem");
				
		return new SecurityDataItem(dataItemContainer, parentItem, true, self, actionContext);
	}

	static class EnvItem extends DataItem{
		EnviromentChecker envChecker;
		
		public EnvItem(EnviromentChecker envChecker, DataItemContainer dataItemContainer, DataItem parentItem, Thing thing,
				ActionContext actionContext) {
			super(dataItemContainer, parentItem, false, thing, actionContext);
			
			this.envChecker = envChecker;
			
			for(String permission : envChecker.getPermissions()) {
				PermissionChecker pchecker = envChecker.getPermissionChecker(permission);
				Thing pthing = new Thing();
				pthing.put("label", permission);
				PermissionItem pitem = new PermissionItem(pchecker, dataItemContainer, this, pthing, actionContext);
				this.addChild(pitem);
			}
			
			if(envChecker.getSecurityHandler() != null) {
				this.addChild(new SecurityHandlerItem(envChecker.getSecurityHandler(), dataItemContainer, this, actionContext));
			}
		}

		@Override
		public Object getData() {
			return envChecker;
		}
	}
	
	static class PermissionItem extends DataItem {
		PermissionChecker checker;	
		
		public PermissionItem(PermissionChecker checker, DataItemContainer dataItemContainer, DataItem parentItem, Thing thing,
				ActionContext actionContext) {
			super(dataItemContainer, parentItem, false, thing, actionContext);
			
			this.checker = checker;
			
			for(String action : checker.getActions()) {
				PermissionActionChecker achecker = checker.getPermissionActionChecker(action);
				Thing pthing = new Thing();
				pthing.put("label", action);
				PermissionActionCheckerItem pitem = new PermissionActionCheckerItem(achecker, dataItemContainer, this, pthing, actionContext);
				this.addChild(pitem);
			}
			if(checker.getSecurityHandler() != null) {
				this.addChild(new SecurityHandlerItem(checker.getSecurityHandler(), dataItemContainer, this, actionContext));
			}
		}

		@Override
		public Object getData() {
			return checker;
		}
	}
	
	static class PermissionActionCheckerItem extends DataItem{
		PermissionActionChecker checker;	
		
		public PermissionActionCheckerItem(PermissionActionChecker checker, DataItemContainer dataItemContainer, DataItem parentItem, Thing thing,
				ActionContext actionContext) {
			super(dataItemContainer, parentItem, false, thing, actionContext);
			
			this.checker = checker;
			
			for(PathChecker pchecker : checker.getPathCheckers()) {
				Thing pthing = new Thing();
				pthing.put("label", pchecker.getPathRegex());
				PathCheckerItem pitem = new PathCheckerItem(pchecker, dataItemContainer, this, pthing, actionContext);
				this.addChild(pitem);
			}
			
			if(checker.getSecurityHandler() != null) {
				this.addChild(new SecurityHandlerItem(checker.getSecurityHandler(), dataItemContainer, this, actionContext));
			}
			
		}

		@Override
		public Object getData() {
			return checker;
		}
	}
	
	static class PathCheckerItem extends DataItem{
		PathChecker checker;	
		
		public PathCheckerItem(PathChecker checker, DataItemContainer dataItemContainer, DataItem parentItem, Thing thing,
				ActionContext actionContext) {
			super(dataItemContainer, parentItem, false, thing, actionContext);
			
			this.checker = checker;
		}

		@Override
		public Object getData() {
			return checker.getSecurityHandler().getThing();
		}
	}
	
	static class SecurityHandlerItem extends DataItem{
		SecurityHandler securityHandler;	
		
		public SecurityHandlerItem(SecurityHandler securityHandler, DataItemContainer dataItemContainer, DataItem parentItem,
				ActionContext actionContext) {
			super(dataItemContainer, parentItem, false, securityHandler.getThing(), actionContext);
			
			this.securityHandler = securityHandler;
		}

		@Override
		public Object getData() {
			return securityHandler.getThing();
		}
	}
}
