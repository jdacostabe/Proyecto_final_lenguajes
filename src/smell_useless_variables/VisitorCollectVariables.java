import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.HashSet;

public class VisitorCollectVariables<T> extends Python3BaseVisitor<T> {
    ParseTree tree;

    public HashSet<String> variables_assigned;

    public VisitorCollectVariables(ParseTree tree){
        this.tree = tree;
        this.variables_assigned = new HashSet<>();
        this.visit(this.tree);
    }

    public HashSet<String> getVariablesAssigned(){
        return variables_assigned;
    }

    @Override public T visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        if(ctx.ASSIGN().isEmpty() == false){

            for (Python3Parser.TestContext variable_name:ctx.testlist_star_expr(0).test()) {
                variables_assigned.add(variable_name.getText());
            }
        }
        return null;
    }

}
