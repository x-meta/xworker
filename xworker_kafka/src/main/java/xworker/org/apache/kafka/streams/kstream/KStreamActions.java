package xworker.org.apache.kafka.streams.kstream;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class KStreamActions {
	public static void build(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createBranch(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		List<Predicate> predicates = new ArrayList<Predicate>();
		KStream[] bstreams = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Predicate) {
				predicates.add((Predicate) obj);
			}
			
		}
		
		Predicate[] ps = new Predicate[predicates.size()];
		predicates.toArray(ps);
		
		if(predicates.size() > 0 && named != null) {		
			bstreams = kstream.branch(named, ps);
		}else if(predicates.size() > 0) {
			bstreams = kstream.branch(ps);
		}
		
		actionContext.l().put(self.getMetadata().getName(), bstreams);
		if(bstreams != null) {
			for(KStream ks : bstreams) {
				for(Thing child : self.getChilds()) {
					child.doAction("build", actionContext, "kstream", ks);
				}
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFilter(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		Predicate predicate = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Predicate && predicate != null) {
				predicate = (Predicate) obj;
			}
			
			if(named != null && predicate != null) {
				break;
			}
		}
				
		if(predicate != null && named != null) {		
			fstream = kstream.filter(predicate, named);	
		}else if(predicate != null) {
			fstream = kstream.filter(predicate);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFilterNot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		Predicate predicate = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof Predicate && predicate != null) {
				predicate = (Predicate) obj;
			}
			
			if(named != null && predicate != null) {
				break;
			}
		}
				
		if(predicate != null && named != null) {		
			fstream = kstream.filterNot(predicate, named);	
		}else if(predicate != null) {
			fstream = kstream.filterNot(predicate);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatMap(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		KeyValueMapper mapper = null;
		KStream fstream = null;
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
			fstream = kstream.flatMap(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.flatMap(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatMapValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueMapper mapper = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapper && mapper != null) {
				mapper = (ValueMapper) obj;
			}
			
			if(named != null && mapper != null) {
				break;
			}
		}
				
		if(mapper != null && named != null) {		
			fstream = kstream.flatMapValues(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.flatMapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatMapValuesWithKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueMapperWithKey mapper = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapperWithKey && mapper != null) {
				mapper = (ValueMapperWithKey) obj;
			}
			
			if(named != null && mapper != null) {
				break;
			}
		}
				
		if(mapper != null && named != null) {		
			fstream = kstream.flatMapValues(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.flatMapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createMap(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		KeyValueMapper mapper = null;
		KStream fstream = null;
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
			fstream = kstream.map(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.map(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createMapValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueMapper mapper = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapper && mapper != null) {
				mapper = (ValueMapper) obj;
			}
			
			if(named != null && mapper != null) {
				break;
			}
		}
				
		if(mapper != null && named != null) {		
			fstream = kstream.mapValues(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.mapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createMapValuesWithKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueMapperWithKey mapper = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueMapperWithKey && mapper != null) {
				mapper = (ValueMapperWithKey) obj;
			}
			
			if(named != null && mapper != null) {
				break;
			}
		}
				
		if(mapper != null && named != null) {		
			fstream = kstream.mapValues(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.mapValues(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatTransform(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		TransformerSupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof TransformerSupplier && supplier != null) {
				supplier = (TransformerSupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.flatTransform(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.flatTransform(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatTransformValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueTransformerSupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueTransformerSupplier && supplier != null) {
				supplier = (ValueTransformerSupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.flatTransformValues(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.flatTransformValues(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createFlatTransformValuesWithKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueTransformerWithKeySupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueTransformerWithKeySupplier && supplier != null) {
				supplier = (ValueTransformerWithKeySupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.flatTransformValues(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.flatTransformValues(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createTransform(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		TransformerSupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof TransformerSupplier && supplier != null) {
				supplier = (TransformerSupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.transform(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.transform(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createTransformValues(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueTransformerSupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueTransformerSupplier && supplier != null) {
				supplier = (ValueTransformerSupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.transformValues(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.transformValues(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createTransformValuesWithKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ValueTransformerWithKeySupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ValueTransformerWithKeySupplier && supplier != null) {
				supplier = (ValueTransformerWithKeySupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.transformValues(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			fstream = kstream.transformValues(supplier, stateStoreNames);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createForeach(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ForeachAction action = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ForeachAction && action != null) {
				action = (ForeachAction) obj;
			}
			
			if(named != null && action != null) {
				break;
			}
		}
				
		if(named != null) {		
			kstream.foreach(action, named);	
		}else if(action != null) {
			kstream.foreach(action);
		}			
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createPeek(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		ForeachAction action = null;
		KStream stream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof ForeachAction && action != null) {
				action = (ForeachAction) obj;
			}
			
			if(named != null && action != null) {
				break;
			}
		}
				
		if(named != null) {		
			stream = kstream.peek(action, named);	
		}else if(action != null) {
			stream = kstream.peek(action);
		}
		
		actionContext.l().put(self.getMetadata().getName(), stream);
		if(stream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", stream);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createGroupBy(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Grouped grouped = null;
		KeyValueMapper mapper = null;
		KGroupedStream fstream = null;
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
			fstream = kstream.groupBy(mapper, grouped);	
		}else if(mapper != null) {
			fstream = kstream.groupBy(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kgstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createGroupByKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Grouped grouped = null;
		KGroupedStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Grouped && grouped != null) {
				grouped = (Grouped) obj;		
				break;
			}
			
		}
				
		if(grouped != null) {		
			fstream = kstream.groupByKey(grouped);	
		}else {
			fstream = kstream.groupByKey();
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kgstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createJoinGlobalKTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		GlobalKTable table = self.doAction("getTable", actionContext);
		Named named = null;
		KeyValueMapper keySelector = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof KeyValueMapper && keySelector != null) {
				keySelector = (KeyValueMapper) obj;
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(named != null && keySelector != null && joiner != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.join(table, keySelector, joiner, named);
		}else if(keySelector != null) {
			fstream = kstream.join(table, keySelector, joiner);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createJoinKStream(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KStream stream = self.doAction("getKStream", actionContext);
		StreamJoined streamJoined = null;
		JoinWindows windows = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StreamJoined && streamJoined != null) {
				streamJoined = (StreamJoined) obj;			
			}
			
			if(obj instanceof JoinWindows && windows != null) {
				windows = (JoinWindows) obj;
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(streamJoined != null && windows != null && joiner != null) {
				break;
			}
		}
				
		if(streamJoined != null) {		
			fstream = kstream.join(stream, joiner, windows, streamJoined);
		}else {
			fstream = kstream.join(stream, joiner, windows);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createJoinKTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KTable table = self.doAction("getTable", actionContext);
		Joined joined = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Joined && joined != null) {
				joined = (Joined) obj;			
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(joined != null &&  joiner != null) {
				break;
			}
		}
				
		if(joined != null) {		
			fstream = kstream.join(table, joiner, joined);
		}else {
			fstream = kstream.join(table, joiner);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createLeftJoinGlobalKTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		GlobalKTable table = self.doAction("getTable", actionContext);
		Named named = null;
		KeyValueMapper keySelector = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof KeyValueMapper && keySelector != null) {
				keySelector = (KeyValueMapper) obj;
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(named != null && keySelector != null && joiner != null) {
				break;
			}
		}
				
		if(named != null) {		
			fstream = kstream.leftJoin(table, keySelector, joiner, named);
		}else if(keySelector != null) {
			fstream = kstream.leftJoin(table, keySelector, joiner);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createLeftJoinKStream(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KStream stream = self.doAction("getKStream", actionContext);
		StreamJoined streamJoined = null;
		JoinWindows windows = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StreamJoined && streamJoined != null) {
				streamJoined = (StreamJoined) obj;			
			}
			
			if(obj instanceof JoinWindows && windows != null) {
				windows = (JoinWindows) obj;
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(streamJoined != null && windows != null && joiner != null) {
				break;
			}
		}
				
		if(streamJoined != null) {		
			fstream = kstream.leftJoin(stream, joiner, windows, streamJoined);
		}else {
			fstream = kstream.leftJoin(stream, joiner, windows);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createLeftJoinKTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KTable table = self.doAction("getTable", actionContext);
		Joined joined = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Joined && joined != null) {
				joined = (Joined) obj;			
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(joined != null &&  joiner != null) {
				break;
			}
		}
				
		if(joined != null) {		
			fstream = kstream.leftJoin(table, joiner, joined);
		}else {
			fstream = kstream.leftJoin(table, joiner);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createOuterJoinKStream(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KStream stream = self.doAction("getKStream", actionContext);
		StreamJoined streamJoined = null;
		JoinWindows windows = null;
		ValueJoiner joiner = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StreamJoined && streamJoined != null) {
				streamJoined = (StreamJoined) obj;			
			}
			
			if(obj instanceof JoinWindows && windows != null) {
				windows = (JoinWindows) obj;
			}
			
			if(obj instanceof ValueJoiner && joiner != null) {
				joiner = (ValueJoiner) obj;
			}
			
			if(streamJoined != null && windows != null && joiner != null) {
				break;
			}
		}
				
		if(streamJoined != null) {		
			fstream = kstream.outerJoin(stream, joiner, windows, streamJoined);
		}else {
			fstream = kstream.outerJoin(stream, joiner, windows);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createMerge(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		KStream stream = self.doAction("getKStream", actionContext);
		Named named = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			
			if(obj instanceof Named && named != null) {
				named = (Named) obj;
				break;
			}			
		}
				
		if(named != null) {		
			fstream = kstream.merge(stream, named);
		}else {
			fstream = kstream.merge(stream);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createProcess(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		org.apache.kafka.streams.processor.ProcessorSupplier supplier = null;
		String[] stateStoreNames = self.doAction("getStateStoreNames", actionContext);
	
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named != null) {
				named = (Named) obj;			
			}
			
			if(obj instanceof org.apache.kafka.streams.processor.ProcessorSupplier && supplier != null) {
				supplier = (org.apache.kafka.streams.processor.ProcessorSupplier) obj;
			}
			
			if(named != null && supplier != null) {
				break;
			}
		}
				
		if(named != null) {		
			kstream.process(supplier, named, stateStoreNames);	
		}else if(supplier != null) {
			kstream.process(supplier, stateStoreNames);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createRepartition(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Repartitioned repartitioned = null;
		KStream fstream = null;
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Repartitioned && repartitioned != null) {
				repartitioned = (Repartitioned) obj;			
				break;
			}
		}
				
		if(repartitioned != null) {		
			fstream = kstream.repartition(repartitioned);
		}else  {
			fstream = kstream.repartition();
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void createSelectKey(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		Named named = null;
		KeyValueMapper mapper = null;
		KStream fstream = null;
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
			fstream = kstream.selectKey(mapper, named);	
		}else if(mapper != null) {
			fstream = kstream.selectKey(mapper);
		}
		
		actionContext.l().put(self.getMetadata().getName(), fstream);
		if(fstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "kstream", fstream);
			}
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildTo(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		
		String topic = self.doAction("getTopic", actionContext);
		Produced produced = null;
		TopicNameExtractor topicExtractor = null;
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Produced && produced == null) {
				produced = (Produced) obj;
			}
			
			if(obj instanceof TopicNameExtractor && topicExtractor == null) {
				topicExtractor = (TopicNameExtractor) obj;
			}
			
			if(produced != null && topicExtractor != null) {
				break;
			}
		}
		
		if(topic != null && !"".equals(topic)) {
			if(produced != null) {
				kstream.to(topic, produced);
			}else {
				kstream.to(topic);
			}
		}else {
			if(produced != null && topicExtractor != null) {
				kstream.to(topicExtractor, produced);
			}else if(topicExtractor != null) {
				kstream.to(topicExtractor);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildToTable(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		KStream kstream = actionContext.getObject("kstream");
		KTable ktable = null;
		
		Named named = null;
		Materialized materialized = null;
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Named && named == null) {
				named = (Named) obj;
			}
			
			if(obj instanceof Materialized && materialized == null) {
				materialized = (Materialized) obj;
			}
			
			if(named != null && materialized != null) {
				break;
			}
		}
		
		if(named != null && materialized != null) {
			ktable = kstream.toTable(named, materialized);
		}else if(materialized != null) {
			ktable = kstream.toTable(materialized);
		}else if(named != null) {
			ktable = kstream.toTable(named);
		}else {
			ktable = kstream.toTable();
		}
		
		actionContext.l().put(self.getMetadata().getName(), ktable);
		for(Thing child : self.getChilds()) {
			child.doAction("build", actionContext, "ktable", ktable);
		}
	}
}
