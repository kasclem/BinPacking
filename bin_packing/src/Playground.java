public class Playground {
    public static void main(String[] args){
        ex1();
    }

    //fitnessFunctionTry
    //todo verify that fitness function works
    static void ex1(){
        State state = State.generateState1();
        Item first = state.itemsToInsert.get(0);
        Item second = state.itemsToInsert.get(1);

        Algorithm ffcd = new FFCD();
        ffcd.setState(state);
        ffcd.pack();

        Swap swap = new Swap(first, second, state);
        System.out.println(swap.fitnessImprovement1());
    }
}