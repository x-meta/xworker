package xworker.org.apache.kafka.streams.kstream;

import java.util.function.Function;

import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class KTableActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildFilter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		Predicate predicate = null;
		KTable table = null;
		Materialized materialized = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Predicate && predicate != null) {
				predicate = (Predicate) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && predicate != null && materialized != null) {
				break;
			}
		}
				
		if(predicate != null && named != null && materialized != null) {
			table = ktable.filter(predicate, named, materialized);			
		}else if(predicate != null && named != null) {
			table = ktable.filter(predicate, named);
		}else if(predicate != null && materialized != null) {
			table = ktable.filter(predicate, materialized);
		}else if(predicate != null) {
			table = ktable.filter(predicate);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildFilterNot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		Predicate predicate = null;
		KTable table = null;
		Materialized materialized = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Predicate && predicate != null) {
				predicate = (Predicate) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && predicate != null && materialized != null) {
				break;
			}
		}
				
		if(predicate != null && named != null && materialized != null) {
			table = ktable.filterNot(predicate, named, materialized);			
		}else if(predicate != null && named != null) {
			table = ktable.filterNot(predicate, named);
		}else if(predicate != null && materialized != null) {
			table = ktable.filterNot(predicate, materialized);
		}else if(predicate != null) {
			table = ktable.filterNot(predicate);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildGroupBy(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Grouped grouped = null;
		KeyValueMapper mapper = null;
		KGroupedTable table = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Grouped && grouped != null) {
				grouped = (Grouped) obj;			
			}
			
			if(obj instanceof KeyValueMapper && mapper != null) {
				mapper = (KeyValueMapper) obj;
			}
			
			if(grouped != null && mapper != null) {
				break;
			}
		}
				
		if(grouped != null) {		
			table = ktable.groupBy(mapper, grouped);	
		}else if(mapper != null) {
			table = ktable.groupBy(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kgtable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildJoin(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		KTable other = self.doAction("getTable", actionContext);
		
		ValueJoiner joiner = null;
		Function foreignKeyExtractor = null;
		Named named = null;
		Materialized materialized = null;
		KTable table = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;			
			}
			
			if(obj instanceof Function && foreignKeyExtractor != null) {
				foreignKeyExtractor = (Function) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			if(obj instanceof Named && named != null) {
				named = (Named) obj;
			}
			
			if(joiner != null && foreignKeyExtractor != null && named != null && materialized != null) {
				break;
			}
		}
				
		if(joiner != null && foreignKeyExtractor != null && named != null && materialized != null) {		
			table = ktable.join(other, foreignKeyExtractor, joiner, named, materialized);	
		}else if(joiner != null && foreignKeyExtractor != null && named != null) {
			table = ktable.join(other, foreignKeyExtractor, joiner, named);
		}else if(joiner != null && foreignKeyExtractor != null && materialized != null) {
			table = ktable.join(other, foreignKeyExtractor, joiner, materialized);
		}else if(foreignKeyExtractor != null && joiner != null){
			table = ktable.join(other, foreignKeyExtractor, joiner);
		}else if(joiner != null && named != null && materialized != null) {
			table = ktable.join(other, joiner, named, materialized);
		} else if(joiner != null && materialized != null) {
			table = ktable.join(other, joiner, materialized);
		}else if(joiner != null && named != null) {
			table = ktable.join(other, joiner, named);
		}else if(joiner != null) {
			table = ktable.join(other, joiner);
		}
			
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildLeftJoin(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		KTable other = self.doAction("getTable", actionContext);
		
		ValueJoiner joiner = null;
		Function foreignKeyExtractor = null;
		Named named = null;
		Materialized materialized = null;
		KTable table = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;			
			}
			
			if(obj instanceof Function && foreignKeyExtractor != null) {
				foreignKeyExtractor = (Function) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			if(obj instanceof Named && named != null) {
				named = (Named) obj;
			}
			
			if(joiner != null && foreignKeyExtractor != null && named != null && materialized != null) {
				break;
			}
		}
				
		if(joiner != null && foreignKeyExtractor != null && named != null && materialized != null) {		
			table = ktable.leftJoin(other, foreignKeyExtractor, joiner, named, materialized);	
		}else if(joiner != null && foreignKeyExtractor != null && named != null) {
			table = ktable.leftJoin(other, foreignKeyExtractor, joiner, named);
		}else if(joiner != null && foreignKeyExtractor != null && materialized != null) {
			table = ktable.leftJoin(other, foreignKeyExtractor, joiner, materialized);
		}else if(foreignKeyExtractor != null && joiner != null){
			table = ktable.leftJoin(other, foreignKeyExtractor, joiner);
		}else if(joiner != null && named != null && materialized != null) {
			table = ktable.leftJoin(other, joiner, named, materialized);
		} else if(joiner != null && materialized != null) {
			table = ktable.leftJoin(other, joiner, materialized);
		}else if(joiner != null && named != null) {
			table = ktable.leftJoin(other, joiner, named);
		}else if(joiner != null) {
			table = ktable.leftJoin(other, joiner);
		}
			
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildOuterJoin(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		KTable other = self.doAction("getTable", actionContext);
		
		ValueJoiner joiner = null;
		Named named = null;
		Materialized materialized = null;
		KTable table = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;			
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			if(obj instanceof Named && named != null) {
				named = (Named) obj;
			}
			
			if(joiner != null && named != null && materialized != null) {
				break;
			}
		}
				
		if(joiner != null && named != null && materialized != null) {
			table = ktable.outerJoin(other, joiner, named, materialized);
		} else if(joiner != null && materialized != null) {
			table = ktable.outerJoin(other, joiner, materialized);
		}else if(joiner != null && named != null) {
			table = ktable.outerJoin(other, joiner, named);
		}else if(joiner != null) {
			table = ktable.outerJoin(other, joiner);
		}
			
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildMapValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		ValueMapper mapper = null;
		KTable table = null;
		Materialized materialized = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapper && mapper != null) {
				mapper = (ValueMapper) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && mapper != null && materialized != null) {
				break;
			}
		}
				
		if(mapper != null && named != null && materialized != null) {		
			table = ktable.mapValues(mapper, named, materialized);	
		}else if(mapper != null && named != null) {
			table = ktable.mapValues(mapper, named);
		}else if(mapper != null && materialized != null) {
			table = ktable.mapValues(mapper, materialized);
		}else if(mapper != null) {
			table = ktable.mapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildMapValuesWithKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		ValueMapperWithKey mapper = null;
		KTable table = null;
		Materialized materialized = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapperWithKey && mapper != null) {
				mapper = (ValueMapperWithKey) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && mapper != null && materialized != null) {
				break;
			}
		}
				
		if(mapper != null && named != null && materialized != null) {		
			table = ktable.mapValues(mapper, named, materialized);	
		}else if(mapper != null && named != null) {
			table = ktable.mapValues(mapper, named);
		}else if(mapper != null && materialized != null) {
			table = ktable.mapValues(mapper, materialized);
		}else if(mapper != null) {
			table = ktable.mapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildSuppress(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Suppressed suppressed = null;
		KTable table = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Suppressed && suppressed != null) {
				suppressed = (Suppressed) obj;	
				break;
			}
		}
		
		if(suppressed != null) {
			table = ktable.suppress(suppressed);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildToStream(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		KeyValueMapper mapper = null;
		KStream stream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof KeyValueMapper && mapper != null) {
				mapper = (KeyValueMapper) obj;
			}
			
			if(named != null && mapper != null) {
				break;
			}
		}
				
		if(mapper != null && named != null) {		
			stream = ktable.toStream(mapper, named);	
		}else if(mapper != null) {
			stream = ktable.toStream(mapper);
		}else if(named != null) {
			stream = ktable.toStream(named);
		}else {
			stream = ktable.toStream();
		}
		
		actionContext.l().put(self.getMetadata().getName(), stream);
		if(stream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", stream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildTransformValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KTable ktable = actionContext.getObject("ktable");
		
		Named named = null;
		ValueTransformerWithKeySupplier mapper = null;
		KTable table = null;
		Materialized materialized = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueTransformerWithKeySupplier && mapper != null) {
				mapper = (ValueTransformerWithKeySupplier) obj;
			}
			
			if(obj instanceof Materialized && materialized != null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && mapper != null && materialized != null) {
				break;
			}
		}
				
		if(mapper != null && named != null && materialized != null) {		
			table = ktable.transformValues(mapper, materialized, named, stateStoreNames);	
		}else if(mapper != null && named != null) {
			table = ktable.transformValues(mapper, named, stateStoreNames);
		}else if(mapper != null && materialized != null) {
			table = ktable.transformValues(mapper, materialized, stateStoreNames);
		}else if(mapper != null) {
			table = ktable.transformValues(mapper, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
}
