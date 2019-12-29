import com.rits.cloning.Cloner;

import java.util.Arrays;
import java.util.Comparator;

public class Runner {
	public static void main(String[] args) {
		State state = State.generateState2();
		Algorithm algo = new VNS();
		Experiment x = new Experiment(state, algo);
		x.run();
		System.out.println(x.run_time);

		//Runner.ex1();
	}

	// try removing object from list
	private static void ex1() {

	}
}