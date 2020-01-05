import com.rits.cloning.Cloner;

import java.util.Arrays;
import java.util.Comparator;

public class Runner {
	public static void main(String[] args) {
		for(int i=0 ; i<30 ; i++){
			State state = State.generateState1();
			System.out.println(String.format("Item count: %d", state.itemsToInsert.size()));
			run(new VNS(), state);
			run(new ABFD(), state);
			run(new FFCD(), state);
			System.out.println("\n");
			Utils.ITEMS_PER_CATEGORY+=20;
		}
	}

	private static void run(Algorithm algo, State state) {
			Experiment x = new Experiment(state, algo);
			int itemCount = state.itemsToInsert.size();
			x.run();
			System.out.println(x);
	}

	// try removing object from list
	private static void ex1() {

	}
}