import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class VisitorUsedParameters<T> extends Python3BaseVisitor<T>  {

    ParseTree tree;
    String variable_name;
    String actual_Function;
    String function;

    public boolean is_used;
    boolean not_even_used;
    int valid_uses;

    VisitorUsedParameters(ParseTree tree, String variable_name,String actual_Function){
        this.tree = tree;
        this.variable_name = variable_name;
        this.actual_Function = actual_Function;
        this.function = "";
        this.is_used = false;
        this.not_even_used = false;
        this.valid_uses = 0;
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
        if(ctx.NAME() != null && ctx.getText().equals(variable_name) && actual_Function.equals(function) && !not_even_used){
            Boolean useless = false;
            RuleContext parent = ctx.parent;
            while(!useless && !parent.getClass().equals(Python3Parser.File_inputContext.class)){

                if(parent.getClass().equals(Python3Parser.ComparisonContext.class) && ((Python3Parser.ComparisonContext) parent).comp_op().size()!=0){
                    break;
                }

                if(parent.getClass().equals(Python3Parser.Expr_stmtContext.class)){
                    Python3Parser.Expr_stmtContext expr_stmt_ctx = (Python3Parser.Expr_stmtContext) parent;

                    for(int i=0;i<expr_stmt_ctx.testlist_star_expr(1).test().size();i++){
                        //Si está a la izquierda y, esto se iguala por si mismo a la derecha o no se a usa si misma en la parte derecha
                        if (expr_stmt_ctx.testlist_star_expr(0).test(i).getText().equals(variable_name)) {
                            if(expr_stmt_ctx.testlist_star_expr(0).test(i).getText().equals(expr_stmt_ctx.testlist_star_expr(1).test(i).getText())){
                                useless = true;
                            }else if(!on_right(expr_stmt_ctx.testlist_star_expr(1).test(i))){
                                this.not_even_used = true;
                                return null;
                            }
                        }
                    }
                    break;
                }
                parent = parent.parent;
            }

            if(!useless){
                this.is_used = true;
                this.valid_uses++;
                return null;
            }
        }

        return visitChildren(ctx);
    }




    private boolean on_right(ParserRuleContext ruleContext){
        boolean res = false;
        for (int i=0; i<ruleContext.getChildCount(); i++) {
            if(ruleContext.getChild(i).getClass().equals(TerminalNodeImpl.class)){
                res = res || ruleContext.getChild(i).getText().equals(variable_name);
            }
            else {
                res = res || on_right((ParserRuleContext) ruleContext.getChild(i));
            }
        }
        return res;
    }
}
