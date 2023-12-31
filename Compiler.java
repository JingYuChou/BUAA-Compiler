import Config.CompilerHandler;
import Config.tasks;

import java.io.*;

public class Compiler {
    public static void main(String[] args) throws IOException, RuntimeException {
        /*
        -l : Lexer analysis
        -p : Parser analysis
        -e : Error handle
        -i : llvm ir output
        -m : mips output
        -io : llvm ir and optimize
        -mo : mips and optimize
        -ie : llvm ir and error handle
        -me : mips and error handle
        -ioe : llvm ir and optimize and error handle
        -moe : mips and optimize and error handle
         */
        tasks.isMIPSoutput = true;
        tasks.isOptimize = false;
        tasks.isLLVMoutput = false;
        tasks.isErrorHandle = true;
        CompilerHandler.work();
        //throw new RuntimeException("Compiler finished");
    }
}
