import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class BinState {
    public Bin bin;
    public int usedCapacity = 0;
    public LinkedList<Item> items = new LinkedList<>();
	public Set<Integer> compatSet = new HashSet<Integer>();

	// todo: optimize, incoming bins may not need new items and compatSet yet
	public BinState(Bin bin){
	    this.bin = bin;
	    this.setCompatSetAll();
    }

    private void setCompatSetAll() {
		this.compatSet.clear();
		for(Category x : Category.all){
			compatSet.add(x.id);
		}
	}

    public int getBinMerit() {
	    int answer = this.bin.weightCapacity - 2 * this.usedCapacity;
	    return answer;
    }

    public int residualCapacity() {
	    return this.bin.weightCapacity - this.usedCapacity;
    }

    // weightOffset is used for when an item is bound to be inserted in the future. kind of like a 	//reservation
    public boolean canInsert(Item item, int reservedWeight){
		if (item.weight > (this.residualCapacity() - reservedWeight)){
			return false;
		}
		if (!this.compatSet.contains(item.c.id)) {
			return false;
		}
		return true;
	}

	public boolean canInsert(Item item){
		return this.canInsert(item, 0);
	}

    public boolean insert(Item item) {
		// todo: optimize check should be outside of insert
		if ( !canInsert(item) ) return false;

		this.items.addLast(item);

		//intersection
		compatSet.retainAll(item.c.compListAsCollection());
		this.usedCapacity += item.weight;
		return true;
	}

    public void remove(Item item) {
		this.items.remove(item);
		this.usedCapacity -= item.weight;
		this.updateCompatSet();
	}

    //todo: there's a better way here. try storing category in set instead of compatible categories
    private void updateCompatSet() {
		//refresh compatSet:
		this.compatSet.clear();
		for (Category c : Category.all){
			this.compatSet.add(c.id);
		}

		for (Item item : this.items){
			this.compatSet.retainAll(item.c.compListAsCollection());
		}
	}

	@Override
	public String toString() {
//		return String.format("[%d, %d, %s]", this.bin.weightCapacity, this.getBinMerit(), this.items);
		return String.format("[%d, %s]", this.bin.weightCapacity, this.items);
	}

	public int size() {
		return this.items.size();
	}

	public BinState clone(){
		BinState newBS = new BinState(this.bin);
		newBS.usedCapacity = this.usedCapacity;
		newBS.items = new LinkedList<>(this.items);
		newBS.compatSet = new HashSet<>(this.compatSet);
		return newBS;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BinState binState = (BinState) o;

		return this.bin.id == binState.bin.id;
	}

	@Override
	public int hashCode() {
		return bin.id;
	}
}
