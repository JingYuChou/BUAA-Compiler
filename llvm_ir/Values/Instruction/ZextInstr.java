package llvm_ir.Values.Instruction;

import BackEnd.MIPS.Assembly.CommentAsm;
import BackEnd.MIPS.MipsController;
import Config.tasks;
import llvm_ir.IRController;
import llvm_ir.Value;
import llvm_ir.llvmType.LLVMType;

public class ZextInstr extends Instr {

    private final LLVMType type1;

    public ZextInstr(LLVMType type1, LLVMType type2, Value operand) {
        super(type2, tasks.isOptimize ? "" : IRController.getInstance().genVirtualRegNum());
        this.addValue(operand);
        this.type1 = type1;
    }

    @Override
    public String toString() {
        return name + " = " + "zext " + type1.toString() + " " + operands.get(0).getName() + " to " + type.toString();
    }

    @Override
    public void genMIPS() {
        CommentAsm commentAsm = new CommentAsm(this.toString());
        MipsController.getInstance().addAsm(commentAsm);
        Value operand = operands.get(0);
        //MIPS不存在bool
        this.useReg = operand.isUseReg();
        if (useReg) this.register = operand.getRegister();
        else this.offset = operand.getOffset();
    }
}
