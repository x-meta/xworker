package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TimeWindowedKStreamActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildAggregate(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TimeWindowedKStream kstream = actionContext.getObject("twkstream");
		
		Named named = null;
		Initializer initializer = null;
		KTable table = null;
		Materialized materialized = null;
		Aggregator aggregator = null;

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Initializer && initializer != null) {
				initializer = (Initializer) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(obj instanceof Aggregator && aggregator != null) {
				aggregator = (Aggregator) obj;
			}
			
			if(named != null && initializer != null && materialized != null 
					&& aggregator != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {	
			table = kstream.aggregate(initializer, aggregator, named, materialized);
		}else if(materialized != null) {
			table = kstream.aggregate(initializer, aggregator, materialized);
		}else if(named != null){
			table = kstream.aggregate(initializer, aggregator, named);
		}else {
			table = kstream.aggregate(initializer, aggregator);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildCount(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TimeWindowedKStream kstream = actionContext.getObject("twkstream");
		
		Named named = null;
		KTable table = null;
		Materialized materialized = null;
		

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && materialized != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {		
			table = kstream.count(named, materialized);	
		}else if(named != null) {
			table = kstream.count(named);
		}else if(materialized != null) {
			table = kstream.count(materialized);
		}else {
			table = kstream.count();
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildReduce(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		TimeWindowedKStream kstream = actionContext.getObject("twkstream");
		
		Named named = null;
		KTable table = null;
		Materialized materialized = null;
		Reducer reducer = null;

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(obj instanceof Reducer && reducer != null) {
				reducer = (Reducer) obj;
			}
					
			if(named != null && materialized != null && reducer != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {		
			table = kstream.reduce(reducer, named, materialized);	
		}else if(materialized != null) {
			table = kstream.reduce(reducer, materialized);
		}else {
			table = kstream.reduce(reducer);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
}
