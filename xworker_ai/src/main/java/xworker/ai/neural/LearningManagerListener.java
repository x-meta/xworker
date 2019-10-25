package xworker.ai.neural;

public interface LearningManagerListener {
	public void learningAdded(Learning learning);
	
	public void learningRemoved(Learning learning);
}
