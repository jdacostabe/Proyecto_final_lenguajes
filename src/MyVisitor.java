import org.antlr.v4.runtime.RuleContext;

import java.util.ArrayList;

public class MyVisitor<T> extends Python3BaseVisitor<T> {

    ArrayList<Python3Parser.TestContext> variables_assigned = new ArrayList<>();

    @Override public T visitFile_input(Python3Parser.File_inputContext ctx) {
        return visitChildren(ctx);
    }

    @Override public T visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        ctx.addChild(new RuleContext());
        //Verificar si es Asignacion
        if(ctx.ASSIGN().isEmpty() == false){

//            for (Python3Parser.TestContext variable_name:ctx.testlist_star_expr(0).test()) {
//                variables_assigned.add(variable_name);
//            }
        }

        //return visitChildren(ctx);
        return null;
    }



    public void print_variables_assigned(){
        for(Python3Parser.TestContext variable: variables_assigned){
            //System.out.println(variable.getText());
        }
    }


}
