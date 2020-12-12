import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws Exception {
        try{
            Refactor refactor = new Refactor(args[0]);
            refactor.codeRefactor();
        } catch (Exception e){
            System.err.println("Error (Test): " + e);
        }
    }
}
