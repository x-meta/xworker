package xworker.dataObject.swt.reacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xworker.dataObject.DataObject;

public class DataObjectReactor{
	private static ThreadLocal<DataObjectReactorContext> contextLocal = new ThreadLocal<DataObjectReactorContext>();
	
	public final static DataObjectReactorContext getContext(){
		return contextLocal.get();
	}
	
	public final static void setContext(DataObjectReactorContext context) {
		contextLocal.set(context);
	}
	
	public DataObjectReactor() {		
	}	
	
	protected List<DataObject> toDataObjectList(DataObject ...dataObjects ){
		List<DataObject> list = new ArrayList<DataObject>();
		if(dataObjects != null) {
			for(int i=0; i<dataObjects.length; i++) {
				list.add(dataObjects[i]);
			}
		}
		
		return list;
	}
	
	protected List<DataObjectReactor> getNextReactors(){
		return Collections.emptyList();
	}
	
	private final boolean begin() {
		DataObjectReactorContext context = getContext();
		if(context == null) {
			context = new DataObjectReactorContext();
			setContext(context);
		}
		
		return !context.isExists(this);
	}
	
	private final void finish() {
		DataObjectReactorContext context = getContext();
		if(context != null) {
			context.removeCount();
		}
	}
	
	public final void fireSelected(List<DataObject> dataObjects) {
		for(DataObjectReactor reactor : getNextReactors()) {
			reactor.onSelected(dataObjects);
		}			
	}
	
	public final void onSelected(List<DataObject> dataObjects) {
		if(begin()) {
			try {	
				doOnSelected(dataObjects);
			}finally {
				finish();
			}
		}
	}
	
	public void doOnSelected(List<DataObject> dataObjects) {		
	}

	public final void fireUnselected() {
		for(DataObjectReactor reactor :  getNextReactors()) {
			reactor.onUnselected();
		}
	}
	
	public final void onUnselected() {
		if(begin()) {
			try {
				doOnUnselected();
			}finally {
				finish();
			}
		}
	}
	
	public void doOnUnselected() {
		
	}

	public final void fireAdded(int index, List<DataObject> dataObjects) {
		for(DataObjectReactor reactor :  getNextReactors()) {
			reactor.onAdded(index, dataObjects);
		}		
	}
	
	public final void onAdded(int index, List<DataObject> dataObjects) {
		if(begin()) {
			try {
				doOnAdded(index, dataObjects);
			}finally {
				finish();
			}
		}	
	}
	
	public void doOnAdded(int index, List<DataObject> dataObjects) {
		
	}

	public final void fireRemoved(List<DataObject> dataObjects) {
		for(DataObjectReactor reactor :  getNextReactors()) {
			reactor.onRemoved(dataObjects);
		}			
	}
	
	public final void onRemoved(List<DataObject> dataObjects) {
		if(begin()) {
			try {
				doOnRemoved(dataObjects);
			}finally {
				finish();
			}
		}	
	}	
	
	public void doOnRemoved(List<DataObject> dataObjects) {
		
	}

	public final void fireUpdated(List<DataObject> dataObjects) {
		for(DataObjectReactor reactor :  getNextReactors()) {
			reactor.onUpdated(dataObjects);
		}
	}
	
	public final void onUpdated(List<DataObject> dataObjects) {
		if(begin()) {
			try {
				doOnUpdated(dataObjects);
			}finally {
				finish();
			}
		}	
	}

	public void doOnUpdated(List<DataObject> dataObjects) {
		
	}
	
	public final void fireLoaded(List<DataObject> dataObjects) {
		for(DataObjectReactor reactor :  getNextReactors()) {
			reactor.onLoaded(dataObjects);
		}
	}
	
	public final void onLoaded(List<DataObject> dataObjects) {
		if(begin()) {
			try {
				doOnLoaded(dataObjects);
			}finally {
				finish();
			}
		}	
	}

	public void doOnLoaded(List<DataObject> dataObjects) {
		
	}
}
