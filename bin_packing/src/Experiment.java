import com.rits.cloning.Cloner;

public class Experiment {
	public State state;
	public Algorithm algo;
	public double score;
	public double run_time;


	public Experiment(State state, Algorithm algo) {
		this.state = state;
		this.algo = algo;
	}


	public void run() {
		State original = Utils.c.deepClone(state);
		//System.out.println(original);
		this.algo.setState(this.state);
		long start_time = System.nanoTime();
		State output = this.algo.pack();
		long end_time = System.nanoTime();
		//System.out.println(output);
		double difference = (end_time - start_time) / 1e9;
		this.run_time = difference;
	}
}