import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.HashMap;

public class VisitorFunctionPrinter<T> extends Python3BaseVisitor<T> {

    ArrayList<String> useless_funtions;

    ParseTree tree;

    String final_text;
    String line_text;
    int line;
    String indent;


    public VisitorFunctionPrinter(ParseTree tree, ArrayList<String> useless_funtions){
        this.useless_funtions = useless_funtions;

        this.tree = tree;

        final_text="";
        this.line = 1;
        this.line_text="";
        this.indent="";

        this.visit(this.tree);
    }

    @Override public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        this.final_text = this.final_text + "def " + ctx.NAME().getText() + "("+ ctx.parameters().typedargslist().getText()+ "):\n";

        this.indent = this.indent+"    ";
        visitChildren(ctx.suite());
        this.indent = this.indent.substring(0,this.indent.length()-4);

        return null;
    }

    @Override public T visitStmt(Python3Parser.StmtContext ctx) {

        if(ctx.compound_stmt() != null){
            if(ctx.compound_stmt().funcdef() != null && useless_funtions.contains(ctx.compound_stmt().funcdef().NAME().getText())) {
                return null;
            }else{
                return visitChildren(ctx);
            }
        }
        else{
            print_code(ctx);
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
                    final_text = (line_text.trim().equals(""))? final_text : final_text + line_text + "\n";
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
                    if((ctx.getChild(i).getText().equals("if")||ctx.getChild(i).getText().equals("elif")||ctx.getChild(i).getText().equals("else"))){
                        final_text =  final_text + indent;
                    }
                    final_text =  final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = indent + "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = indent.substring(0,indent.length()-4);
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
                    if(ctx.getChild(i).getText().equals("while")){
                        final_text =  final_text + indent;
                    }
                    final_text =  final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = indent + "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = indent.substring(0,indent.length()-4);
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
                    if(ctx.getChild(i).getText().equals("for")){
                        final_text =  final_text + indent;
                    }
                    final_text =  final_text + ctx.getChild(i).getText() + " ";
                }
            }else{
                indent = indent + "    ";
                visit(ctx.getChild(i));
                print = true;
                indent = indent.substring(0,indent.length()-4);
            }
        }
        return null;
    }
}
