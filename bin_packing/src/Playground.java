import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Playground {
    public static void main(String[] args){
        smallVNS();
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

    static void smallFFCD(){
        State state = State.generateState2();
        Runner.run(new FFCD(), state);
    }

    static void smallABFD(){
        State state = State.generateState2();
        Runner.run(new ABFD(), state);
    }

    static void smallVNS(){
        State state = State.generateState2();
        Runner.run(new VNS(), state);
    }

    private static void writeUsingFileWriter(String data) {
        File file = new File(String.format("serializedFiles/%s", "Formatted.txt"));
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write("Start");
            fr.append("Hello");
            fr.append("Hello");
            fr.append("Hi");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}