import java.util.Collections;
import java.util.Comparator;

public class FFCD extends Algorithm {

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
			}else if(o1.weight > o2.weight) {
				return -1;
			}else{
				return 0;
			}
		}
	};

	// todo: optimize, don't start from beginning?
	public static void ffd(Item item, State currState) {
		for(Bin b : currState.currentBins) {
			if(currState.insert(b, item)){
				return;
			}
		}
		Bin newBin = currState.incomingBins.pollFirst();
		currState.currentBins.add(newBin);
		currState.insert(newBin, item);
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
