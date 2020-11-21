package xworker.util.statistics;

public class Summary {
	long count = 0;
	double total = 0;
	double max = 0;
	double min = 0;
	double average = 0;
	
	public synchronized void add(double data) {
		count++;
		
		total += data;
		
		if(max < data) {
			max = data;
		}
		if(min > data) {
			min = data;
		}		
	}
	
	public double total() {
		return total;
	}
	
	public double max() {
		return max;
	}
	
	public double min() {
		return min;
	}
	
	public double average() {
		if(count == 0) {
			return 0;
		}
		return total / count;
	}
	
	public long count() {
		return count;
	}
}
