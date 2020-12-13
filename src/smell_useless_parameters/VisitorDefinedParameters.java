import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;

public class VisitorDefinedParameters<T> extends Python3BaseVisitor<T> {

    ParseTree tree;
    public HashMap<String, ArrayList> internal_variables;
    public HashMap<String, ArrayList> parameters;
    String actual_Function;

    VisitorDefinedParameters(ParseTree tree){
        this.tree = tree;
        this.internal_variables = new HashMap<>();
        this.parameters = new HashMap<>();
        this.actual_Function = "";
        this.visit(this.tree);
    }

    @Override public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        this.actual_Function = ctx.NAME().getText();
        internal_variables.put(ctx.NAME().getText(),new ArrayList());
        parameters.put(ctx.NAME().getText(),new ArrayList());
        visitChildren(ctx);
        this.actual_Function = "";
        return null;
    }

    @Override public T visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        if(ctx.ASSIGN().isEmpty() == false && !this.actual_Function.equals("")){
            for (Python3Parser.TestContext variable_name:ctx.testlist_star_expr(0).test()) {
                ArrayList variables = this.internal_variables.get(this.actual_Function);
                if(!variables.contains(variable_name.getText())) {
                    variables.add(variable_name.getText());
                    this.internal_variables.put(this.actual_Function, variables);
                }
            }
        }
        return null;
    }

    @Override public T visitTypedargslist(Python3Parser.TypedargslistContext ctx) {
        if(!this.actual_Function.equals("")){
            for(int i=0;i<ctx.tfpdef().size();i++){
                ArrayList variables = this.parameters.get(this.actual_Function);
                if(!variables.contains(ctx.tfpdef(i).getText())) {
                    variables.add(ctx.tfpdef(i).getText());
                    this.parameters.put(this.actual_Function, variables);
                }

            }
        }
        return null;
    }
}
