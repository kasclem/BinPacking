import java.util.*;

public class ABFD extends Algorithm {

    TreeSet<Bin> currentBins;
    //LinkedList<Bin> currentBins;

    //sort items to insert according to non-increasing order of weight
    static Comparator<Item> abfd_sort = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            if (o1.weight > o2.weight){
                return -1;
            }else if(o1.weight < o2.weight){
                return 1;
            }else{
                return 0;
            }
        }
    };

    //sort incoming bins according to increasing order of weight capacity
    static Comparator<Bin> abfd_bin_sort = new Comparator<Bin>() {
        @Override
        public int compare(Bin o1, Bin o2) {
            if(o1.weightCapacity > o2.weightCapacity){
                return 1;
            }else if(o1.weightCapacity < o2.weightCapacity){
                return -1;
            }else{
                return 0;
            }
        }
    };

    //sort decreasing bin merit. bin_merit = bin.free_space - bin.used_space
    static StateComparator<Bin> abfdBestBinSort = new StateComparator<Bin>() {
        @Override
        public int compare(Bin o1, Bin o2) {
            BinState b1 = this.state.binStates.get(o1.id);
            BinState b2 = this.state.binStates.get(o2.id);
            int o1_merit = b1.getBinMerit();
            int o2_merit = b2.getBinMerit();

            // should not return 0 because bins with same bin_merit will not be included. TreeSet behavior
            if(o1_merit > o2_merit){
                return -1;
            }else{
                return 1;
            }
        }
    };

    @Override
    public State pack() {
        abfdBestBinSort.setState(this.state);
        currentBins = new TreeSet<>(abfdBestBinSort);
        //currentBins = new LinkedList<Bin>();
        Collections.sort(this.state.itemsToInsert, ABFD.abfd_sort);
        Collections.sort(this.state.incomingBins, ABFD.abfd_bin_sort);
        LinkedList<Item> itemsToInsert = this.state.itemsToInsert;
        while(!itemsToInsert.isEmpty()) {
			Item toInsert = itemsToInsert.pollFirst();
			ABFD.bf_insert(currentBins, toInsert, this.state);
		}
        return this.state;
    }

    //best first insert
    public static void bf_insert(TreeSet<Bin> currentBins, Item toInsert, State state){
        //Collections.sort(currentBins, abfd_best_bin_sort);
        Iterator<Bin> iterator = currentBins.iterator();
        while(iterator.hasNext()){
            Bin curr = iterator.next();
            if(state.insert(curr, toInsert)){
                return;
            };
        }

        Bin newBin = state.incomingBins.pollFirst();
        state.insert(newBin, toInsert);
        currentBins.add(newBin);
        state.currentBins.add(newBin);
    }
}
