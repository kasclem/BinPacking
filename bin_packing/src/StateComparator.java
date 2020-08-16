import java.util.Comparator;

public abstract class StateComparator<E> implements Comparator<E> {
    public State state;

    public void setState(State state){
        this.state = state;
    }
}
