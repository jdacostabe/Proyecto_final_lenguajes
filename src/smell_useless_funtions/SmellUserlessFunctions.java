import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;

public class SmellUserlessFunctions implements Smeller {

    @Override public String fix(ParseTree tree) {
        VisitorFunctions find_functions = new VisitorFunctions(tree);
        ArrayList<String> not_used = new ArrayList<>();

        for(Object function : find_functions.functions){
            VisitorUselessFunctions used = new VisitorUselessFunctions(tree, (String)function);
            if(!used.is_used){
                not_used.add((String)function);
            }
        }

        VisitorFunctionPrinter final_text = new VisitorFunctionPrinter(tree,not_used);;

        return final_text.final_text;
    }

}
