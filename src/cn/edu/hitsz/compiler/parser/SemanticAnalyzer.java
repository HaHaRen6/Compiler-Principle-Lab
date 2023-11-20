package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.Stack;

// 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {
    private final Stack<Token> tokens = new Stack<>();
    private SymbolTable memTable = new SymbolTable();

    @Override
    public void whenAccept(Status currentStatus) {
        // 该过程在遇到 Accept 时要采取的代码动作
        System.out.println("SemanticAnalyzer Success");
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // 该过程在遇到 reduce production 时要采取的代码动作
        int length = production.body().size();
        switch (production.index()) {
            case 4 -> { // S -> D id
                memTable.get(tokens.pop().getText()).setType(SourceCodeType.Int);
                tokens.pop();
                tokens.push(null);
            }
            case 5 -> { // D -> int
            }
            default -> { //
                for (int i = 0; i < length; i++) {
                    tokens.pop();
                }
                tokens.push(null);
            }
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // 该过程在遇到 shift 时要采取的代码动作
        tokens.push(currentToken);
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        memTable = table;
    }
}

