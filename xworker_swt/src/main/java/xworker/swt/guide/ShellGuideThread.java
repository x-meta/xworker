package xworker.swt.guide;

/**
 * 用于自动检测任务是否已经完成。
 * 
 * @author zhangyuxiang
 *
 */
public class ShellGuideThread implements Runnable{
	ShellGuide guide;
	boolean stop = false;
	
	public ShellGuideThread(ShellGuide guide) {
		this.guide = guide;
	}
	
	public void stop() {
		stop = true;
	}
	
	@Override
	public void run() {
		while(true) {
			if(stop) {
				break;
			}
			
			if(guide.isDisposed()) {
				break;
			}
			
			try {
				if(guide.maskComposite != null && guide.maskComposite.isDisposed() == false) {
					guide.maskComposite.getDisplay().syncExec(new Runnable() {
						public void run() {
							try {
								guide.checkCurrentNode();
							}catch(Exception e) {
								e.printStackTrace();						
							}
						}
					});
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
