import org.antlr.runtime.tree.TreeWizard;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;

public class SmellUselessParameters implements Smeller {

    @Override public String fix(ParseTree tree) {
        VisitorDefinedParameters find_functions = new VisitorDefinedParameters(tree);

        HashMap<String,ArrayList<Integer>> parameters_not_used = new HashMap<>();
        HashMap<String, HashMap<String,Integer>> used_parameters = new HashMap<>();
        HashMap<String,ArrayList<String>> variables_not_used = new HashMap<>();
        HashMap<String, HashMap<String,Integer>> used_variables = new HashMap<>();
        ArrayList<String> all_parameters = new ArrayList<>();

        find_functions.parameters.forEach((key, value) -> {
            HashMap<String,Integer> used = new HashMap<>();
            ArrayList<Integer> parameters = new ArrayList<>();
            for(String parameter: (ArrayList<String>) value){
                VisitorUsedParameters used_parameter = new VisitorUsedParameters(tree, parameter, key.toString());
                if(!used_parameter.is_used)
                    parameters.add(((ArrayList<String>) value).indexOf(parameter));
                all_parameters.add(parameter);
                used.put(parameter,used_parameter.valid_uses);
            }
            used_parameters.put((String) key, used);
            parameters_not_used.put((String) key, parameters);
        });


//        parameters_not_used.forEach((key, value) -> {
//            System.out.println("Función: "+key+", Parametros: "+value.toString());;
//        });

//        used_parameters.forEach((key1, value1) -> {
//            System.out.println("Función: "+key1);
//            value1.forEach((key2, value2) -> {
//                System.out.println("Variable: "+key2+", Numero de usos: "+value2.toString());;
//            });
//        });

        find_functions.internal_variables.forEach((key, value) -> {
            HashMap<String,Integer> used = new HashMap<>();
            ArrayList<String> variables = new ArrayList<>();
            for(String variable: (ArrayList<String>) value){
                VisitorUsedFunctionVariables used_variable = new VisitorUsedFunctionVariables(tree, variable, key.toString());
                if(!used_variable.is_used)
                    variables.add(variable);
                used.put(variable,used_variable.used_definitions);
            }
            used_variables.put((String) key, used);
            variables_not_used.put((String) key, variables);
        });

//        variables_not_used.forEach((key, value) -> {
//            System.out.println("Función: "+key+", Variables: "+value.toString());;
//        });

        VisitorPrinter final_text = new VisitorPrinter(tree, parameters_not_used, used_parameters, variables_not_used, used_variables, all_parameters);

//        VisitorCodeWithoutVaribleAsignations final_text = new VisitorCodeWithoutVaribleAsignations(tree,variables_not_used, used_definitions);


        return final_text.final_text;
    }
}
