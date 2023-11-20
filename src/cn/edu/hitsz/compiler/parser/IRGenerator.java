package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {
    Stack<IRValue> irStack = new Stack<>();
    ArrayList<Instruction> IL = new ArrayList<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        if ("IntConst".equals(currentToken.getKindId())) {
            irStack.push(IRImmediate.of(Integer.parseInt(currentToken.getText())));
        } else if ("id".equals(currentToken.getKindId())) {
            irStack.push(IRVariable.named(currentToken.getText()));
        }
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        switch (production.index()) {
            case 6 -> {     // S -> id = E;
                IRValue second = irStack.pop();
                IRValue first = irStack.pop();
                IL.add(Instruction.createMov((IRVariable) first, second));
            }
            case 7 -> {     // S -> return E;
                IL.add(Instruction.createRet(irStack.pop()));
            }
            case 8 -> {     // E -> E + A;
                IRValue second = irStack.pop();
                IRValue first = irStack.pop();
                IRVariable target = IRVariable.temp();
                IL.add(Instruction.createAdd(target, first, second));
                irStack.push(target);
            }
            case 9 -> {     // E -> E - A;
                IRValue second = irStack.pop();
                IRValue first = irStack.pop();
                IRVariable target = IRVariable.temp();
                IL.add(Instruction.createSub(target, first, second));
                irStack.push(target);
            }
            case 11 -> {    // A -> A * B;
                IRValue second = irStack.pop();
                IRValue first = irStack.pop();
                IRVariable target = IRVariable.temp();
                IL.add(Instruction.createMul(target, first, second));
                irStack.push(target);
            }
            case 1, 10, 12, 14, 15 -> {    // E -> A;

            }
            default -> {
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        System.out.println("IRGenerator Success!\n");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
    }

    public List<Instruction> getIR() {
        return IL;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

