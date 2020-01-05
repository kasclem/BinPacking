import java.util.ArrayList;
import java.util.Objects;

public class Item {
	public static ArrayList<Item> all = new ArrayList<Item>();
	public static int lastId = -1;
	public int id;
	public Category c;
	public int weight;

	public Item() {
		this.id = Item.all.size();
		Item.all.add(this);
	}

    public static Item createItem(int weight, int category) {
		Item item = new Item();
		item.c = Category.all.get(category);
		item.weight = weight;
		item.c.members.add(item);
		return item;
    }

	public static Item generateRandom() {
		Item item = new Item();
		item.weight = Utils.randIntRange(Utils.MIN_ITEM_WEIGHT, Utils.MAX_ITEM_WEIGHT+1);
		return item;
	}

	@Override
	public String toString() {
		return String.format("(I, %s, %d)", this.c, this.weight);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Item item = (Item) o;

		return id == item.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
