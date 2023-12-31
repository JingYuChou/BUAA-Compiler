package FrontEnd.Nodes.Exp;

import Enums.SyntaxVarType;
import Enums.tokenType;
import FrontEnd.Nodes.Node;
import FrontEnd.Nodes.TokenNode;
import llvm_ir.IRController;
import llvm_ir.Value;
import llvm_ir.Values.ConstInteger;
import llvm_ir.Values.Instruction.BinaryInstr;
import llvm_ir.llvmType.Integer32Type;

import java.util.ArrayList;

public class MulExp extends Node {
    public MulExp(SyntaxVarType type, ArrayList<Node> children) {
        super(type, children);
    }

    @Override
    public int getDim() {
        for (Node n : children) if (n.getDim() != -1) return n.getDim();
        return -1;
    }

    public int calc() {
        if (children.size() == 1) return ((UnaryExp) children.get(0)).calc();
        else {
            int a = ((MulExp) children.get(0)).calc();
            int b = ((UnaryExp) children.get(2)).calc();
            switch (((TokenNode) children.get(1)).getTokenType()) {
                case MULT -> {
                    return a * b;
                }
                case DIV -> {
                    return a / b;
                }
                case MOD -> {
                    return a % b;
                }
                default -> {
                    return -1;
                }
            }
        }
    }

    @Override
    public Value genLLVMir() {
        if (children.size() == 1) {
            return ((UnaryExp) children.get(0)).genLLVMir();
        } else {
            Value operand1 = children.get(0).genLLVMir();
            Value operand2 = children.get(2).genLLVMir();
            BinaryInstr.op Op;
            if (operand1 instanceof ConstInteger constInteger && operand2 instanceof ConstInteger constInteger1) {
                switch (((TokenNode) children.get(1)).getTokenType()) {
                    case MULT -> {
                        return new ConstInteger(constInteger.getVal() * constInteger1.getVal());
                    }
                    case DIV -> {
                        return new ConstInteger(constInteger.getVal() / constInteger1.getVal());
                    }
                    case MOD -> {
                        return new ConstInteger(constInteger.getVal() % constInteger1.getVal());
                    }
                    default -> {
                        return null;
                    }
                }
            } else if (operand1 instanceof ConstInteger constInteger) {
                if (constInteger.getVal() == 1 && ((TokenNode) children.get(1)).getTokenType() == tokenType.MULT) {
                    return operand2;
                } else if (constInteger.getVal() == 0) {
                    return new ConstInteger(0);
                }
            } else if (operand2 instanceof ConstInteger constInteger) {
                if (constInteger.getVal() == 1 && ((TokenNode) children.get(1)).getTokenType() == tokenType.MULT) {
                    return operand1;
                } else if (constInteger.getVal() == 0 && ((TokenNode) children.get(1)).getTokenType() == tokenType.MULT) {
                    return new ConstInteger(0);
                } else if (constInteger.getVal() == 1 && ((TokenNode) children.get(1)).getTokenType() == tokenType.DIV) {
                    return operand1;
                } else if (constInteger.getVal() == 1 && ((TokenNode) children.get(1)).getTokenType() == tokenType.MOD) {
                    return new ConstInteger(0);
                }
            } else if (operand1 == operand2) {
                if (((TokenNode) children.get(1)).getTokenType() == tokenType.DIV) {
                    return new ConstInteger(1);
                } else if (((TokenNode) children.get(1)).getTokenType() == tokenType.MOD) {
                    return new ConstInteger(0);
                }
            }
            switch (((TokenNode) children.get(1)).getTokenType()) {
                case MULT -> {
                    Op = BinaryInstr.op.MUL;
                }
                case DIV -> {
                    Op = BinaryInstr.op.SDIV;
                }
                case MOD -> {
                    Op = BinaryInstr.op.SREM;
                }
                default -> {
                    Op = null;
                }
            }
            BinaryInstr binaryInstr = new BinaryInstr(new Integer32Type(), operand1, operand2, Op);
            IRController.getInstance().addInstr(binaryInstr);
            return binaryInstr;
        }
    }
}
