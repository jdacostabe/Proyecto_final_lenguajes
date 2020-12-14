import org.antlr.v4.runtime.tree.ParseTree;

public class SmellDuplicatedCodeIfLinesInitials implements Smeller {
    @Override
    public String fix(ParseTree tree) {
        VisitorDuplicatedCodeIfLinesInitials visitor_duplicated_code_line = new VisitorDuplicatedCodeIfLinesInitials(tree);
        return visitor_duplicated_code_line.final_text;
    }
}

