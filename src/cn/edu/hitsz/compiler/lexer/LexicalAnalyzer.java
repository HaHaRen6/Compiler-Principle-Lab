package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private ArrayList<String> tokens;
    private ArrayList<Token> tokenList;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        tokens = new ArrayList<>();
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        try {
            String s = Files.readString(Paths.get(path));
            char[] page = s.toCharArray();
            ArrayList<String> rowTokens = new ArrayList<>();
            for (char c : page) {
                if (rowTokens == null) {
                    rowTokens = new ArrayList<>();
                }
                if (c == '\r' || c == '\n' || c == '\t') {
                    continue;
                } else if (c == '=' || c == ';' || c == '+' || c == ',' || c == '-' ||
                        c == '*' || c == '/' || c == '(' || c == ')') {
                    if (rowTokens.size() != 0) {
                        tokens.add(String.join("", rowTokens));
                    }
                    tokens.add(Character.toString(c));
                    rowTokens = null;
                    continue;
                } else if (c == ' ') {
                    if (rowTokens.size() != 0) {
                        tokens.add(String.join("", rowTokens));
                    }
                    rowTokens = null;
                    continue;
                }
                rowTokens.add(Character.toString(c));
            }
        } catch (IOException e) {
            System.out.println("readError");
        }
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // 自动机实现的词法分析过程
        tokenList = new ArrayList<Token>();
        for (String token : tokens) {
            if (!TokenKind.isAllowed(token) && !Objects.equals(token, ";")) {
                try {
                    Integer.parseInt(token);
                    tokenList.add(Token.normal("IntConst", token));
                    System.out.println(tokenList.get(tokenList.size() - 1));
                } catch (NumberFormatException e) {
                    tokenList.add(Token.normal("id", token));
                    System.out.println(tokenList.get(tokenList.size() - 1));
                    if (!symbolTable.has(token)) {
                        symbolTable.add(token);
                    }
                }
            } else if (Objects.equals(token, ";")) {
                tokenList.add(Token.simple("Semicolon"));
            } else {
                tokenList.add(Token.simple(token));
                System.out.println(tokenList.get(tokenList.size() - 1));
            }
        }
        assert(tokenList.size() != 0);
        tokenList.add(Token.eof());
        System.out.println((tokenList));
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokenList;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
