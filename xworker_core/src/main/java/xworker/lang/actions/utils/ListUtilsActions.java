package xworker.lang.actions.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ListUtilsActions {
	public static Object newArrayList(ActionContext actionContext){
		return new ArrayList<Object>();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> emptyIfNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		if(list == null){
			return Collections.emptyList();
		}else{
			return list;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> defaultIfNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		List<T> defaultList = (List<T>) self.doAction("getDefaultList", actionContext);
		if(list == null){
			return defaultList;
		}else{
			return list;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> intersection(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list1 = (List<T>) self.doAction("getList1", actionContext);
		List<T> list2 = (List<T>) self.doAction("getList2", actionContext);
		
		return ListUtils.intersection(list1, list2);
	}	
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> subtract(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list1 = (List<T>) self.doAction("getList1", actionContext);
		List<T> list2 = (List<T>) self.doAction("getList2", actionContext);
		
		return ListUtils.subtract(list1, list2);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> sum(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list1 = (List<T>) self.doAction("getList1", actionContext);
		List<T> list2 = (List<T>) self.doAction("getList2", actionContext);
		
		return ListUtils.sum(list1, list2);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> union(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list1 = (List<T>) self.doAction("getList1", actionContext);
		List<T> list2 = (List<T>) self.doAction("getList2", actionContext);
		
		return ListUtils.union(list1, list2);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> select(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<? extends E> inputCollection = (Collection<? extends E>) self.doAction("getInputCollection", actionContext);
		Predicate predicate= (Predicate) self.doAction("getPredicate", actionContext);
		
		Collection<? extends E> cls = CollectionUtils.select(inputCollection, predicate);
		List<E> list = new ArrayList<E>();
		for(E e : cls){
			list.add(e);
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> selectRejected(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<? extends E> inputCollection = (Collection<? extends E>) self.doAction("getInputCollection", actionContext);
		Predicate predicate= (Predicate) self.doAction("getPredicate", actionContext);
		
		Collection<? extends E> cls = CollectionUtils.selectRejected(inputCollection, predicate);
		List<E> list = new ArrayList<E>();
		for(E e : cls){
			list.add(e);
		}
		
		return list;
	}
	
	public static boolean isEqualList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<?> list1 = (Collection<?>) self.doAction("getList1", actionContext);
		Collection<?> list2 = (Collection<?>) self.doAction("getList2", actionContext);
		
		return ListUtils.isEqualList(list1, list2);
	}
	
	public static int hashCodeForList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<?> list = (Collection<?>) self.doAction("getList", actionContext);
		
		return ListUtils.hashCodeForList(list);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> retainAll(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<E> collection = (Collection<E>) self.doAction("getCollection", actionContext);
		Collection<?> retain = (Collection<?>) self.doAction("getRetain", actionContext);
		
		return ListUtils.retainAll(collection, retain);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> List<E> removeAll(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Collection<E> collection = (Collection<E>) self.doAction("getCollection", actionContext);
		Collection<?> remove = (Collection<?>) self.doAction("getRemove", actionContext);
		
		return ListUtils.retainAll(collection, remove);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> synchronizedList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		return ListUtils.synchronizedList(list);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> unmodifiableList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		return ListUtils.synchronizedList(list);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> predicatedList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		Predicate predicate= (Predicate) self.doAction("getPredicate", actionContext);
		return ListUtils.predicatedList(list, predicate);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> transformedList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		Transformer transformer= (Transformer) self.doAction("getTransformer", actionContext);
		return ListUtils.transformedList(list, transformer);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> lazyList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		Factory factory= (Factory) self.doAction("getFactory", actionContext);
		return ListUtils.lazyList(list, factory);		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> fixedSizeList(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		return ListUtils.fixedSizeList(list);		
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static <T> int indexOf(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		List<T> list = (List<T>) self.doAction("getList", actionContext);
		Predicate predicate= (Predicate) self.doAction("getPredicate", actionContext);
		throw new ActionException("method 'indexOf' not implemented ");
		//return ListUtils.indexOf(list, predicate);		
	}
}
