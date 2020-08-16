import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Utils {
	public static String[] VARIABLES_ARR = new String[]{"Time(s)", "Bin Count"};
	public static String[] ALGOS_ARR = new String[]{"VNS", "ABFD", "FFCD"};

	public static final int[][] CATEGORY_TABLE1 = new int[][]{
		new int[] {1, 0, 1, 0, 0, 0}, //0
		new int[] {0, 1, 0, 1, 1, 1}, //1
		new int[] {1, 0, 1, 1, 0, 0}, //2
		new int[] {0, 1, 1, 1, 1, 1}, //3
		new int[] {0, 1, 0, 1, 1, 0}, //4
		new int[] {0, 1, 0, 1, 0, 1}, //5
	};

	public static final int[][] SAMPLE_STATE = new int[][]{
			new int[] {0, 4},
			new int[] {0, 8},
			new int[] {0, 7},
			new int[] {1, 11},
			new int[] {1, 11},
			new int[] {1, 10},
			new int[] {2, 12},
			new int[] {2, 10},
			new int[] {2, 11},
			new int[] {3, 6},
			new int[] {3, 10},
			new int[] {3, 12},
			new int[] {4, 9},
			new int[] {4, 8},
			new int[] {4, 8},
			new int[] {5, 4},
			new int[] {5, 9},
			new int[] {5, 8},
	};

	public static final int[] SAMPLE_STATE_BINS = new int[]{
			21, 15, 18, 18, 15, 15, 18, 21, 15, 18, 21, 18, 15, 21, 21, 18, 21, 18
	};

	// info log on?
	public static final boolean DEBUG = true;

	//used by VNS.N3
	//todo: on experiments use alpha=0.05 beta=0.075
	public static final double ALPHA = 0.05;
	public static int ITEMS_PER_CATEGORY = 50;
	public static final int MIN_ITEM_WEIGHT = 4;
	public static final int MAX_ITEM_WEIGHT = 13;
	public static final int[] WEIGHT_CHOICES = new int[] {15, 18, 21};

	//stopping criterion:
	private static int numOfItems = ITEMS_PER_CATEGORY * 6; // there are 6 categories
	public static final int LAMBDA = numOfItems/2; // based from moura-santos experiments
	public static final int PSI = numOfItems*5; // based from moura-santos experiments
	public static Random r = new Random();

	// high exclusive
	public static int randIntRange(int low, int high) {
		int result = r.nextInt(high-low) + low;
		return result;
	}

	public static int chooseFrom(int[] choices) {
		int i = Utils.randIntRange(0, choices.length);
		return choices[i];
	}

	// high exclusive
	static Set<Integer> chosen = new HashSet<>();
	// todo: LowPrio optimize
	public static Set<Integer> chooseFromRange(int low, int high, int count){
		chosen.clear();
		while (chosen.size() < count){
			int newNum = randIntRange(low, high);
			chosen.add(newNum);
		}
		return chosen;
	}

	public static void log(){

	}


	public static int getRow(int trialCount) {
		return trialCount + HEADER_COUNT;
	}

	// variable = (0, 1) either run_time or bin count
	// algorithm = (0, 1, 2) either vns, abfd or ffcd
	static int VARIABLES = 2;
	static int ALGOS = 3;
	public static int getCol(int i, int variable, int algorithm) {
		return i * (VARIABLES * ALGOS + 1) + algorithm * VARIABLES + variable + 1;
	}


	public static final int HEADER_COUNT = 3;
	public static void writeToCsv(ArrayList<ArrayList<String>> table) {
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter("myfile.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		for(ArrayList<String> row : table){
			for(String cell : row){
				sb.append(cell);
				sb.append(",");
			}
			sb.append("\n");
		}

		try {
			br.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
