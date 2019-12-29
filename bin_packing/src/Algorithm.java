
public abstract class Algorithm {
	protected State state;
	
	public abstract State pack();
	public void setState(State state) {
		this.state = state;
	}
}
