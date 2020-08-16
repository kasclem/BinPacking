import java.util.Collections;
import java.util.Comparator;

public class FFCD extends Algorithm {

	public FFCD(){
		this.name = "FFCD";
	}

	//sorter increasing dck decreasing weight
	static Comparator<Item> ffcd_sort =  new Comparator<Item>() {
		@Override
		public int compare(Item o1, Item o2) {
			int dck1 = o1.c.compatible.size();
			int dck2 = o2.c.compatible.size();
			if(dck1>dck2) {
				return 1;
			}else if(dck1<dck2) {
				return -1;
			}
//			else if(o1.c.id > o2.c.id){
//				return 1;
//			}
			else if(o1.weight > o2.weight) {
				return -1;
			}else if(o1.weight < o2.weight){
				return 1;
			}else if(o1.id > o2.id){
				return 1;
			}else{
				return -1;
			}
		}
	};

	// todo: optimize, don't start from beginning?
	public static void ffd(Item item, State currState) {
		for(BinState b : currState.currentBins) {
			if(currState.insert(b.bin, item)){
				return;
			}
		}
		BinState newBin = currState.incomingBins.pollFirst();
		currState.currentBins.add(newBin);

		currState.insert(newBin.bin, item);

	}

	@Override
	public State pack() {
		Collections.sort(this.state.itemsToInsert, FFCD.ffcd_sort);
		while(!this.state.itemsToInsert.isEmpty()) {
			Item toInsert = this.state.itemsToInsert.pollFirst();
			FFCD.ffd(toInsert, this.state);
		}
		return this.state;
	}
}
