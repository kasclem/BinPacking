public abstract class ShakingOperator {
    public int score;
    public String name;

    public ShakingOperator(String name){
        this.name = name;
    }
    abstract State operate(State state);

    @Override
    public String toString() {
        return this.name;
    }
}