import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Bin {
	public static ArrayList<Bin> all = new ArrayList<Bin>();
	public static int lastId = -1;

	public int id;
	public int weightCapacity;

	public Bin() {
		this.id = Bin.all.size();
		Bin.all.add(this);
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

//	public int size() {
//		return this.items.size();
//	}
}
