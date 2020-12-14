import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class VisitorPrinter<T> extends Python3BaseVisitor<T> {

    HashMap<String,ArrayList<Integer>> parameters_not_used;
    HashMap<String, HashMap<String,Integer>> used_parameters;
    HashMap<String,ArrayList<String>> variables_not_used;
    HashMap<String, HashMap<String,Integer>> used_variables;
    ArrayList<String> all_parameters;

    ParseTree tree;
    String final_text;

    String actual_function;
    ArrayList<String> actual_parameters_useless;
    ArrayList<String> actual_variables_useless;

    String line_text;
    int line;
    String indent;


    public VisitorPrinter(ParseTree tree, HashMap<String,ArrayList<Integer>> parameters_not_used, HashMap<String, HashMap<String,Integer>> used_parameters, HashMap<String,ArrayList<String>> variables_not_used, HashMap<String, HashMap<String,Integer>> used_variables, ArrayList<String> all_parameters){
        this.parameters_not_used = parameters_not_used;
        this.used_parameters = used_parameters;
        this.variables_not_used = variables_not_used;
        this.used_variables = used_variables;
        this.all_parameters = all_parameters;

        this.tree = tree;
        this.final_text = "";

        this.actual_function = "";
        this.actual_parameters_useless = new ArrayList<>();
        this.actual_variables_useless = new ArrayList<>();

        this.line = 1;
        this.line_text="";
        this.indent="";

        this.visit(this.tree);
    }

    @Override public T visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        if(ctx.trailer(0) != null && ctx.trailer().size() == 1){
            this.final_text = final_text + this.indent + ctx.atom().getText() + "(";
            ArrayList<Integer> indexes = parameters_not_used.get(ctx.atom().getText());

            if(ctx.trailer(0).arglist() != null && ctx.trailer(0).arglist().argument() != null) {
                if (indexes == null || indexes.size() == 0) {
                    this.final_text = final_text + ctx.trailer(0).arglist().getText();
                } else if (indexes != null && !(indexes.size() == ctx.trailer(0).arglist().argument().size())) {
                    for (int i = 0; i < ctx.trailer(0).arglist().argument().size(); i++) {
                        this.final_text = (indexes.contains(i)) ? final_text : final_text + ctx.trailer(0).arglist().argument(i).getText() + ",";
                    }
                    this.final_text = this.final_text.substring(0, this.final_text.length() - 1);
                }
            }

            this.final_text = final_text + ")\n";
            return null;
        }

        print_code(ctx);

        return null;
    }

    @Override public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        this.final_text = this.final_text + "def " + this.actual_function + "(";

        ArrayList<Integer> indexes = parameters_not_used.get(this.actual_function);

        if (indexes == null || indexes.size() == 0) {
            this.final_text = final_text + ctx.parameters().typedargslist().getText();
        } else if (indexes != null && !(indexes.size() == ctx.parameters().typedargslist().tfpdef().size())) {
            for (int i = 0; i < ctx.parameters().typedargslist().tfpdef().size(); i++) {
                this.final_text = (indexes.contains(i)) ? final_text : final_text + ctx.parameters().typedargslist().tfpdef(i).getText() + ", ";
            }
            this.final_text = this.final_text.substring(0, this.final_text.length() - 2);
        }

        this.final_text = this.final_text + "):\n";

        this.indent = this.indent+"    ";
        this.indent = this.indent.substring(0,this.indent.length()-4);

        return null;
    }

    @Override public T visitStmt(Python3Parser.StmtContext ctx) {

        if(ctx.compound_stmt() != null){
            if(ctx.compound_stmt().funcdef() != null) {
                this.actual_function = ctx.compound_stmt().funcdef().NAME().getText();

                //Se inicializan los parámetros inútiles de la función (Se buscan por indice y se guardan sus nombres)
                ArrayList<Integer> parameter_indexes = parameters_not_used.get(this.actual_function);
                this.actual_parameters_useless = new ArrayList<>();

                for(Integer value : parameter_indexes){
                    actual_parameters_useless.add(this.all_parameters.get(value));
                }

                //Se inicializan las variables inútiles de la función
                this.actual_variables_useless = new ArrayList<>(variables_not_used.get(this.actual_function));
                visitChildren(ctx);
                this.actual_function = "";
            }else{
                return visitChildren(ctx);
            }
        }else if(!this.actual_function.equals("")){
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            if( ctx.simple_stmt() != null &&
                ctx.simple_stmt().small_stmt() != null &&
                ctx.simple_stmt().small_stmt().get(0) != null &&
                ctx.simple_stmt().small_stmt().get(0).expr_stmt() != null && (
                ctx.simple_stmt().small_stmt().get(0).expr_stmt().ASSIGN().size() > 0 ||
                ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign() != null )
            ){
                if(this.actual_parameters_useless.size()!=0 || this.actual_variables_useless.size()!=0){
                    Python3Parser.Expr_stmtContext expr_stmtContext = ctx.simple_stmt().small_stmt().get(0).expr_stmt();
                    Python3Parser.Testlist_star_exprContext variable_list = expr_stmtContext.testlist_star_expr(0);

                    String left_part = "";
                    String center_part = (ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign() != null) ? ctx.simple_stmt().small_stmt().get(0).expr_stmt().augassign().getText() : "=";
                    String right_part = "";

                    for (int i=0; i<variable_list.test().size(); i++) {
                        Python3Parser.TestContext variable_in_expre = variable_list.test(i);

                        System.out.println(ctx.getText());
                        if(this.actual_parameters_useless.size()!=0 && this.used_parameters.get(this.actual_function).get(variable_in_expre.getText())!=null){
                            if(!actual_parameters_useless.contains(variable_in_expre.getText()) && this.used_parameters.get(this.actual_function).get(variable_in_expre.getText()) > 0){
                                left_part = (left_part.equals("")) ? variable_in_expre.getText() : left_part+","+variable_in_expre.getText();

                                if(center_part.equals("=")){
                                    right_part = (right_part.equals("")) ? expr_stmtContext.testlist_star_expr(1).test(i).getText() : right_part + "," + expr_stmtContext.testlist_star_expr(1).test(i).getText();
                                }else{
                                    right_part = (right_part.equals("")) ? expr_stmtContext.testlist().test(i).getText() : right_part + "," + expr_stmtContext.testlist().test(i).getText();
                                }
                            }

                            if(this.used_parameters.get(this.actual_function).get(variable_in_expre.getText()) != null){
                                if(this.used_parameters.get(this.actual_function).get(variable_in_expre.getText())-1 == 0){
                                    System.out.println("????");
                                    actual_parameters_useless.remove(variable_in_expre.getText());
                                }
                                this.used_parameters.get(this.actual_function).replace(variable_in_expre.getText(), this.used_parameters.get(this.actual_function).get(variable_in_expre.getText())-1);
                            }

                        }else if(this.actual_variables_useless.size()!=0 && this.used_parameters.get(this.actual_function).get(variable_in_expre.getText())!=null){
                            if(!actual_variables_useless.contains(variable_in_expre.getText()) && this.used_variables.get(this.actual_function).get(variable_in_expre.getText()) > 0){
                                System.out.println(ctx.getText());
                                left_part = (left_part.equals("")) ? variable_in_expre.getText() : left_part+","+variable_in_expre.getText();

                                if(center_part.equals("=")){
                                    right_part = (right_part.equals("")) ? expr_stmtContext.testlist_star_expr(1).test(i).getText() : right_part + "," + expr_stmtContext.testlist_star_expr(1).test(i).getText();
                                }else{
                                    right_part = (right_part.equals("")) ? expr_stmtContext.testlist().test(i).getText() : right_part + "," + expr_stmtContext.testlist().test(i).getText();
                                }
                            }

                            if(this.used_variables.get(this.actual_function).get(variable_in_expre.getText()) != null){
                                if(this.used_variables.get(this.actual_function).get(variable_in_expre.getText())-1 == 0){
                                    actual_variables_useless.remove(variable_in_expre.getText());
                                }
                                this.used_variables.get(this.actual_function).replace(variable_in_expre.getText(), this.used_variables.get(this.actual_function).get(variable_in_expre.getText())-1);
                            }

                        }
                    }

                    if(!left_part.equals("")){
                        line_text="";
                        final_text = final_text + indent + left_part + center_part + right_part +"\n";
                    }
                    line++;

                }else{
                    final_text = final_text + indent;
                    print_code(ctx);
                    final_text = final_text + "\n";
                }
            }else{
                line++;
                print_code(ctx);
                final_text = final_text + "\n";
            }
        }else{
            if(ctx.getText().trim().substring(ctx.getText().trim().length()-1, ctx.getText().trim().length()).equals(")")){
                return visitChildren(ctx);
            }

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
