import java.lang.reflect.Array;
import java.util.ArrayList;

public class Runner {

	static ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();

	public static void main(String[] args) {
		int STARTING_COUNT = 300;
		int TRIAL_COUNT = 100;
		int GROWTH = 300;
		int DATA_POINTS = 11;

		setHeaders(DATA_POINTS, STARTING_COUNT, GROWTH);

		for(int i=0 ; i<DATA_POINTS ; i++){
		    double vns_time_total = 0;
		    double vns_count_total = 0;
            double abfd_time_total = 0;
            double abfd_count_total = 0;
            double ffcd_time_total = 0;
            double ffcd_count_total = 0;
            int ITEM_COUNT = STARTING_COUNT + i*GROWTH;
			Utils.ITEMS_PER_CATEGORY= ITEM_COUNT/Utils.CATEGORY_TABLE1.length;
			State state = State.generateState1();

			for(int j=0 ; j<TRIAL_COUNT ; j++){
				set(j+Utils.HEADER_COUNT, 0, String.valueOf(j));
			    System.out.println(String.format("Trial: %d", j));

				System.out.println(String.format("Item count: %d", state.itemsToInsert.size()));
				Experiment vns = run(new VNS(), state);
				vns_time_total+=vns.run_time;
				vns_count_total+=vns.binCount;

				Experiment abfd = run(new ABFD(), state);
				abfd_time_total+=abfd.run_time;
				abfd_count_total+=abfd.binCount;

				Experiment ffcd = run(new FFCD(), state);
				ffcd_time_total += ffcd.run_time;
				ffcd_count_total += ffcd.binCount;

				set(Utils.getRow(j), Utils.getCol(i, 0, 0), String.valueOf(vns.run_time));
				set(Utils.getRow(j), Utils.getCol(i, 1, 0), String.valueOf(vns.binCount));
				set(Utils.getRow(j), Utils.getCol(i, 0, 1), String.valueOf(abfd.run_time));
				set(Utils.getRow(j), Utils.getCol(i, 1, 1), String.valueOf(abfd.binCount));
				set(Utils.getRow(j), Utils.getCol(i, 0, 2), String.valueOf(ffcd.run_time));
				set(Utils.getRow(j), Utils.getCol(i, 1, 2), String.valueOf(ffcd.binCount));
			}
			double avAbfdCount = abfd_count_total/TRIAL_COUNT;
			double avAbfdTime = abfd_time_total/TRIAL_COUNT;
			double avFfcdCount = ffcd_count_total/TRIAL_COUNT;
			double avFfcdTime = ffcd_time_total/TRIAL_COUNT;
			double avVnsCount = vns_count_total/TRIAL_COUNT;
			double avVnsTime = vns_time_total/TRIAL_COUNT;

			System.out.println(String.format(
			        "\n" +
                    "Average for Item count %d:\n" +
                    "Average Bin Counts. ABFD: %f, FFCD: %f, VNS: %f\n" +
                    "Average Run Times. ABFD: %f, FFCD: %f, VNS: %f\n\n",
                    state.itemCount(),
                    avAbfdCount, avFfcdCount, avVnsCount,
                    avAbfdTime, avFfcdTime, avVnsTime
            ));
		}

		Utils.writeToCsv(table);
	}

	private static void setHeaders(int data_points, int starting_count, int growth) {
		for(int i=0 ; i<data_points ; i++){
			for(int j=0 ; j<Utils.ALGOS ; j++){
				for(int k=0 ; k<Utils.VARIABLES ; k++){
					int col = Utils.getCol(i, k, j);
					int ITEM_COUNT = starting_count + i*growth;
					set(0, col, String.valueOf(ITEM_COUNT));
					set(1, col, Utils.ALGOS_ARR[j]);
					set(2, col, Utils.VARIABLES_ARR[k]);
				}
			}
		}
	}

	private static void set(int i, int j, String valueOf) {
		if(table.size() <= i){
			for(int s = table.size() ; s<=i ; s++){
				table.add(new ArrayList<String>());
			}
		}
		ArrayList<String> row = table.get(i);

		if(row.size() <= j){
			for(int s = row.size() ; s<=j ; s++){
				row.add("");
			}
		}
		row.set(j, valueOf);
	}

	static Experiment run(Algorithm algo, State state) {
		Experiment x = new Experiment(state, algo);
		int itemCount = state.itemsToInsert.size();
		x.run();
		System.out.println(x);
		return x;
	}

	// try removing object from list
	private static void ex1() {

	}
}