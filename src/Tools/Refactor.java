import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileWriter;
import java.io.IOException;

public class Refactor {

    ArrayList<Smeller> smellers;
    String file_name;

    Refactor(String file_name){
        this.file_name = file_name;
        this.smellers = new ArrayList<>();
        this.smellers.add(new SmellUselessVariables());

    }

    public void codeRefactor() throws IOException {


        for(Smeller smeller : smellers){
            Python3Lexer lexer = new Python3Lexer(CharStreams.fromFileName(file_name));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Python3Parser parser = new Python3Parser(tokens);
            ParseTree tree = parser.file_input();

            String refactored_code = smeller.fix(tree);

            System.out.println(refactored_code);
        }



//        try {
//            FileWriter writer = new FileWriter(file_name);
//            writer.write(final_text);
//            writer.close();
//        }catch(IOException e){
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
    }
}
