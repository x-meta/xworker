package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.CogroupedKStream;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.SessionWindowedCogroupedKStream;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.SlidingWindows;
import org.apache.kafka.streams.kstream.TimeWindowedCogroupedKStream;
import org.apache.kafka.streams.kstream.Windows;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CogroupedKStreamActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildAggregate(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CogroupedKStream kstream = actionContext.getObject("cgkstream");
		
		Named named = null;
		Initializer initializer = null;
		KTable table = null;
		Materialized materialized = null;

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
			
			if(named != null && initializer != null && materialized != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {	
			table = kstream.aggregate(initializer, named, materialized);	
		}else if(materialized != null) {
			table = kstream.aggregate(initializer, materialized);
		}else if(named != null) {
			table = kstream.aggregate(initializer, named);
		}else {
			table = kstream.aggregate(initializer);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildCogroup(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CogroupedKStream kstream = actionContext.getObject("cgkstream");
		
		CogroupedKStream cgkstream = null;
		Aggregator aggregator = null;
		KGroupedStream groupedStream = self.doAction("getGroupedStream", actionContext);

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Aggregator && aggregator != null) {
				aggregator = (Aggregator) obj;
				break;
			}
		}
				
		cgkstream = kstream.cogroup(groupedStream, aggregator);
		
		actionContext.l().put(self.getMetadata().getName(), cgkstream);
		if(cgkstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "cgkstream", cgkstream);
			}
		}	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void buildWindowedBySessionWindows(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CogroupedKStream kstream = actionContext.getObject("cgkstream");
		
		SessionWindowedCogroupedKStream	swkstream = null;
		SessionWindows windows = null;
		

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof SessionWindows && windows != null) {
				windows = (SessionWindows) obj;
				break;
			}
		}
				
		swkstream = kstream.windowedBy(windows);
		
		actionContext.l().put(self.getMetadata().getName(), swkstream);
		if(swkstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "swckstream", swkstream);
			}
		}	
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void buildWindowedBySlidingWindows(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CogroupedKStream kstream = actionContext.getObject("cgkstream");
		
		TimeWindowedCogroupedKStream twkstream = null;
		SlidingWindows windows = null;
		

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof SlidingWindows && windows != null) {
				windows = (SlidingWindows) obj;
				break;
			}
		}
				
		twkstream = kstream.windowedBy(windows);
		
		actionContext.l().put(self.getMetadata().getName(), twkstream);
		if(twkstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "twckstream", twkstream);
			}
		}	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void buildWindowedBy(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		CogroupedKStream kstream = actionContext.getObject("cgkstream");
		
		TimeWindowedCogroupedKStream twkstream = null;
		Windows windows = null;
		

		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof Windows && windows != null) {
				windows = (Windows) obj;
				break;
			}
		}
				
		twkstream = kstream.windowedBy(windows);
		
		actionContext.l().put(self.getMetadata().getName(), twkstream);
		if(twkstream != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "twckstream", twkstream);
			}
		}	
	}
}
