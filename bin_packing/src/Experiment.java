import com.rits.cloning.Cloner;

public class Experiment {
	public State state;
	public Algorithm algo;
	public double score;
	public double run_time;
	public int binCount;


	public Experiment(State state, Algorithm algo) {
		this.state = state;
		this.algo = algo;
	}


	public void run() {
		System.gc();

		State copy = state.clone();
		//System.out.println(original);
		this.algo.setState(copy);
		long start_time = System.nanoTime();
		State output = this.algo.pack();
		long end_time = System.nanoTime();
		//System.out.println(output);
		double difference = (end_time - start_time) / 1e9;
		this.run_time = difference;
		this.binCount = output.currentBins.size();
	}

	@Override
	public String toString() {
		return String.format("%s Run Time: %f, Bin Count: %d", this.algo.name, this.run_time, this.binCount);
	}
}