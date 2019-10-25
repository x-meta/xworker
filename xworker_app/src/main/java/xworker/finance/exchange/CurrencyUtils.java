package xworker.finance.exchange;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import javax.money.spi.Bootstrap;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.internal.loader.DefaultLoaderService;
import org.javamoney.moneta.spi.LoaderService;

import xworker.task.TaskManager;

public class CurrencyUtils {
	static {
		TaskManager.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					CurrencyUtils.loadDatas();
				}catch(Exception e) {
					
				}
			}
		}, 0, 30 * 60000, TimeUnit.MILLISECONDS);
	}
	
	public static Collection<String> getExchangeProviders(){
		return MonetaryConversions.getConversionProviderNames();
	}
	
	public static String[] getDefaultProviders(){
		List<String> providers = MonetaryConversions.getDefaultConversionProviderChain();
		String ps[] = new String[providers.size()];
		providers.toArray(ps);
		return ps;
	}
	
	public static double getexChangeRate(String baseCode, String termCode){
		return getexChangeRate(baseCode, termCode, getDefaultProviders());
	}
	
	public static double getexChangeRate(String baseCode, String termCode, String ... providers){
		ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(providers);
		ExchangeRate rate = provider.getExchangeRate(baseCode, termCode);
		
		return rate.getFactor().doubleValue();
	}
	
	public static double exchange(String baseCode, String termCode, double amount, String ... providers){
		ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(providers);
		CurrencyConversion conversion = provider.getCurrencyConversion(termCode);
		
		//MonetaryAmountFactory<?> f = Monetary.getDefaultAmountFactory();
		//f.setCurrency(baseCode);
		//f.setNumber(amount);
		//MonetaryAmount termMoney = f.create();
		
		MonetaryAmount termMoney = Money.of(amount, baseCode);		
		MonetaryAmount baseMoney = termMoney.with(conversion);
		return baseMoney.getNumber().doubleValue();		
	}
	
	public static double exchange(String baseCode, String termCode, double amount){
		return exchange(baseCode, termCode, amount, getDefaultProviders());
	}
	
	public static void loadDatas() {
		DefaultLoaderService loader = (DefaultLoaderService) Bootstrap.getService(LoaderService.class);
        for(String resourceId : loader.getResourceIds()) {
        	//System.out.println("Load Resource: " + resourceId);
        	loader.unload(resourceId);
			
        	loader.loadDataAsync(resourceId);
        }
	}
	
	
	public static void main(String args[]){
		try{
			System.out.println(MonetaryConversions.getConversionProviderNames());
			
			/*
			List<CurrencyUnit> units = new ArrayList<CurrencyUnit>();
			for(CurrencyUnit unit: Monetary.getCurrencies()) {
				units.add(unit);
			}
			ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider();					
			Map<CurrencyUnit, CurrencyUnit> map = new HashMap<CurrencyUnit, CurrencyUnit>();
			for(int i=0; i<units.size() - 1; i++) {				
				for(int n=i+1; n<units.size(); n++) {
					try {
						provider.getExchangeRate(units.get(i), units.get(n));
						System.out.println("have");
						map.put(units.get(i), units.get(i));
					}catch(Exception e) {
					
					}
				}
			}
			
			System.out.println(map);*/
			//System.out.println(getexChangeRate("USD", "CNY"));
			Thread.sleep(2000);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd Hh:mm:ss");
			while(true) {
				//System.out.println(sf.format(new Date()) + "：" + exchange("USD", "CNY", 1, "ECB","IMF", "IDENT", "ECB-HIST", "IMF-HIST"));
				loadDatas();
				Thread.sleep(10 * 1000);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
