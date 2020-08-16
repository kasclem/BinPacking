import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class State {
	public static State currState;
	public Category[] compatList;
	public LinkedList<Item> itemsToInsert = new LinkedList<Item>();
	public ArrayList<BinState> currentBins = new ArrayList<BinState>();
	public LinkedList<BinState> incomingBins = new LinkedList<BinState>();
	public ArrayList<Bin> itemBinList = new ArrayList<>();
	public ArrayList<BinState> binStates = new ArrayList<>();

	public void writeStateToFile(String name){
		File file = new File(String.format("serializedFiles/%s", name));
		FileWriter fr = null;

		try {
			fr = new FileWriter(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static State generateState1() {
		resetThings();
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
				Bin newBin = Bin.generateRandom();
			}
		}

		for(Bin bin : Bin.all){
			BinState newBinState = new BinState(bin);
			state.incomingBins.add(newBinState);
			state.binStates.add(newBinState);
		}
		for(Item item : Item.all){
			state.itemBinList.add(null);
		}
		return state;
	}

	public boolean insert(Bin bin, Item item){
		BinState binState = this.binStates.get(bin.id);
		if(!binState.insert(item)){
			return false;
		}


		itemBinList.set(item.id, binState.bin);

		return true;
	}

	public Bin getItemBin(Item item){
		Bin bin = itemBinList.get(item.id);
		return bin;
	}

	//get from Utils
	public static State generateState2(){
		resetThings();
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
		}

		for(Bin bin : Bin.all){
			BinState newBinState = new BinState(bin);
			state.binStates.add(newBinState);
			state.incomingBins.add(newBinState);
		}
		for(Item item : Item.all){
			state.itemBinList.add(null);
		}
		return state;
	}

	private static void resetThings() {
		for(Category c : Category.all){
			c.members.clear();
		}
		Category.all.clear();
		Bin.all.clear();
		Item.all.clear();
	}


	private static void initCategory(State state){
		Category.init(Utils.CATEGORY_TABLE1);
		state.compatList =  Category.all.toArray(new Category[0]);
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
		for(BinState bin : this.currentBins){
			ctr+=bin.size();
		}

		return ctr + this.itemsToInsert.size();
	}


	public int residualCapacity(Bin to) {
		BinState binState = this.binStates.get(to.id);
		int answer = binState.residualCapacity();
		return answer;
	}

	
	public boolean canInsert(Bin to, Item item, int reservedWeight) {
		BinState binState = this.binStates.get(to.id);
		boolean answer = binState.canInsert(item, reservedWeight);
		return answer;
	}

	public boolean canInsert(Bin to, Item item){
		return this.canInsert(to, item, 0);
	}

	public void removeItem(Bin from, Item item){
		BinState binState = this.binStates.get(from.id);
		binState.remove(item);
		this.itemBinList.set(item.id, null);
		this.itemsToInsert.addFirst(item);
	}

	public void detachFromBin(Item item){
		Bin bin = this.itemBinList.get(item.id);
		this.removeItem(bin, item);
	}

	public void clearBin(Bin specificBin){
		BinState binState = this.binStates.get(specificBin.id);
		while(!binState.items.isEmpty()){
			Item i = binState.items.getFirst();
			this.detachFromBin(i);
		}
		this.currentBins.remove(binState);
		this.incomingBins.addFirst(binState);
	}

	public void move(Item item, Bin to){
		this.detachFromBin(item);
		Item item2 = this.itemsToInsert.pollFirst();
		this.insert(to, item2);
	}

	public BinState getBinState(int itemId) {
		Bin bin = this.itemBinList.get(itemId);
		return this.binStates.get(bin.id);
	}

	public State clone(){
		State state = new State();
		state.compatList = this.compatList;
		state.itemsToInsert = new LinkedList<>(this.itemsToInsert);
		state.currentBins = new ArrayList<>();
		state.incomingBins = new LinkedList<>();
		state.itemBinList = new ArrayList<>(this.itemBinList);
		state.binStates = new ArrayList<>();
		for(BinState bs : this.binStates){
			state.binStates.add(bs.clone());
		}
		for(BinState bs : this.currentBins){
			BinState newBs = state.binStates.get(bs.bin.id);
			state.currentBins.add(newBs);
		}
		for(BinState bs : this.incomingBins){
			BinState newBs = state.binStates.get(bs.bin.id);
			state.incomingBins.add(newBs);
		}
		return state;
	}
}
