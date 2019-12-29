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
    }
}
