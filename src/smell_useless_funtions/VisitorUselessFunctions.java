import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

public class VisitorUselessFunctions<T> extends Python3BaseVisitor<T> {

    ParseTree tree;
    String function;
    boolean is_used;

    VisitorUselessFunctions(ParseTree tree, String function){
        this.tree = tree;
        this.function = function;
        this.is_used = false;
        this.visit(this.tree);
    }

    @Override public T visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        if(ctx.trailer(0) != null && ctx.trailer().size() == 1) {
            if (ctx.atom().getText().equals(this.function)) {
                this.is_used = true;

            }
        }
        return null;
    }
}
