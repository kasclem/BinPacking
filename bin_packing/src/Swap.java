public class Swap {
    private final State state;
    public Item first;
    public Item second;

    // DeltaTo = New - Original
    // x2 = bin2 usedCapacity
    // y = itemOutW
    // z = itemInW
    // x1 = bin1 used capacity
    // DeltaTo = ((x2 - y + z)^2) - (x2^2)
    // DeltaTo = ((x2 - y + z)^2) - (x2^2)
    // DeltaTo = x2^2 - x2y + x2z - x2y + y^2 - yz + x2z - yz + z^2 - x2^2
    // DeltaTo = -2(x2)y + 2(x2)z - 2yz
    // DeltaFrom = -2(x1)z + 2(x1)y - 2yz
    // Total = -2(x2)y - 2(x1)z + 2(x2)z + 2(x1)y - 4yz


    public Swap(Item first, Item second, State parentState){
        this.first = first;
        this.second = second;
        this.state = parentState;
    }


    public void performSwap(){
        Bin firstBin = state.itemBinList.get(first.id);
        Bin secondBin = state.itemBinList.get(second.id);

        state.detachFromBin(first);
        state.detachFromBin(second);

        state.itemsToInsert.poll();
        state.itemsToInsert.poll();

        state.insert(secondBin, first);
        state.insert(firstBin, second);
    }


    //VNS swapping
    public boolean canSwap(){
        boolean response = true;
        if( fitnessImprovement1() <= 0 ){
            return false;
        }
        Bin firstBin = state.itemBinList.get(first.id);
        Bin secondBin = state.itemBinList.get(second.id);

        //detach first as swapping might fail if not done this first, because of incompatibility
        state.detachFromBin(first);
        state.detachFromBin(second);

        state.itemsToInsert.poll();
        state.itemsToInsert.poll();

        if( !state.canInsert(secondBin, first) ) response = false;
        if( !state.canInsert(firstBin, second)) response = false;

        // reinsert detached bins
        state.insert(firstBin, first);
        state.insert(secondBin, second);
        return response;
    }


    public int fitnessImprovement1(){
        BinState firstBin = state.getBinState(first.id);
        BinState secondBin = state.getBinState(second.id);
        
        // DeltaTo computation
        int x = secondBin.usedCapacity;
        int y = second.weight;
        int z = first.weight;
        int newUsed = x - y + z;
        int deltaTo = newUsed * newUsed - x * x;
        
        //DeltaFrom computation
        x = firstBin.usedCapacity;
        y = first.weight;
        z = second.weight;
        newUsed = x - y + z;
        int deltaFrom = newUsed * newUsed - x * x;
        
        int total = deltaFrom + deltaTo;
        return total;
    }

    public boolean shouldEnqueue() {
        return this.fitnessImprovement1() <= 0;
    }
}
