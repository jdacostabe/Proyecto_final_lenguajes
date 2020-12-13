import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashSet;

public class VisitorUsedVariable<T> extends Python3BaseVisitor<T> {

    ParseTree tree;
    String variable_name;
    Boolean is_used;
    Boolean is_defined;
    int used_definitions;
    int unused_definitions;

    public VisitorUsedVariable(ParseTree tree, String variable_name){
        this.tree               = tree;
        this.variable_name      = variable_name;
        this.is_used            = false;
        this.is_defined         = false;
        this.used_definitions   = 0;
        this.unused_definitions = 0;
        this.visit(this.tree);
    }


    @Override public T visitAtom(Python3Parser.AtomContext ctx) {
        if(ctx.NAME() != null && ctx.getText().equals(this.variable_name)){
            Boolean isLeftPart = false;
            RuleContext parent = ctx.parent;
            while(!isLeftPart && !parent.getClass().equals(Python3Parser.File_inputContext.class)){

                if(parent.getClass().equals(Python3Parser.ComparisonContext.class) && ((Python3Parser.ComparisonContext) parent).comp_op().size()!=0){
                    break;
                }

                if(parent.getClass().equals(Python3Parser.Expr_stmtContext.class)){
                    Python3Parser.Expr_stmtContext expr_stmt_ctx = (Python3Parser.Expr_stmtContext) parent;

                    for (Python3Parser.TestContext left_variable_name:expr_stmt_ctx.testlist_star_expr(0).test()) {
                        if (left_variable_name.getText().equals(variable_name)) {
                            isLeftPart = true;
                            this.is_defined = true;
                            this.unused_definitions++;
                        }
                    }

                    while(!parent.getClass().equals(Python3Parser.File_inputContext.class)){
                        if(parent.parent.getClass().equals(Python3Parser.Compound_stmtContext.class) && parent.getClass().equals(Python3Parser.FuncdefContext.class)){
                            isLeftPart = true;
                            break;
                        }
                        parent = parent.parent;
                    }
                    break;
                }
                parent = parent.parent;
            }

            if(!isLeftPart && this.is_defined){
                this.is_used = true;
                this.used_definitions = this.used_definitions + this.unused_definitions;
                this.unused_definitions = 0;
                return null;
            }
        }

        return visitChildren(ctx);
    }
}
