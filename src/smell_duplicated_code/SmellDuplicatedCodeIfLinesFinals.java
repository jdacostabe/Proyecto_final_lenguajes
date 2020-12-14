import org.antlr.v4.runtime.tree.ParseTree;

public class SmellDuplicatedCodeIfLinesFinals implements Smeller{
    @Override
    public String fix(ParseTree tree) {
        VisitorDuplicatedCodeIfLinesFinals visitor_duplicated_code_line = new VisitorDuplicatedCodeIfLinesFinals(tree);
        return visitor_duplicated_code_line.final_text;
    }
}
