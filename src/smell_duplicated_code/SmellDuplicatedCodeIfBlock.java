import org.antlr.v4.runtime.tree.ParseTree;

public class SmellDuplicatedCodeIfBlock implements Smeller{

    SmellDuplicatedCodeIfBlock(){

    }

    @Override
    public String fix(ParseTree tree) {
        VisitorDuplicatedCodeIfBlock visitor_duplicated_code_block = new VisitorDuplicatedCodeIfBlock(tree);


        return visitor_duplicated_code_block.final_text;
    }
}
