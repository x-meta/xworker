package xworker.notification;

public interface NotificationManagerListener {
	public void added(Notification notification);
	
	public void removed(Notification notification);
	
	public void updated(Notification notification);
}
