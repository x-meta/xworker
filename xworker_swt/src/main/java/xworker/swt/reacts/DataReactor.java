package xworker.swt.reacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

/**
 * DataReactor是一个数据事件响应模型。当一组数据发生变化时触发相应的事件，事件可以传递到后续的响应器，后续的响应器根据
 * 事件的类型决定处理方法。
 * 
 * @author zyx
 *
 */
public class DataReactor{
	public static final byte ADDED = 1;
	public static final byte SELECTED = 2;
	public static final byte LOADED = 3;
	public static final byte REMOVED = 4;
	public static final byte UPDATED = 5;
	public static final byte UNSELECTED = 6;
	
	protected Thing self;
	protected ActionContext actionContext;
	/** 前一个激活本DataReactor的DataReactor */
	protected DataReactor preDataReactor;
	/** 绑定到的对象 */
	protected Object bindObject;	
	/** 当前Reactor所维持的数据对象列表 */
	protected List<Object> datas = new ArrayList<Object>(); 
	protected List<DataReactor> nextReactors = new ArrayList<DataReactor>();
	protected List<DataFilter> filters = new ArrayList<DataFilter>();
	protected int maxDataCount = 500;
	
	public DataReactor(Thing self, ActionContext actionContext) {
		this(self, actionContext, true);
	}
	
	public DataReactor(Thing self, ActionContext actionContext, boolean createChilds) {
		this.self = self;
		this.actionContext = actionContext;
		int count = self.getInt("maxDataCount");
		if(count > 0) {
			this.maxDataCount = count;
		}
		
		if(createChilds) {
			//创建子节点的
			actionContext.peek().put("parent", null);		
			for(Thing child : self.getChilds()) {
				Object obj = child.doAction("create", actionContext);
				if(obj instanceof DataFilter) {
					filters.add((DataFilter) obj); 
				}
				//不需要下面的代码，因为在getNextReactions()方法里会获取
				//else if(obj instanceof DataReactor) {
				//	addNextReator((DataReactor) obj);
				//}
			}
		}
	}

	public void addDataFilter(DataFilter dataFilter) {
		if(filters.contains(dataFilter) == false) {
			filters.add(dataFilter);
		}
	}
	
	public Object getBindObject() {
		return bindObject;
	}


	public DataReactor getPreDataReactor() {
		return preDataReactor;
	}

	public void addNextReator(DataReactor reactor) {
		if(nextReactors.contains(reactor) == false) {
			nextReactors.add(reactor);
		}
	}
	
	public List<DataReactor> getNextReactors() {
		//取属性设置的
		List<String> nextReactors = self.doAction("getNextReactors", actionContext);
		List<DataReactor> list = new ArrayList<DataReactor>();
		if(nextReactors != null) {
			for(String ext : nextReactors) {
				Object obj = actionContext.get(ext);
				if(obj instanceof DataReactor && list.contains(obj) == false) {
					list.add((DataReactor) obj);
				}
			}
		}
		
		//取子节点设置的
		for(Thing child : self.getChilds()) {
			String name = child.getMetadata().getName();
			Object obj = actionContext.get(name);
			if(obj instanceof DataReactor && list.contains(obj) == false) {
				list.add((DataReactor) obj);
			}
		}
		
		//本对象设置的Reactor
		for(DataReactor reactor : this.nextReactors) {
			if(list.contains(reactor) == false) {
				list.add(reactor);
			}
		}
		
		return list;
	}
	
	protected List<Object> toList(Object ...datas ){
		List<Object> list = new ArrayList<Object>();
		if(datas != null) {
			for(int i=0; i<datas.length; i++) {
				list.add(datas[i]);
			}
		}
		
		return list;
	}
	
	private final boolean begin(DataReactorContext context) {
		context.push(this);
		
		return !context.isExists(this);
	}
	
	private final void finish(DataReactorContext context) {
		context.pop();
	}
	
	public final void fireSelected() {
		fireSelected(datas, null);
	}
	
	public final void fireSelected(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		context.push(this);
		try {
			datas = doFilter(DataReactor.SELECTED, datas);
			if(datas == null) {
				return;
			}
			
			if(datas.size() > 0) {
				for(DataReactor reactor : getNextReactors()) {
					reactor.preDataReactor = this;
					reactor.onSelected(datas, context);
				}			
			}else {
				this.fireUnselected(context);
			}
		}finally {
			context.pop();
		}
	}
		
	public int getMaxDataCount() {
		return maxDataCount;
	}

	public void setMaxDataCount(int maxDataCount) {
		this.maxDataCount = maxDataCount;
	}

	public final void onSelected(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		if(begin(context)) {
			try {	
				this.datas.clear();
				this.datas.addAll(datas);
				
				doOnSelected(datas, context);
			}finally {
				finish(context);
			}
		}
	}
	
	protected void doOnSelected(List<Object> datas, DataReactorContext context) {		
	}

	public final void fireUnselected() {
		fireUnselected(null);
	}
	
	public final void fireUnselected(DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		try {
			context.push(this);
		
			List<Object> datas = doFilter(DataReactor.UNSELECTED, Collections.emptyList());
			if(datas == null) {
				return;
			}
			
			for(DataReactor reactor :  getNextReactors()) {
				reactor.preDataReactor = this;
				reactor.onUnselected(context);
			}
		}finally {
			context.pop();
		}
	}
	
	public final void onUnselected(DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		
		if(begin(context)) {
			try {				
				datas.clear();
				
				doOnUnselected(context);
			}finally {
				finish(context);
			}
		}
	}
	
	protected void doOnUnselected(DataReactorContext context) {
		
	}

	private final List<Object> doFilter(byte event, List<Object> datas){		
		List<Object> list = datas;
		//过滤数据
		if(event != DataReactor.UNSELECTED) {
			for(DataFilter filter : filters) {
				list = filter.filterDatas(this, event, list);
				if(list == null || list.size() == 0) {
					if(event != DataReactor.LOADED) {
						//load事件可以加载空的数据
						return null;
					}
				}
			}
		}
		
		//过滤事件
		for(DataFilter filter : filters) {
			if(!filter.filterEvents(this, event, list)) {
				return null;
			}
		}
		
		return list;
	}
	
	public final void fireAdded(int index, List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		try {
			context.push(this);
			
			datas = doFilter(DataReactor.ADDED, datas);
			if(datas == null) {
				return;
			}
			
			for(DataReactor reactor :  getNextReactors()) {
				reactor.preDataReactor = this;
				reactor.onAdded(index, datas, context);
			}	
		}finally {
			context.pop();
		}
	}
	
	public final void onAdded(int index, List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		if(begin(context)) {
			try {
				for(Object data : datas) {
					if(datas != null && this.datas.contains(data) == false) {
						if(index < 0 || index >= this.datas.size()) {
							this.datas.add(data);
						}else {
							this.datas.add(index, data);
						}
					}
				}
				
				//超出最大数量删除
				while(this.datas.size() > this.maxDataCount) {
					this.datas.remove(0);
				}
				
				
				doOnAdded(index, datas, context);
			}finally {
				finish(context);
			}
		}	
	}
	
	protected void doOnAdded(int index, List<Object> datas, DataReactorContext context) {
		
	}

	public final void fireRemoved(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		try {
			context.push(this);
			
			datas = doFilter(DataReactor.REMOVED, datas);
			if(datas == null) {
				return;
			}
			
			for(DataReactor reactor :  getNextReactors()) {
				reactor.preDataReactor = this;
				reactor.onRemoved(datas, context);
			}	
		}finally {
			context.pop();
		}
	}
	
	public final void onRemoved(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		if(begin(context)) {
			try {
				for(Object data : datas) {
					this.datas.remove(data);
				}
				
				doOnRemoved(datas, context);
			}finally {
				finish(context);
			}
		}	
	}	
	
	protected void doOnRemoved(List<Object> datas, DataReactorContext context) {
		
	}

	public final void fireUpdated(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		context.push(this);
		try {
			datas = doFilter(DataReactor.UPDATED, datas);
			if(datas == null) {
				return;
			}
			
			for(DataReactor reactor :  getNextReactors()) {
				reactor.preDataReactor = this;
				reactor.onUpdated(datas, context);
			}
		}finally {
			context.pop();
		}
	}
	
	public final void onUpdated(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		if(begin(context)) {
			try {
				for(Object data : datas) {
					if(data != null && this.datas.contains(data) == false) {
						this.datas.add(data);
					}
				}
				
				//超出最大数量删除
				while(this.datas.size() > this.maxDataCount) {
					this.datas.remove(0);
				}
				
				doOnUpdated(datas, context);
			}finally {
				finish(context);
			}
		}	
	}

	protected void doOnUpdated(List<Object> datas, DataReactorContext context) {
		
	}
	
	public final void fireLoaded(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		try {
			context.push(this);
			
			datas = doFilter(DataReactor.LOADED, datas);
			if(datas == null) {
				return;
			}
			
			for(DataReactor reactor :  getNextReactors()) {
				reactor.preDataReactor = this;
				reactor.onLoaded(datas, context);
			}
		}finally {
			context.pop();
		}
	}
	
	public final void onLoaded(List<Object> datas, DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext(); 
		}
		if(begin(context)) {
			try {
				this.datas.clear();
				if(datas != null) {
					this.datas.addAll(datas);
				}
				
				doOnLoaded(datas, context);
			}finally {
				finish(context);
			}
		}	
	}

	protected void doOnLoaded(List<Object> datas, DataReactorContext context) {
		
	}
	
	public List<Object> getDatas(){
		return datas;
	}

	public Thing getSelf() {
		return self;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	public int getDataIndex(Object data) {
		return datas.indexOf(data);
	}
	
	public void fireLoaded() {
		fireLoaded(null);
	}
	
	public void fireLoaded(DataReactorContext context) {
		if(context == null) {
			context = new DataReactorContext();
			context.isExists(this);
		}
		
		this.fireLoaded(datas, context);
	}
	
	/**
	 * 获取parent变量。
	 * 
	 * @param actionContext
	 * @return
	 */
	public Widget getParentWidget(ActionContext actionContext) {
		List<Bindings> bindings = actionContext.getScopes();
		for(int i=bindings.size() - 1; i>=0; i--) {
			Object widget = bindings.get(i).get("parent");
			if(widget != null && widget instanceof Widget) {
				return (Widget) widget;
			}
		}
		
		return null;
	}
}
