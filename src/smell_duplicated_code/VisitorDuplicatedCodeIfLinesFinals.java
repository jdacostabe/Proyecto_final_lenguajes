import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;


public class VisitorDuplicatedCodeIfLinesFinals<T> extends Python3BaseVisitor<T>  {
    String final_text;
    String line_text;
    int line;
    String indent;
    int delete_blocks;
    int convert_else;

    public VisitorDuplicatedCodeIfLinesFinals(ParseTree tree){
        this.final_text = "";
        this.line = 1;
        this.line_text="";
        this.indent="";
        this.delete_blocks = 0;
        this.visit(tree);
        this.convert_else = 0;
        final_text = final_text + line_text;

    }
    @Override public T visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        //CONTANDO LINEAS FINALES COMUNES
        int same_final_lines = 0;

        for(int num_actual_line=0; num_actual_line<ctx.suite(0).stmt().size(); num_actual_line++){
            String actual_line = ctx.suite(0).stmt(ctx.suite(0).stmt().size()-1-num_actual_line).getText().trim();

            boolean same_actual_line = true;
            for(int num_suite = 1; num_suite<ctx.suite().size(); num_suite++){

                if(ctx.suite(num_suite).stmt(ctx.suite(num_suite).stmt().size() -1 - num_actual_line) == null){
                    same_actual_line = false;
                    break;
                }

                if(!ctx.suite(num_suite).stmt(ctx.suite(num_suite).stmt().size() -1 - num_actual_line).getText().trim().equals(actual_line)){
                    same_actual_line = false;
                    break;
                }
            }

            if((ctx.ELSE() != null )  && (same_actual_line)){
                same_final_lines++;
            }
        }




        //IMPRIMIENDO BLOQUE IF - SIN LINEAS FINALES COMUNES
        boolean print_block_if;
        if(ctx.suite(0).stmt().size() - same_final_lines > 0){
            print_block_if = true;
            print_terminal((TerminalNodeImpl) ctx.IF());
            print_code(ctx.test(0));
            print_terminal((TerminalNodeImpl) ctx.COLON(0));

            for(int i=0; i<ctx.suite(0).stmt().size()-same_final_lines; i++)
                visit(ctx.suite(0).stmt(i));
            line = line + same_final_lines;
        }
        else {
            print_block_if = false;
        }

        //IMPRIMIENDO BLOQUES ElIF - SIN LINEAS FINALES COMUNES
        for(int num_elif=0; num_elif<ctx.ELIF().size(); num_elif++){
            if(ctx.suite(num_elif+1).stmt().size() - same_final_lines > 0){

                line = ((TerminalNodeImpl) ctx.ELIF(num_elif)).symbol.getLine() -1 ;
                if(!print_block_if){
                    print_block_if = true;
                    print_terminal((TerminalNodeImpl) ctx.IF());
                    line_text = line_text + " " + ctx.test(num_elif+1).getText() + ":";

                }
                else{
                    print_terminal((TerminalNodeImpl) ctx.ELIF(num_elif));
                    print_code(ctx.test(num_elif+1));
                    print_terminal((TerminalNodeImpl) ctx.COLON(num_elif+1));
                }
                line = ((TerminalNodeImpl) ctx.ELIF(num_elif)).symbol.getLine();
                for(int i=0; i<ctx.suite(num_elif+1).stmt().size()-same_final_lines; i++)
                    visit(ctx.suite(num_elif+1).stmt(i));
                line = line + same_final_lines;
            }
            else{
                line = ((TerminalNodeImpl) ctx.ELIF(num_elif)).symbol.getLine();
                line = line + same_final_lines;
            }
        }

        //IMPRIMENDO BLOQUE ELSE - SIN LINEAS FINALES COMUNES
        if((ctx.ELSE() != null) && (ctx.suite(ctx.ELIF().size()+1).stmt().size()-same_final_lines >0)){
            line = ((TerminalNodeImpl) ctx.ELSE()).symbol.getLine() -1;
            if(!print_block_if){
                print_terminal((TerminalNodeImpl) ctx.IF());
                convert_else = convert_else + 4;
                line_text = line_text + " not(";
                print_code(ctx.test(0));

                for(int num_elif=0; num_elif<ctx.ELIF().size(); num_elif++){
                    line_text = line_text + " or ";
                    convert_else = convert_else + 4;
                    print_code(ctx.test(num_elif+1));
                }
                line_text = line_text + "):";
                convert_else = convert_else - ctx.ELIF().size()*4;
                convert_else = convert_else - 4;
            }
            else {
                print_terminal((TerminalNodeImpl) ctx.ELSE());
                print_terminal((TerminalNodeImpl) ctx.COLON(ctx.ELIF().size()+1));
            }

            line = ((TerminalNodeImpl) ctx.ELSE()).symbol.getLine();
            for(int i=0; i<ctx.suite(ctx.ELIF().size()+1).stmt().size()-same_final_lines; i++)
                visit(ctx.suite(ctx.ELIF().size()+1).stmt(i));
        }
        else if((ctx.ELSE() != null)){
            line = ((TerminalNodeImpl) ctx.ELSE()).symbol.getLine() + 1 ;
        }

        this.delete_blocks++;
        for(int i = 0; i<same_final_lines; i++){
            visit(ctx.suite(ctx.ELIF().size()+1).stmt(ctx.suite(ctx.ELIF().size()+1).stmt().size()-i-1));
        }
        this.delete_blocks--;

        //IMPRIMIENDO LINEAS INICIALES COMUNES
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

        for(int j=line_text.length(); j<(column_terminal - (4*this.delete_blocks) + convert_else ); j++) {
            line_text = line_text + " ";
        }

        line_text = line_text + terminalNode.symbol.getText().trim();


    }
}

