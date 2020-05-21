package xworker.swt.reacts;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ProxyDataReactor extends DataReactor{
	DataReactor dataReactor;
	DataReactor callbackDataReactor = null;
	public ProxyDataReactor(Thing self, ActionContext actionContext) {
		this(self, actionContext, true);
	}
	
	public ProxyDataReactor(Thing self, ActionContext actionContext, boolean createChilds) {
		super(self, actionContext, createChilds);
		
		Thing callback = new Thing("xworker.swt.reactors.DataReactor");
		callbackDataReactor = new ProxyCallbackDataReactor(this, callback, actionContext);
	}

	/**
	 * 设置要被代理的dataReactor。
	 * 
	 * @param dataReactor
	 */
	public void setDataReactor(DataReactor dataReactor) {
		this.dataReactor = dataReactor;
		this.dataReactor.addNextReator(callbackDataReactor);
		
		//把filter移植到要被代理的dataReactor
		for(DataFilter dataFilter : this.filters) {
			dataReactor.addDataFilter(dataFilter);
		}
	}
	
	@Override
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnSelected(datas, context);
		}
	}

	@Override
	protected void doOnUnselected(DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnUnselected(context);
		}
	}

	@Override
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnAdded(index, datas, context);
		}
	}

	@Override
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnRemoved(datas, context);
		}
	}

	@Override
	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnUpdated(datas, context);
		}
	}

	@Override
	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
		if(dataReactor != null) {
			dataReactor.doOnLoaded(datas, context);
		}
	}

	public static class ProxyCallbackDataReactor  extends DataReactor{
		ProxyDataReactor proxy;
		
		public ProxyCallbackDataReactor(ProxyDataReactor proxy, Thing self, ActionContext actionContext) {
			super(self, actionContext);
			
			this.proxy = proxy;
		}

		@Override
		protected void doOnSelected(List<Object> datas, DataReactorContext context) {
			proxy.fireSelected(datas, context);
		}

		@Override
		protected void doOnUnselected(DataReactorContext context) {
			proxy.fireUnselected(context);
		}

		@Override
		protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
			proxy.fireAdded(index, datas, context);
		}

		@Override
		protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
			proxy.fireRemoved(datas, context);
		}

		@Override
		protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
			proxy.fireUpdated(datas, context);
		}

		@Override
		protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
			proxy.fireLoaded();
		}
		
		
	}
}
