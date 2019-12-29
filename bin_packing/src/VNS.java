import java.util.*;

public class VNS extends Algorithm {

    static Algorithm ffcd = new FFCD();

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
        Bin bin = i.bin;
        if (bin.size() <= 1){
            state.currentBins.remove(bin);
            state.incomingBins.addFirst(bin);
        }
        i.detachFromBin();
        state.itemsToInsert.addFirst(i);
    }

    static ShakingOperator n2 = new ShakingOperator("N2") {
        @Override
        public State operate(State state) {
            int index = Utils.randIntRange(0, Category.all.size());
            Category selected = Category.all.get(index);
            for(Item i : selected.members){
                VNS.detachItemFromBin(i, state);

                //todo: remove when finish debugging
                System.out.println();
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
            for(Bin bin : toRemove){
                VNS.emptyBin(state, bin);
            }
            ffcd.setState(state);
            State response = ffcd.pack();
            return response;
        }
    };
    ShakingOperator[] shakings = new ShakingOperator[]{n1, n2, n3};

    // sort by increasing used capacity
    static Comparator<Bin> l1_comparator = new Comparator<Bin>() {
        @Override
        public int compare(Bin o1, Bin o2) {
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
    static boolean canMove(Bin from, Bin to, Item item){
        /*
        the residual capacity of bin iB after the move is smaller than the
        residual capacity of bin iA before the move;
        */
        boolean cond1 = ( to.residualCapacity()-item.weight ) < from.residualCapacity();
        if (!cond1) return false;
        if (!to.canInsert(item)) return false;
        return true;
    }

    static void move(Item item, Bin to, State state){
        item.detachFromBin();
        state.insert(to, item);
    }

    //objective score
    static int fitnessFunction(State state){
        int score = 0;
        for( Bin bin : state.currentBins ){
            score += bin.usedCapacity * bin.usedCapacity;
        }
        return score;
    }

    //1.) sort bins by descending residual capacity
    static LinkedList<Item> toMove = new LinkedList<>();
    static LinkedList<Bin> toRemove = new LinkedList<>();
    static State L1(State state){
        // todo: maybe, this is not needed here:
        toMove.clear();
        state.currentBins.sort(l1_comparator);
        int binSize = state.currentBins.size();
        for(int i=0 ; i<binSize ; i++){
            Bin fromBin = state.currentBins.get(i);
            for(int j=i+1 ; j<binSize ; j++){
                Bin toBin = state.currentBins.get(j);

                // put items in queue first because moving immediately mutates the one we're iterating on
                for(Item item : fromBin.items){
                    if ( VNS.canMove(fromBin, toBin, item) ) toMove.addLast(item);
                }

                //move the ones stored in queue
                while(!toMove.isEmpty()){
                    Item item = toMove.pollFirst();
                    VNS.move(item, toBin, state);
                }
            }
        }
        // identify empty bins and remove them
        VNS.removeEmptyBins(state);
        return state;
    }

    private static void removeEmptyBins(State state) {
        toRemove.clear();
        for(Bin bin : state.currentBins){
            if(bin.usedCapacity == 0){
                toRemove.addFirst(bin);
            }
        }
        while(!toRemove.isEmpty()){
            Bin bin = toRemove.pollFirst();
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
            }else{
                return 1;
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
        try{
            Collections.sort(swapQueue, swapComparator);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }


        while( !swapQueue.isEmpty() ){
            Swap curr = swapQueue.pollFirst();
            Bin firstBin = curr.first.bin;
            Bin secondBin = curr.second.bin;
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

    static void emptyBin(State state, Bin bin){
        state.itemsToInsert.addAll(bin.items);
        state.clearBin(bin);
        state.currentBins.remove(bin);
        state.incomingBins.addFirst(bin);
    }

    static int N3GetBinCount(int binCount){
        double alpha = Utils.ALPHA;

        //round-down
        int max = (int)(alpha * binCount);

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

    //todo: make sure empty bins are brought back to incoming bins once packing is over
    @Override
    public State pack() {
        int totalCtr = 0;
        int noImprovementCtr = 0;
        this.resetScores();
        ffcd.setState(state);
        State initState = ffcd.pack();
        State currentSolution = Utils.c.deepClone(initState);
        State best = initState;

        while(!VNS.shouldStop(totalCtr, noImprovementCtr)){
            ShakingOperator h = this.chooseNeighborhood();

            //todo: remove after debug
            h = VNS.n2;

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
            currentSolution = Utils.c.deepClone(best);
            totalCtr++;
        }
        return this.state;
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