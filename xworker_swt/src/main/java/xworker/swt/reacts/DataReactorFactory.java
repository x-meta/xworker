package xworker.swt.reacts;

import java.awt.Composite;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.app.IEditorContainer;
import xworker.swt.reacts.creators.BrowserDataReactorCreator;
import xworker.swt.reacts.creators.CComboDataReactorCreator;
import xworker.swt.reacts.creators.CTabFolderDataReactorCreator;
import xworker.swt.reacts.creators.ComboDataReactorCreator;
import xworker.swt.reacts.creators.CompositeDataReactorCreator;
import xworker.swt.reacts.creators.EditorContainerDataReactorCreator;
import xworker.swt.reacts.creators.ListDataReactorCreator;
import xworker.swt.reacts.creators.SelectionDataReactorCreator;
import xworker.swt.reacts.creators.TableDataReactorCreator;
import xworker.swt.reacts.creators.TextDataReactorCreator;
import xworker.swt.reacts.creators.ThingDataReactorCreator;
import xworker.swt.reacts.creators.TreeDataReactorCreator;
import xworker.swt.reacts.datas.CollectionDataReactor;
import xworker.swt.reacts.filters.FileFilter;
import xworker.swt.reacts.filters.SourceDataFilter;
import xworker.swt.reacts.filters.ThingFilter;


public class DataReactorFactory {
	//private static Logger logger = LoggerFactory.getLogger(DataReactorFactory.class);
	private static Map<Class<?>, DataReactorCreator> creators = new HashMap<Class<?>, DataReactorCreator>();
	private static Map<String, DataFilter> filters = new HashMap<String, DataFilter>();
	
	static {
		initReactors();
	}
	
	public static void initReactors() {
		creators.put(Text.class, new TextDataReactorCreator());
		creators.put(Browser.class, new BrowserDataReactorCreator());
		creators.put(CCombo.class, new CComboDataReactorCreator());
		creators.put(Combo.class, new ComboDataReactorCreator());
		creators.put(org.eclipse.swt.widgets.List.class, new ListDataReactorCreator());
		try {
			//StyledText是特殊的，在RWT不存在，只能通过反射获取
			Class<?> cls = Class.forName("org.eclipse.swt.custom.StyledText");
			Class<?> creatorCls = Class.forName("xworker.swt.reacts.creators.StyledTextDataReactorCreator");
			Object creator = creatorCls.getConstructor(new Class<?>[0]).newInstance(new Object[0]);
			creators.put(cls, (DataReactorCreator) creator);
		}catch(Exception e) {			
		}
		creators.put(Table.class, new TableDataReactorCreator());
		SelectionDataReactorCreator selectionCreator = new SelectionDataReactorCreator();
		creators.put(MenuItem.class, selectionCreator);
		creators.put(ToolItem.class, selectionCreator);
		creators.put(Button.class, selectionCreator);
		creators.put(Thing.class, new ThingDataReactorCreator());
		creators.put(Tree.class, new TreeDataReactorCreator());
		creators.put(CTabFolder.class, new CTabFolderDataReactorCreator());
		creators.put(IEditorContainer.class, new EditorContainerDataReactorCreator());
		creators.put(Composite.class, new CompositeDataReactorCreator());
		
		//初始化Filter
		filters.put("thing", new ThingFilter());
		filters.put("file", new FileFilter());
		filters.put("source", new SourceDataFilter());
	}
	
	public static DataReactor create(String action, String[] params,  Object control, ActionContext actionContext) {
		if(control == null) {
			return null;
		}
		
		//分割响应器和过滤器
		String[] acs = null;		
		if(action != null) {
			acs = action.split("[|]");
			action = acs[0];
		}
		
		//创建数据响应器
		DataReactor dataReactor = null;
		DataReactorCreator creator = creators.get(control.getClass());
		if(creator != null) {
			dataReactor = creator.create(control, action, actionContext);
		}else {
			//一些数据相关的
			if(control instanceof Collection) {
				Thing thing = new Thing("xworker.swt.reactors.datas.CollectionDataReactor");
				CollectionDataReactor reactor = new CollectionDataReactor(thing, actionContext);
				reactor.setCollection((Collection<?>) control);
				dataReactor =  reactor;
			}else {
				Thing thing = new Thing("xworker.swt.reactors.DataReactor");
				DataReactor reactor = new DataReactor(thing, actionContext);
				reactor.getDatas().add(control);
				dataReactor = reactor;
			}
			
			//logger.warn("Can not create DataReactor, Creator not exists, class=" + control.getClass());
			//return null;
		}	
		
		//添加过滤器
		if(params != null && params.length > 1) {
			for(int i=1; i<params.length; i++) {
				String filterName = params[i];
				Object filter = actionContext.getObject(filterName);
				if(filter instanceof DataFilter) {
					dataReactor.addDataFilter((DataFilter) filter);
					continue;
				}
				
				DataFilter f = filters.get(filterName);
				if(f != null) {
					dataReactor.addDataFilter(f);
					continue;
				}
			}
		}
		return dataReactor;
	}
	
	/**
	 * 返回DataFilter，首先从变量上下文中获取，其次从预注册的缓存中获取。
	 * 
	 * @param name
	 * @param actionContext
	 * @return
	 */
	public static DataFilter getDataFilter(String name, ActionContext actionContext) {
		Object obj = actionContext.get(name);
		if(obj instanceof DataFilter) {
			return (DataFilter) obj;
		}
		
		return filters.get(name);
	}
}
