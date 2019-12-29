import java.util.ArrayList;
import java.util.LinkedList;

public class State {
	public static State currState;
	public Category[] compatList;
	public LinkedList<Item> itemsToInsert = new LinkedList<Item>();
	public ArrayList<Bin> currentBins = new ArrayList<Bin>();
	public LinkedList<Bin> incomingBins = new LinkedList<Bin>();
	public ArrayList<Bin> itemBinList = new ArrayList<>();
	public ArrayList<BinState> binStates = new ArrayList<>();

	public static State generateState1() {
		State state = new State();
		//CATEGORY
		State.initCategory(state);
		//ITEMS AND BINS
		for(int i=0 ; i<state.compatList.length ; i++) {
			Category curr = state.compatList[i];
			for(int j=0 ; j<Utils.ITEMS_PER_CATEGORY ; j++) {
				Item item = Item.generateRandom();
				item.c = curr;
				curr.members.add(item);
				state.itemsToInsert.add(item);
				state.incomingBins.add(Bin.generateRandom());
			}
		}

		for(Bin bin : Bin.all){
			state.binStates.add(new BinState(bin));
		}
		for(Item item : Item.all){
			state.itemBinList.add(null);
		}
		return state;
	}


	public boolean insert(Bin bin, Item item){
		if(!bin.insert(item)){
			return false;
		}
		itemBinList.set(item.id, bin);
		return true;
	}

	public Bin getItemBin(Item item){
		Bin bin = itemBinList.get(item.id);
		return bin;
	}

	//get from Utils
	public static State generateState2(){
		State state = new State();
		State.initCategory(state);

		for(int[] row : Utils.SAMPLE_STATE){
			int cati = row[0];
			int weight = row[1];
			Category cat = Category.all.get(cati);
			Item item = new Item();
			item.c = cat;
			cat.members.add(item);
			item.weight = weight;
			state.itemsToInsert.add(item);
		}

		for(int bin_weight : Utils.SAMPLE_STATE_BINS){
			Bin bin = new Bin();
			bin.weightCapacity = bin_weight;
			state.incomingBins.add(bin);
		}

		for(Bin bin : Bin.all){
			state.binStates.add(new BinState(bin));
		}
		for(Item item : Item.all){
			state.itemBinList.add(null);
		}
		return state;
	}

	private static void initCategory(State state){
		Category.init(Utils.CATEGORY_TABLE1);
		state.compatList =  Category.all.toArray(new Category[0]);
	}


	public static State specificState(int[] binCapacities, int[][] item_category_tuple){
		State state = new State();
		State.initCategory(state);
		for(int x: binCapacities){
			Bin bin = Bin.createBin(x);
			state.incomingBins.add(bin);
		}
		for(int[] row: item_category_tuple){
			int weight = row[0];
			int category = row[1];
			Item item = Item.createItem(weight, category);
			state.itemsToInsert.add(item);
		}
		return state;
	}


	@Override
	public String toString() {
		String string = String.format(
				"Items: %s\n" +
						"Used Bins: %s\n" +
						"Bins to use: %s", this.itemsToInsert, this.currentBins, this.incomingBins
		);
		return string;
	}

	public int itemCount(){
		int ctr = 0;
		for(Bin bin : this.currentBins){
			ctr+=bin.size();
		}
		return ctr;
	}

	public void clearBin(Bin bin) {
		bin.clear();
	}
}
