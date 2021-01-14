package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Merger;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.SessionWindowedCogroupedKStream;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SessionWindowedCogroupedKStreamActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void buildAggregate(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		SessionWindowedCogroupedKStream kstream = actionContext.getObject("swckstream");
		
		Named named = null;
		Initializer initializer = null;
		KTable table = null;
		Materialized materialized = null;
		Merger sessionMerger = null;
		

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
			
			if(obj instanceof Merger && sessionMerger != null) {
				sessionMerger = (Merger) obj;
			}
	
			if(named != null && initializer != null && materialized != null 
					&& sessionMerger != null) {
				break;
			}
		}
				
		if(named != null && materialized != null) {	
			table = kstream.aggregate(initializer, sessionMerger, named, materialized);
		}else if(materialized != null) {
			table = kstream.aggregate(initializer, sessionMerger, materialized);
		}else if(named != null){
			table = kstream.aggregate(initializer, sessionMerger, named);
		}else {
			table = kstream.aggregate(initializer, sessionMerger);
		}
		
		actionContext.l().put(self.getMetadata().getName(), table);
		if(table != null) {
			for(Thing child : self.getChilds()) {
				child.doAction("build", actionContext, "ktable", table);
			}
		}	
	}
}
