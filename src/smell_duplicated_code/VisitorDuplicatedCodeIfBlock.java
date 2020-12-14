import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;


public class VisitorDuplicatedCodeIfBlock<T> extends Python3BaseVisitor<T> {

    String final_text;
    String line_text;
    int line;
    String indent;
    int delete_blocks;
    int convert_else;
    VisitorDuplicatedCodeIfBlock(ParseTree tree){
        this.final_text     =   "";
        this.line           =   1;
        this.line_text      =   "";
        this.indent         =   "";
        this.delete_blocks  =   0;
        this.convert_else   =   0;
        this.visit(tree);
        final_text = final_text + line_text;
    }


    @Override public T visitIf_stmt(Python3Parser.If_stmtContext ctx) {


        String if_block     = ctx.suite(0).getText();
        String else_block   = (ctx.suite(ctx.ELIF().size()+1) != null)? ctx.suite(ctx.ELIF().size()+1).getText(): "pass";

        //IMPRIMIENDO TODOS LOS BLOQUES IGUALES
        boolean same_blocks = (if_block.trim().equals(else_block.trim()));

        for (int i =0; i<ctx.ELIF().size(); i++)
            if(!if_block.trim().equals(ctx.suite(i+1).getText().trim())) same_blocks = false;


        if(same_blocks){
            if(if_block.trim().equals("pass")) return null;

            this.delete_blocks = this.delete_blocks + 1;

            line = ((TerminalNodeImpl) ctx.IF()).symbol.getLine();
            visit(ctx.suite(0));

            this.delete_blocks = this.delete_blocks - 1;

            return null;
        }



        //IMPRIMIENDO IF - ELIF: CON POSIBLES BLOQUES IGUALES SEGUIDOS
        for(int i=0; i<ctx.ELIF().size()+1; i++){
            String actual_block = ctx.suite(i).getText();

            TerminalNodeImpl terminal_if_elif = (i==0)? (TerminalNodeImpl) ctx.IF() : (TerminalNodeImpl) ctx.ELIF(i-1);

            print_terminal(terminal_if_elif);
            print_code(ctx.test(i));

            int same_blocks_elif = 0;
            for(int j=i+1; j<ctx.ELIF().size()+1; j++){
                if(ctx.suite(j).getText().trim().equals(actual_block.trim())){
                    line = ((TerminalNodeImpl) ctx.ELIF(j-1)).symbol.getLine();
                    this.convert_else = this.convert_else + 4;
                    line_text = line_text + " or ";

                    print_code(ctx.test(j));
                    same_blocks_elif++;
                }
                else{
                    break;
                }
            }
            print_terminal((TerminalNodeImpl) ctx.COLON(i));
            i = i+same_blocks_elif;
            this.convert_else = this.convert_else - same_blocks_elif*4;
            visit(ctx.suite(i));
        }

        //PRINT ELSE
        if(!else_block.equals("pass")){
            line = ((TerminalNodeImpl) ctx.ELSE()).symbol.getLine();
            print_terminal((TerminalNodeImpl) ctx.ELSE());
            print_terminal((TerminalNodeImpl) ctx.COLON(ctx.ELIF().size()+1));
            visit(ctx.suite(ctx.ELIF().size()+1));
        }

        return null;
    }

    @Override public T visitStmt(Python3Parser.StmtContext ctx) {

        if(ctx.compound_stmt() != null && ctx.compound_stmt().funcdef() == null){
            return visitChildren(ctx);
        }

        print_code(ctx);
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


    private void print_code(ParserRuleContext ruleContext){
        for (int i=0; i<ruleContext.getChildCount(); i++) {
            if(ruleContext.getChild(i).getClass().equals(TerminalNodeImpl.class)){
                TerminalNodeImpl terminalNode = (TerminalNodeImpl) ruleContext.getChild(i);
                print_terminal(terminalNode);
            }
            else {
                print_code((ParserRuleContext) ruleContext.getChild(i));
            }
        }
    }

    private void print_terminal(TerminalNodeImpl terminalNode){
        int line_terminal = terminalNode.symbol.getLine();
        int column_terminal = terminalNode.symbol.getCharPositionInLine();

        if(line < line_terminal){
            final_text = final_text + line_text + "\n";
            final_text = (line+1 == line_terminal)? final_text : final_text + "\n";
            line_text="";
            line=line_terminal;
        }

        for(int j=line_text.length(); j<(column_terminal - (4*this.delete_blocks) + convert_else); j++) {
            line_text = line_text + " ";
        }

        line_text = line_text + terminalNode.symbol.getText().trim();
    }
}
