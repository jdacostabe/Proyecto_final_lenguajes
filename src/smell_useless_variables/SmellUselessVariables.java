import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.HashSet;

public class SmellUselessVariables implements Smeller {

    public String fix(ParseTree tree){
        VisitorCollectVariables visitor_collector_variables = new VisitorCollectVariables(tree);

        HashSet<String> variables_in_code = visitor_collector_variables.variables_assigned;

        HashSet<String> variables_not_used = new HashSet<>();

        for (String variable: variables_in_code) {
            VisitorUsedVariable visitor_used_variable = new VisitorUsedVariable(tree, variable);
            if(!visitor_used_variable.is_used)
                variables_not_used.add(variable);
        }

        VisitorCodeWithoutVaribleAsignations final_text = new VisitorCodeWithoutVaribleAsignations(tree,variables_not_used);

        return final_text.final_text;
    }









}

