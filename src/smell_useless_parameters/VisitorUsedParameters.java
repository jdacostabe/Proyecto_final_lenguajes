import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class VisitorUsedParameters<T> extends Python3BaseVisitor<T>  {

    ParseTree tree;
    String variable_name;
    String actual_Function;
    String function;
    public boolean is_used;
    boolean is_defined;
    int used_definitions;
    int unused_definitions;

    VisitorUsedParameters(ParseTree tree, String variable_name,String actual_Function){
        this.tree = tree;
        this.variable_name = variable_name;
        this.actual_Function = actual_Function;
        this.function = "";
        this.is_used = false;
        this.visit(this.tree);
    }

    @Override public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        if(this.actual_Function.equals(ctx.NAME().getText())){
            function = actual_Function;
            visitChildren(ctx);
            function = "";
        }
        return null;
    }

    @Override public T visitAtom(Python3Parser.AtomContext ctx) {
        if(ctx.NAME() != null && ctx.getText().equals(variable_name) && actual_Function.equals(function)){
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
