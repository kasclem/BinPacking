import java.util.*;

public class ABFD extends Algorithm {

    TreeSet<BinState> currentBins;
    //LinkedList<Bin> currentBins;

    public ABFD(){
        this.name = "ABFD";
    }

    //sort items to insert according to decreasing order of weight
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
    static Comparator<BinState> abfd_bin_sort = new Comparator<BinState>() {
        @Override
        public int compare(BinState o1, BinState o2) {
            if(o1.bin.weightCapacity > o2.bin.weightCapacity){
                return 1;
            }else if(o1.bin.weightCapacity < o2.bin.weightCapacity){
                return -1;
            }else{
                return 0;
            }
        }
    };

    //sort decreasing bin merit. bin_merit = bin.free_space - bin.used_space
    static StateComparator<BinState> abfdBestBinSort = new StateComparator<BinState>() {
        @Override
        public int compare(BinState o1, BinState o2) {
            int o1_merit = o1.getBinMerit();
            int o2_merit = o2.getBinMerit();

            // should not return 0 because bins with same bin_merit will not be included. TreeSet behavior
            if(o1_merit > o2_merit){
                return -1;
            }else if(o1.equals(o2)){
                return 0;
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
    public static void bf_insert(TreeSet<BinState> currentBins, Item toInsert, State state){
        Iterator<BinState> iterator = currentBins.iterator();
        int max = Integer.MIN_VALUE;
        BinState bestBin = null;
        while(iterator.hasNext()){
            BinState curr = iterator.next();
            if(curr.getBinMerit() > max){
                if(state.canInsert(curr.bin, toInsert)){
                    max = curr.getBinMerit();
                    bestBin = curr;
                }
            }
        }

        if(bestBin!=null){
            state.insert(bestBin.bin, toInsert);
            return;
        }
        BinState newBin = state.incomingBins.pollFirst();
        state.insert(newBin.bin, toInsert);
        currentBins.add(newBin);
        state.currentBins.add(newBin);
    }
}
