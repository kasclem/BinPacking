import java.util.ArrayList;

public class Category {
	public static ArrayList<Category> all = new ArrayList<Category>();
	public int id;
	public ArrayList<Category> compatible = new ArrayList<Category>();
	public ArrayList<Item> members = new ArrayList<Item>();
	
	public Category(int id) {
		this.id = id;
	}
	
	public static Category getRandomCategory() {
		int x = Utils.randIntRange(0, Category.all.size());
		return Category.all.get(x);
	}
	
	public static void init(int[][] table) {
		for(int i=0 ; i<table.length ; i++) {
			Category c = new Category(i);
			Category.all.add(c);
		}
		for(int i=0 ; i<table.length ; i++) {
			int[] row = table[i];
			Category curr = Category.all.get(i);
			for(int j=0 ; j<row.length ; j++) {
				int curr_int = row[j];
				if (curr_int == 0) continue;
				Category toAdd = Category.all.get(j);
				curr.compatible.add(toAdd);
			}
		}
	}
	
	public ArrayList<Integer> compListAsCollection(){
		ArrayList<Integer> x = new ArrayList<Integer>();
		for(Category c : this.compatible) {
			x.add(c.id);
		}
		return x;
	}

	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
