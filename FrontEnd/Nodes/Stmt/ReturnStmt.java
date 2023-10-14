package FrontEnd.Nodes.Stmt;

import Enums.SyntaxVarType;
import FrontEnd.Nodes.Node;

import java.util.ArrayList;

public class ReturnStmt extends Stmt {
    public ReturnStmt(SyntaxVarType type, ArrayList<Node> children) {
        super(type, children);
    }
}
