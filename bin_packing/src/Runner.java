
import java.util.Arrays;
import java.util.Comparator;

public class Runner {
	public static void main(String[] args) {
		int starting_count = 1;
		int growth = 1;
		for(int i=10 ; i<20 ; i++){
			for(int j=0 ; j<3 ; j++){
				Utils.ITEMS_PER_CATEGORY= starting_count + ((i));
				State state = State.generateState1();
				System.out.println(String.format("Item count: %d", state.itemsToInsert.size()));
				run(new VNS(), state);
				run(new ABFD(), state);
				run(new FFCD(), state);
				System.out.println("\n");

			}
		}
	}

	public static void run(Algorithm algo, State state) {
		Experiment x = new Experiment(state, algo);
		int itemCount = state.itemsToInsert.size();
		x.run();
		System.out.println(x);
	}

	// try removing object from list
	private static void ex1() {

	}
}