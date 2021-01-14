package xworker.util.statistics;

public class Speed {
	DataEntry datas[] = new DataEntry[62];
	int index = 0;
	
	public synchronized void add(long data) {
		DataEntry entry = datas[index];
		if(entry == null) {
			entry = new DataEntry();
			entry.add(data);
			datas[index] = entry;
		}else if(entry.isCurrentSecond()) {
			entry.add(data);
		}else {
			index ++;
			if(index >= datas.length) {
				index = 0;
			}
			
			entry = new DataEntry();
			entry.add(data);
			datas[index] = entry;
		}
	}
	
	/**
	 * 实时速度，返回上一秒接收到的数据。
	 * 
	 * @return
	 */
	public double realSpeed() {
		int i = index --;
		if(index == -1) {
			index = datas.length - 1;
		}
		
		DataEntry entry = datas[i];
		if(entry != null && entry.time > (System.currentTimeMillis() / 1000 - 2)) {
			//两秒之内
			return entry.getData();
		}else {
			return 0;
		}
	}
	
	/**
	 * 指定秒数内的平均速度，0 &lt; seconds &lt;= 60。
	 * 
	 * @param seconds
	 * @return
	 */
	public double averageSpeed(int seconds) {
		if(seconds <= 0) {
			return realSpeed();
		}else if(seconds > 60) {
			seconds = 60;
		}
		
		
		double total = 0;
		int idx = index - 1;
		for(int i=0; i<seconds; i++) {
			if(idx < 0) {
				idx = datas.length - 1;				
			}
			DataEntry entry = datas[idx];
			if(entry != null && entry.time >= (System.currentTimeMillis() / 1000 - seconds - 1)) {
				total = total + entry.getData();
			}
			idx --;
		}
		
		return total / seconds;
	}
	
	class DataEntry{
		long time = System.currentTimeMillis() / 1000;
		
		double data = 0;
		
		public boolean isCurrentSecond() {
			if(time >= (System.currentTimeMillis()) / 1000 - 1) {
				return true;
			}else {
				return false;
			}
		}
		
		public boolean isCurrentMinute() {
			if(time / 60 == System.currentTimeMillis() / 60000) {
				return true;
			}else {
				return false;
			}
		}
		
		public void add(double data) {
			this.data += data;
		}
		
		public double getData() {
			return data;
		}
	}
}
