import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SmellUselessParameters implements Smeller {

    @Override public String fix(ParseTree tree) {
        VisitorDefinedParameters find_functions = new VisitorDefinedParameters(tree);

        HashSet<String> parameters_not_used = new HashSet<>();
        HashMap<String, Integer> used_parameters = new HashMap<>();
        HashSet<String> variables_not_used = new HashSet<>();
        HashMap<String, Integer> used_variables = new HashMap<>();

        find_functions.parameters.forEach((key, value) -> {
            for(String variable: (ArrayList<String>) value){
                VisitorUsedParameters used_variable = new VisitorUsedParameters(tree, variable, key.toString());
                if(!used_variable.is_used)
                    parameters_not_used.add(variable);
                used_parameters.put(variable,used_variable.used_definitions);
            }
        });


        find_functions.internal_variables.forEach((key, value) -> {
            for(String variable: (ArrayList<String>) value){
                VisitorUsedParameters used_variable = new VisitorUsedParameters(tree, variable, key.toString());
                if(!used_variable.is_used)
                    variables_not_used.add(variable);
                used_variables.put(variable,used_variable.used_definitions);
            }
        });



        return "\nFIN PROVISIONAL";
    }
}
