package xworker.finance.exchange;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CurrencyActions {
	public static double getExchangeRate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String baseCode = self.doAction("getBaseCode", actionContext);
		String termCode = self.doAction("getTermCode", actionContext);
		
		return CurrencyUtils.getexChangeRate(baseCode, termCode);
	}
	
	public static double exchangeRate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String baseCode = self.doAction("getBaseCode", actionContext);
		String termCode = self.doAction("getTermCode", actionContext);
		double amount = self.doAction("getAmount", actionContext);
		
		return CurrencyUtils.exchange(baseCode, termCode, amount);
	}
}
