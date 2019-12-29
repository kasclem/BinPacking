import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Bin {
	public static ArrayList<Bin> all = new ArrayList<Bin>();
	public static int lastId = -1;

	public int id;
	public int weightCapacity;
	public int usedCapacity;
	public LinkedList<Item> items = new LinkedList<>();
	public Set<Integer> compatSet = new HashSet<Integer>();

	public Bin() {
		this.id = Bin.all.size();
		Bin.all.add(this);
		this.setCompatSetAll();
	}

	private void setCompatSetAll() {
		this.compatSet.clear();
		for(Category x : Category.all){
			compatSet.add(x.id);
		}
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Bin)){
			return false;
		}
		Bin obj2 = (Bin) obj;
		return obj2.id == this.id;
	}

	public static Bin createBin(int weightCapacity){
		Bin bin = new Bin();
		bin.weightCapacity = weightCapacity;
		return bin;
	}

	public static Bin generateRandom() {
		Bin bin = new Bin();
		bin.weightCapacity = Utils.chooseFrom(Utils.WEIGHT_CHOICES);
		return bin;
	}

	public void remove(Item item) {
		this.items.remove(item);
		this.usedCapacity -= item.weight;
		this.updateCompatSet();
	}

	public int residualCapacity(){
		return this.weightCapacity - this.usedCapacity;
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

	// warn: should only be used by state.insert
	public boolean insert(Item item) {
		// todo: optimize check should be outside of insert
		if ( !canInsert(item) ) return false;

		this.items.addLast(item);

		//intersection
		compatSet.retainAll(item.c.compListAsCollection());
		this.usedCapacity += item.weight;
		item.bin = this;
		return true;
	}

	public boolean canInsert(Item item){
		if (item.weight > (this.weightCapacity - this.usedCapacity)){
			return false;
		}
		if (!this.compatSet.contains(item.c.id)) {
			return false;
		}
		return true;
	}

	public int getBinMerit(){
		return this.weightCapacity - 2 * this.usedCapacity;
	}

	@Override
	public String toString() {
		return String.format("[%d, %s]", this.weightCapacity, this.items);
	}

	public void clear() {
		while(!items.isEmpty()){
			Item i = items.getFirst();
			i.detachFromBin();
		}
	}

	public int size() {
		return this.items.size();
	}
}
