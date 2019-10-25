package xworker.ai.aima.learning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import aima.core.learning.framework.Attribute;
import aima.core.learning.framework.AttributeSpecification;
import aima.core.learning.framework.DataSet;
import aima.core.learning.framework.Example;
import aima.core.learning.framework.Learner;
import aima.core.learning.inductive.DLTestFactory;
import aima.core.learning.learners.AdaBoostLearner;
import aima.core.learning.learners.CurrentBestLearner;
import aima.core.learning.learners.DecisionListLearner;
import aima.core.learning.learners.DecisionTreeLearner;
import aima.core.learning.learners.MajorityLearner;
import ognl.Ognl;
import ognl.OgnlException;

public class LearnerActions {
	@SuppressWarnings("unchecked")
	public static AdaBoostLearner createAdaBoostLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		List<Learner> learners = (List<Learner>) self.doAction("getLearners", actionContext);
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		AdaBoostLearner learner = new AdaBoostLearner(learners, dataSet);
		if(dataSet != null){
			learner.train(dataSet);
		}
		
		return learner;
	}
	
	public static CurrentBestLearner createCurrentBestLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		CurrentBestLearner learner = new CurrentBestLearner(self.getString("trueGoalValue"));
		if(dataSet != null){
			learner.train(dataSet);
		}
		
		return learner;
	}
	
	public static DecisionListLearner createDecisionListLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		DecisionListLearner learner = new DecisionListLearner(self.getString("positive"), self.getString("negative"), new DLTestFactory());
		if(dataSet != null){
			learner.train(dataSet);
		}
		
		return learner;
	}
	
	public static DecisionTreeLearner createDecisionTreeLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		DecisionTreeLearner learner = new DecisionTreeLearner();
		if(dataSet != null){
			learner.train(dataSet);
		}
		
		return learner;
	}
	
	public static MajorityLearner createMajorityLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		MajorityLearner learner = new MajorityLearner();
		if(dataSet != null){
			learner.train(dataSet);
		}
		
		return learner;
	}
	
	public static List<Learner> getLearners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		List<Learner> learners = new ArrayList<Learner>();
		Thing lthings =self.getThing("Learners@0");
		if(lthings != null){
			for(Thing l : lthings.getChilds()){
				Learner learner = (Learner) l.doAction("create", actionContext);
				if(learner != null){
					learners.add(learner);
				}
			}
		}
		
		return learners;
	}
	
	/**
	 * 返回数据集。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static DataSet getDataSet(ActionContext actionContext){
		return getDataSet1(actionContext);
	}
	
	public static DataSet getDataSet1(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String dataSetVar = self.getStringBlankAsNull("dataSetVar");
		if(dataSetVar != null){
			return (DataSet) actionContext.get(dataSetVar);
			
		}
		String dataSetStr = self.getStringBlankAsNull("dataSetThingPath");
		if(dataSetStr != null){
			Thing dataSet = World.getInstance().getThing(dataSetStr);
			if(dataSet != null){
				return (DataSet) dataSet.doAction("create", actionContext);
			}
		}
		
		Thing dataSet = self.getThing("Dataset@0");
		if(dataSet != null){
			return (DataSet) dataSet.doAction("create", actionContext);
		}
		
		return null;
	}
	
	public static Learner getLearner(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String learnerVar = self.getStringBlankAsNull("learnerVar");
		if(learnerVar != null){
			return (Learner) actionContext.get(learnerVar);
		}
		
		String learnerThingPath = self.getStringBlankAsNull("learnerThingPath");
		if(learnerThingPath != null){
			Thing thing = World.getInstance().getThing(learnerThingPath);
			if(thing != null){
				return (Learner) thing.doAction("create", actionContext);
			}
		}
		
		Thing learners = self.getThing("Learners@0");
		if(learners != null && learners.getChilds().size() > 0){
			return (Learner) learners.getChilds().get(0).doAction("create", actionContext);
		}
		
		return null;
	}
	
	public static Object getData(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String dataVar = self.getStringBlankAsNull("dataVar");
		if(dataVar != null){
			return actionContext.get(dataVar);
		}
		
		return null;
	}
	
	public static Example getExample(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		String exampleVar = self.getStringBlankAsNull("exampleVar");
		if(exampleVar != null){
			return (Example) actionContext.get(exampleVar);
		}
		
		Object data = self.doAction("getData", actionContext);
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		if(data != null && dataSet != null){
			Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
			List<String> attrNames = dataSet.getAttributeNames();
			for(String attrName : attrNames){
				Object value = Ognl.getValue(attrName, data);
				AttributeSpecification attributeSpec = dataSet.specification.getAttributeSpecFor(attrName);
				attributes.put(attrName, attributeSpec.createAttribute(String.valueOf(value)));
			}
			return new Example(attributes, attributes.get(dataSet.specification.getTarget()));
		}
		
		return null;
	}
	
	public static void runTrain(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Learner learner = (Learner) self.doAction("getLearner", actionContext);
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		if(learner != null && dataSet != null){
			learner.train(dataSet);
		}
	}
	
	public static Object runPredict(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Learner learner = (Learner) self.doAction("getLearner", actionContext);
		Example example = (Example) self.doAction("getExample", actionContext);
		
		if(learner != null && example != null){
			return learner.predict(example);
		}
		
		return null;
	}
	
	public static Object runTest(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Learner learner = (Learner) self.doAction("getLearner", actionContext);
		DataSet dataSet = (DataSet) self.doAction("getDataSet", actionContext);
		
		if(learner != null && dataSet != null){
			return learner.test(dataSet);
		}
		
		return null;
	}
}
