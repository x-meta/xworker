package xworker.service;

import java.util.ArrayList;
import java.util.List;

public class ServiceList {
	List<Service> services = new ArrayList<Service>();
	Service activeService = null;
	
	public void addService(Service service) {
		service.serviceList = this;
		if(services.contains(service) == false) {
			services.add(service);
		}
	}
	
	protected void active(Service service) {
		this.activeService = service;
	}
	
	protected void remove(Service service) {
		services.remove(service);
		
		if(activeService == service) {
			activeService = null;
		}
	}
	
	public Service getActiveService() {
		Service service = activeService;
		if(service != null) {
			return service;
		}
		
		if(services.size() > 0) {
			service = services.get(0);
		}
		
		return service;
	}
	
	public List<Service> getServices(){
		return services;
	}
}
