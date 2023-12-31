package llvm_ir.Values.Instruction.terminatorInstr;

import BackEnd.MIPS.Assembly.*;
import BackEnd.MIPS.MipsController;
import BackEnd.MIPS.Register;
import llvm_ir.IRController;
import llvm_ir.Value;
import llvm_ir.Values.BasicBlock;
import llvm_ir.Values.ConstBool;
import llvm_ir.Values.ConstInteger;
import llvm_ir.Values.Instruction.CallInstr;
import llvm_ir.Values.Instruction.Instr;
import llvm_ir.llvmType.BoolType;
import llvm_ir.llvmType.LLVMType;

import java.util.ArrayList;
import java.util.HashMap;

public class BranchInstr extends Instr {

    public BranchInstr(BasicBlock label1, BasicBlock label2, Value judge) {
        super(new LLVMType(), "");
        this.addValue(judge);
        this.addValue(label1);
        this.addValue(label2);
    }

    public BranchInstr(BasicBlock label1) {
        super(new LLVMType(), "");
        this.addValue(label1);
    }

    @Override
    public String toString() {
        if (operands.size() == 1)
            return "br label " + operands.get(0).getName();
        return "br " + new BoolType().toString() + " " + operands.get(0).getName() + ", label " + operands.get(1).getName() + ", label " + operands.get(2).getName();
    }

    @Override
    public void genMIPS() {
        CommentAsm asm = new CommentAsm(this.toString());
        MipsController.getInstance().addAsm(asm);
        ArrayList<BasicBlock> blocks = new ArrayList<>(MipsController.getInstance().getCurrentFunction().getBlockArrayList());
        BasicBlock currentBlock = MipsController.getInstance().getCurrentBlock();
        if (operands.size() == 1) {
            BasicBlock label1 = (BasicBlock) operands.get(0);
            if (blocks.indexOf(currentBlock) + 1 == blocks.indexOf(label1)) {
                return;
            }
            JAsm j = new JAsm(label1.getMIPSLabel());
            MipsController.getInstance().addAsm(j);
        } else {
            BasicBlock label1 = (BasicBlock) operands.get(1);
            BasicBlock label2 = (BasicBlock) operands.get(2);
            Value judge = operands.get(0);
            if (judge.isUseReg() && judge.getRegister() != Register.ZERO) {
                if (blocks.indexOf(currentBlock) + 1 != blocks.indexOf(label1)) {
                    BranchITAsm bne = new BranchITAsm(BranchITAsm.Op.bne, judge.getRegister(), Register.ZERO, label1.getMIPSLabel());
                    MipsController.getInstance().addAsm(bne);
                }
                if (blocks.indexOf(currentBlock) + 1 != blocks.indexOf(label2)) {
                    BranchITAsm beq = new BranchITAsm(BranchITAsm.Op.beq, judge.getRegister(), Register.ZERO, label2.getMIPSLabel());
                    MipsController.getInstance().addAsm(beq);
                }
            } else if (judge instanceof ConstInteger constInteger) {
                int v = constInteger.getVal();
                JAsm j;
                if (v == 0) {
                    if (blocks.indexOf(currentBlock) + 1 == blocks.indexOf(label2)) {
                        return;
                    }
                    j = new JAsm(label2.getMIPSLabel());
                } else {
                    if (blocks.indexOf(currentBlock) + 1 == blocks.indexOf(label1)) {
                        return;
                    }
                    j = new JAsm(label1.getMIPSLabel());
                }
                MipsController.getInstance().addAsm(j);
            } else {
                MemITAsm lw = new MemITAsm(MemITAsm.Op.lw, Register.K0, Register.SP, judge.getOffset());
                MipsController.getInstance().addAsm(lw);
                if (blocks.indexOf(currentBlock) + 1 != blocks.indexOf(label1)) {
                    BranchITAsm bne = new BranchITAsm(BranchITAsm.Op.bne, Register.K0, Register.ZERO, label1.getMIPSLabel());
                    MipsController.getInstance().addAsm(bne);
                }
                if (blocks.indexOf(currentBlock) + 1 != blocks.indexOf(label2)) {
                    BranchITAsm beq = new BranchITAsm(BranchITAsm.Op.beq, Register.K0, Register.ZERO, label2.getMIPSLabel());
                    MipsController.getInstance().addAsm(beq);
                }
            }
        }
    }

    public ArrayList<BasicBlock> getSuccessors() {
        ArrayList<BasicBlock> successors = new ArrayList<>();
        if (operands.size() == 1) {
            BasicBlock label1 = (BasicBlock) operands.get(0);
            successors.add(label1);
        } else {
            BasicBlock label1 = (BasicBlock) operands.get(1);
            BasicBlock label2 = (BasicBlock) operands.get(2);
            successors.add(label1);
            successors.add(label2);
        }
        return successors;
    }

    @Override
    public Instr copy(HashMap<Value, Value> map) {
        if (map.containsKey(this)) return (Instr) map.get(this);
        if (operands.size() == 1) {
            BasicBlock label1 = (BasicBlock) operands.get(0);
            return new BranchInstr((BasicBlock) label1.copy(map));
        } else {
            BasicBlock label1 = (BasicBlock) operands.get(1);
            BasicBlock label2 = (BasicBlock) operands.get(2);
            return new BranchInstr((BasicBlock) label1.copy(map), (BasicBlock) label2.copy(map), operands.get(0).copy(map));
        }
    }

    @Override
    public boolean isPinnedInst() {
        return true;
    }

    public Value getJudge() {
        return operands.get(0);
    }
}
