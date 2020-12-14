import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;

public class VisitorFunctions<T> extends Python3BaseVisitor<T> {

    ParseTree tree;
    ArrayList<String> functions;

    VisitorFunctions(ParseTree tree){
        this.tree = tree;
        this.functions = new ArrayList<>();
        this.visit(this.tree);
    }

    @Override public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        this.functions.add(ctx.NAME().getText());
        return null;
    }
}