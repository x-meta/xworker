package xworker.ai.ga;

import java.util.ArrayList;
import java.util.List;

/**
 * 遗传算法。
 * 
 * @author Administrator
 *
 */
public class GeneticAlgorithms {
	/** 基因组群体，种群 */
	List<Genome> genomes = new ArrayList<Genome>();
	
	/** 群体的大小 */
	int popSize;
	
	/** 杂交概率 */
	double crossoverRate;
	
	/** 变异概率 */
	double mutationRate;
	
	/**
	 * 一个时代。
	 */
	public void eopch(){
		updateFitnessScores();
	}
	
	/**
	 * 更新适应分数。
	 */
	public void updateFitnessScores(){
		
	}
}
