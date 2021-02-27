package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedTable;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Reducer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class KGroupedTableActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildAggregate(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KGroupedTable ktable = actionContext.getObject("kgtable");
		
		Named named = null;
		Initializer initializer = null;
		KTable table = null;
		Materialized materialized = null;
		Aggregator adder = null;
		Aggregator subtractor = null;
		

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
			
			if(obj instanceof Aggregator && adder != null) {
				adder = (Aggregator) obj;
			}
			
			if(obj instanceof Aggregator && subtractor != null) {
				subtractor = (Aggregator) obj;
			}
			
			if(named != null && initializer != null && materialized != null && adder != null && subtractor != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {		
			table = ktable.aggregate(initializer, adder, subtractor, named, materialized);	
		}else if(named != null) {
			table = ktable.aggregate(initializer, adder, subtractor, named);
		}else if(materialized != null) {
			table = ktable.aggregate(initializer, adder, subtractor, materialized);
		}else {
			table = ktable.aggregate(initializer, adder, subtractor);
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
		KGroupedTable ktable = actionContext.getObject("kgtable");
		
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
			table = ktable.count(named, materialized);	
		}else if(named != null) {
			table = ktable.count(named);
		}else if(materialized != null) {
			table = ktable.count(materialized);
		}else {
			table = ktable.count();
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
		KGroupedTable ktable = actionContext.getObject("kgtable");
		
		Named named = null;
		KTable table = null;
		Materialized materialized = null;
		Reducer adder = null;
		Reducer subtractor = null;
		

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(obj instanceof Reducer && adder != null) {
				adder = (Reducer) obj;
			}
			
			if(obj instanceof Reducer && subtractor != null) {
				subtractor = (Reducer) obj;
			}
			
			if(named != null && materialized != null && adder != null && subtractor != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {		
			table = ktable.reduce(adder, subtractor, named, materialized);	
		}else if(materialized != null) {
			table = ktable.reduce(adder, subtractor, materialized);
		}else {
			table = ktable.reduce(adder, subtractor);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
}
