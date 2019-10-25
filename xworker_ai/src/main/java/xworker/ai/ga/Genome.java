package xworker.ai.ga;

/**
 * 基因组，使用索引进行编码。
 * 
 * @author Administrator
 *
 */
public class Genome {
	/** 染色体 */
	int[] chromosome ;
	
	/** 不同基因的总数 */	
	int geneTotalCount;
	
	/** 该基因组的适应度 */
	double fitness = 0;
	
	/**
	 * 创建一个基因组。
	 * 
	 * @param geneTotalCount
	 * @param genesLength
	 */
	public Genome(int geneTotalCount, int genesLength){
		chromosome = new int[genesLength];
	}
	
	/**
	 * 返回该基因组的适应度。
	 * 
	 * @return
	 */
	public double getFitness(){
		return fitness;
	}
}
