
public class Experiment {
	public State state;
	public Algorithm algo;
	public double score;
	public double run_time;
	public int binCount;
	public long runSize;


	public Experiment(State state, Algorithm algo) {
		this.state = state;
		this.algo = algo;
	}


	public void run() {
		System.gc();

		State copy = state.clone();
		//System.out.println(original);
		this.algo.setState(copy);

		System.gc();
		long startSize = Runtime.getRuntime().totalMemory();
		long start_time = System.nanoTime();
		State output = this.algo.pack();
		long end_time = System.nanoTime();
		long endSize = Runtime.getRuntime().totalMemory();
		//System.out.println(output);
		double difference = (end_time - start_time) / 1e9;
		long sizeConsumption = endSize - startSize;
		this.run_time = difference;
		this.binCount = output.currentBins.size();
		this.runSize = sizeConsumption;
	}

	@Override
	public String toString() {
		return String.format("%s Run Time: %f, Bin Count: %d, Memory: %d", this.algo.name, this.run_time, this.binCount, this.runSize);
	}
}