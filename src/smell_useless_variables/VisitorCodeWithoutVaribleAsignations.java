import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.HashSet;

public class VisitorCodeWithoutVaribleAsignations<T> extends Python3BaseVisitor<T> {

    ParseTree tree;
    HashSet<String> variables_name;
    String final_text;
    String line_text;
    int line;
    String indent;
    boolean inCompound;

    public VisitorCodeWithoutVaribleAsignations(ParseTree tree, HashSet<String> variables_name){
        this.tree = tree;
        this.variables_name = variables_name;
        this.final_text = "";
        this.line = 1;
        this.line_text="";
        this.indent="";
        this.visit(this.tree);
    }

    @Override public T visitStmt(Python3Parser.StmtContext ctx) {

        if(ctx.compound_stmt() != null && ctx.compound_stmt().funcdef() == null){
            return visitChildren(ctx);
        }

        if( ctx.simple_stmt() != null &&
            ctx.simple_stmt().small_stmt() != null &&
            ctx.simple_stmt().small_stmt().get(0) != null &&
            ctx.simple_stmt().small_stmt().get(0).expr_stmt() != null && (
            ctx.simple_stmt().small_stmt().get(0).expr_stmt().ASSIGN().size() > 0 ||
            ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign() != null )
          ){
            Python3Parser.Expr_stmtContext expr_stmtContext = ctx.simple_stmt().small_stmt().get(0).expr_stmt();
            Python3Parser.Testlist_star_exprContext variable_list = expr_stmtContext.testlist_star_expr(0);

            String left_part = "";
            String center_part = (ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign() != null) ? ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign().getText() : "=";
            String right_part = "";

            for (int i=0; i<variable_list.test().size(); i++) {
                Python3Parser.TestContext variable_in_expre = variable_list.test(i);

                if(!variables_name.contains(variable_in_expre.getText())){
                    left_part = (left_part.equals("")) ? variable_in_expre.getText() : left_part+","+variable_in_expre.getText();

                    if(center_part.equals("=")){
                        right_part = (right_part.equals("")) ? expr_stmtContext.testlist_star_expr(1).test(i).getText() : right_part + "," + expr_stmtContext.testlist_star_expr(1).test(i).getText();
                    }else{
                        right_part = (right_part.equals("")) ? expr_stmtContext.testlist().test(i).getText() : right_part + "," + expr_stmtContext.testlist().test(i).getText();
                    }
                }
            }

            if(!left_part.equals("")){
                line_text="";
                final_text = final_text + indent + left_part + center_part + right_part +"\n";
            }
            line++;
        }
        else{
            if(!indent.equals("")){
                final_text = final_text + indent + ctx.getText().trim() + "\n";
            }else{
                print_code(ctx);
            }
        }

        return null;
    }

    private void print_code(ParserRuleContext ruleContext){
        for (int i=0; i<ruleContext.getChildCount(); i++) {
            if(ruleContext.getChild(i).getClass().equals(TerminalNodeImpl.class)){
                TerminalNodeImpl terminalNode = (TerminalNodeImpl) ruleContext.getChild(i);

                int line_terminal = terminalNode.symbol.getLine();
                int column_terminal = terminalNode.symbol.getCharPositionInLine();

                if(line < line_terminal){
                    final_text = final_text + line_text + "\n";
                    final_text = (line+1 == line_terminal)? final_text : final_text + "\n";
                    line_text="";
                    line=line_terminal;
                }

                for(int j=line_text.length(); j<column_terminal; j++) {
                    line_text = line_text + " ";
                }

                line_text = line_text + terminalNode.symbol.getText().trim();
            }
            else {
                print_code((ParserRuleContext) ruleContext.getChild(i));
            }
        }
    }

    @Override public T visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        boolean print = true;
        for(int i=0; i<ctx.getChildCount(); i++){
            if(print){
                if(ctx.getChild(i).getText().equals(":")){
                    final_text = final_text.substring(0,final_text.length()-1) + ":\n";
                    print = false;
                    line++;
                }else {
                    final_text = final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = "";
            }
        }
        return null;
    }

    @Override public T visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        boolean print = true;
        for(int i=0; i<ctx.getChildCount(); i++){
            if(print){
                if(ctx.getChild(i).getText().equals(":")){
                    final_text = final_text.substring(0,final_text.length()-1) + ":\n";
                    print = false;
                    line++;
                }else {
                    final_text = final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = "";
            }
        }
        return null;
    }

    @Override public T visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        boolean print = true;
        for(int i=0; i<ctx.getChildCount(); i++){
            if(print){
                if(ctx.getChild(i).getText().equals(":")){
                    final_text = final_text.substring(0,final_text.length()-1) + ":\n";
                    print = false;
                    line++;
                }else {
                    final_text = final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = "";
            }
        }
        return null;
    }
}
