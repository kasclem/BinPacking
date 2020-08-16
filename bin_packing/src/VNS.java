import java.util.*;

public class VNS extends Algorithm {

    static Algorithm ffcd = new FFCD();

    public VNS(){
        this.name = "VNS";
    }

    static ShakingOperator n1 = new ShakingOperator("N1") {
        @Override
        public State operate(State state) {
            int index = Utils.randIntRange(0, Category.all.size());
            Category selected = state.compatList[index];

            for(Item i : selected.members){
                VNS.detachItemFromBin(i, state);
            }

            ffcd.setState(state);
            ffcd.pack();
            return state;
        }
    };


    private static void detachItemFromBin(Item i, State state) {
        Bin bin = state.itemBinList.get(i.id);
        BinState binState = state.binStates.get(bin.id);
        if (binState.size() <= 1){
            state.currentBins.remove(binState);
            state.incomingBins.addFirst(binState);
        }
        state.detachFromBin(i);
    }

    static ShakingOperator n2 = new ShakingOperator("N2") {
        @Override
        public State operate(State state) {
            int index = Utils.randIntRange(0, Category.all.size());
            Category selected = Category.all.get(index);
            for(Item i : selected.members){
                VNS.detachItemFromBin(i, state);
            }

            // pick again but different category
            int index2 = index;
            while(index2 == index){
                index2 = Utils.randIntRange(0, Category.all.size());
            }
            selected = Category.all.get(index2);
            for(Item i : selected.members){
                VNS.detachItemFromBin(i, state);
            }

            //ffcd
            ffcd.setState(state);
            ffcd.pack();
            return state;
        }
    };

    static ShakingOperator n3 = new ShakingOperator("N3") {
        @Override
        public State operate(State state) {
            int currBinCount = state.currentBins.size();
            int binsToEmpty = VNS.N3GetBinCount(currBinCount);
            Set<Integer> chosenIndexes = Utils.chooseFromRange(0, currBinCount, binsToEmpty);
            toRemove.clear();
            for(Integer i_curr : chosenIndexes){
                toRemove.addFirst(state.currentBins.get(i_curr));
            }
            for(BinState binState : toRemove){
                state.clearBin(binState.bin);
            }
            ffcd.setState(state);
            State response = ffcd.pack();
            return response;
        }
    };
    ShakingOperator[] shakings = new ShakingOperator[]{n1, n2, n3};

    // sort by increasing used capacity
    static StateComparator<BinState> l1_comparator = new StateComparator<BinState>() {
        @Override
        public int compare(BinState o1, BinState o2) {
            int o1C = o1.usedCapacity;
            int o2C = o2.usedCapacity;
            if( o1C > o2C ){
                return 1;
            }else if ( o1C < o2C ){
                return -1;
            }else{
                return 0;
            }
        }
    };

    // l1 helper, returns false if move is not performed
    // Note: We want to prioritize: item weight larger, to residue smaller, from residue larger
    static boolean canMove(State state, Bin from, Bin to, Item item, boolean checkToMove){
        /*
        the residual capacity of bin iB after the move is smaller than the
        residual capacity of bin iA before the move;
        */
        int reservedWeight = 0;
        if(checkToMove){
            for(Item toMoveItem : toMove){
                reservedWeight+=toMoveItem.weight;
            }
        }
        boolean cond1 = ( state.residualCapacity(to)-item.weight - reservedWeight) < state.residualCapacity(from)+reservedWeight;
        if (!cond1) return false;
        if (!state.canInsert(to, item, reservedWeight)) return false;
        return true;
    }

    //objective score
    static int fitnessFunction(State state){
        int score = 0;
        for( BinState binState : state.currentBins ){
            score += binState.usedCapacity * binState.usedCapacity;
        }
        return score;
    }

    //1.) sort bins by descending residual capacity
    static LinkedList<Item> toMove = new LinkedList<>();
    static LinkedList<BinState> toRemove = new LinkedList<>();
    static State L1(State state){
        // todo: maybe, this is not needed here:
        toMove.clear();
        l1_comparator.setState(state);
        ArrayList<BinState> l1_currentbins = new ArrayList<>(state.currentBins);
        l1_currentbins.sort(l1_comparator);
        int binSize = l1_currentbins.size();
        for(int i=0 ; i<binSize ; i++){
            BinState fromBin = l1_currentbins.get(i);
            for(int j=i+1 ; j<binSize ; j++){
                BinState toBin = l1_currentbins.get(j);

                // put items in queue first because moving immediately mutates the one we're iterating on
                for(Item item : fromBin.items){
                    if ( VNS.canMove(state, fromBin.bin, toBin.bin, item, true) ) toMove.addLast(item);
                }

                //move the ones stored in queue
                while(!toMove.isEmpty()){
                    Item item = toMove.pollFirst();
                    state.move(item, toBin.bin);
                }
            }
        }
        // identify empty bins and remove them
        VNS.removeEmptyBins(state);
        return state;
    }

    private static void removeEmptyBins(State state) {
        toRemove.clear();
        for(BinState bin : state.currentBins){
            if(bin.usedCapacity == 0){
                toRemove.addFirst(bin);
            }
        }
        while(!toRemove.isEmpty()){
            BinState bin = toRemove.pollFirst();
            state.currentBins.remove(bin);
            state.incomingBins.addFirst(bin);
        }
    }

    static Comparator<Swap> swapComparator = new Comparator<Swap>() {
        @Override
        public int compare(Swap o1, Swap o2) {
            int o1Val = o1.fitnessImprovement1();
            int o2Val = o2.fitnessImprovement1();
            if(o1Val>o2Val){
                return -1;
            }else if(o1Val<o2Val){
                return 1;
            }else{
                return 0;
            }
        }
    };

    static LinkedList<Swap> swapQueue = new LinkedList<Swap>();
    static HashSet<Integer> swappedBins = new HashSet<>();
    static State L2(State state){
        swapQueue.clear();
        swappedBins.clear();

        ArrayList<Item> all = Item.all;
        int size = all.size();
        for(int i=0 ; i<size ; i++){
            Item first = all.get(i);
            for(int j=i+1 ; j<size ; j++){
                Item second = all.get(j);
                Swap newSwap = new Swap(first, second, state);
                //todo: uncomment once verified fitnessImprovement() is working
//                if( !newSwap.shouldEnqueue()){
//                    continue;
//                }
                swapQueue.offer(newSwap);
            }
        }

        Collections.sort(swapQueue, swapComparator);



        while( !swapQueue.isEmpty() ){
            Swap curr = swapQueue.pollFirst();

            Bin firstBin = state.itemBinList.get(curr.first.id);
            Bin secondBin = state.itemBinList.get(curr.second.id);

            if( swappedBins.contains(firstBin.id) || swappedBins.contains(secondBin.id) ){
                continue;
            }


            if( curr.canSwap() ){
                //todo: remove once verified. stop using canSwap too.
                int origFitness = VNS.fitnessFunction(state);
                int fitnessDelta = curr.fitnessImprovement1();
                curr.performSwap();
                int val1 = VNS.fitnessFunction(state);
                int val2 = origFitness + fitnessDelta;
                assert val1==val2 : "Fitness function is wrong";
            }
            swappedBins.add(firstBin.id);
            swappedBins.add(secondBin.id);
        }

        swapQueue.clear();
        swappedBins.clear();
        return state;
    }

    static int N3GetBinCount(int binCount){
        double alpha = Utils.ALPHA;

        //round-down
        int max = (int)Math.ceil((alpha * binCount));

        // increment for round-up
        max+=1;
        int binsToBeEmptied = Utils.randIntRange(1, max);
        return binsToBeEmptied;
    }
    static RandomCollection<ShakingOperator> randomShaking = new RandomCollection<>();
    public ShakingOperator chooseNeighborhood(){
        randomShaking.clear();
        for(ShakingOperator op : this.shakings){
            randomShaking.add(op.score, op);
        }
        return randomShaking.next();
    }


    @Override
    public State pack() {
        int totalCtr = 0;
        int noImprovementCtr = 0;
        this.resetScores();
        ffcd.setState(state);
        State initState = ffcd.pack();
        State currentSolution = initState.clone();
        State best = initState;

        while(!VNS.shouldStop(totalCtr, noImprovementCtr)){
            ShakingOperator h = this.chooseNeighborhood();
            currentSolution = h.operate(currentSolution);

            currentSolution = VNS.L1(currentSolution);
            currentSolution = VNS.L2(currentSolution);
            if(VNS.hasImprovement(best, currentSolution)){
                noImprovementCtr = 0;
                h.score++;
                best = currentSolution;
            }else{
                noImprovementCtr++;
            }
//            if(currentSolution.currentBins.size() < min){
//                min = currentSolution.currentBins.size();
//            }
//            System.out.println(min);
            currentSolution = best.clone();
            totalCtr++;
        }
        return best;
    }

    private static boolean hasImprovement(State best, State currentSolution) {
        boolean hasImproved =  ((VNS.fitnessFunction(currentSolution) > VNS.fitnessFunction(best)) && (currentSolution.currentBins.size() <= best.currentBins.size()));
        return hasImproved;
    }


    private static boolean shouldStop(int totalCtr, int noImprovementCtr) {
        boolean response = (noImprovementCtr >= Utils.LAMBDA) || (totalCtr >= Utils.PSI);
        return response;
    }

    private void resetScores() {
        for(ShakingOperator op : this.shakings){
            op.score = 1;
        }
    }
}