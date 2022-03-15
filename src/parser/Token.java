package parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private static final String ADD_P = "(?<ADD>\\s*\\+\\s*)";
    private static final String SUB_P = "(?<SUB>\\s*-\\s*)";
    private static final String MULT_P = "(?<MULT>\\s*\\*\\s*)";
    private static final String NUM_P = "(?<NUM>\\s*([+-]?)(\\d+)\\s*)";
    private static final String LP_P = "(?<LP>\\s*\\(\\s*)";
    private static final String RP_P = "(?<RP>\\s*\\)\\s*)";
    private static final String VAR_P = "(?<VAR>\\s*[xyzi]\\s*)";
    private static final String SIN_P = "(?<SIN>\\s*sin\\s*)";
    private static final String COS_P = "(?<COS>\\s*cos\\s*)";
    private static final String SUM_P = "(?<SUM>\\s*sum\\s*)";
    private static final String FUNC_P = "(?<FUNC>\\s*[f-h]\\s*)";
    private static final String EXP_P = "(?<EXP>\\s*\\*\\*\\s*)";
    private static final String PAU_P = "(?<PAU>\\s*,\\s*)";
    private static final String ERR_P = "(?<ERR>.)";
    private static final Pattern TOKEN_PATTERN = Pattern.compile(ADD_P + "|" + SUB_P +
            "|" + EXP_P + "|" + NUM_P + "|" + LP_P +
            "|" + RP_P + "|" + VAR_P + "|" + MULT_P +
            "|" + SIN_P + "|" + COS_P + "|" + SUM_P +
            "|" + FUNC_P + "|" + PAU_P + "|" + ERR_P);
    private final ArrayList<Token> tokens = new ArrayList<>();

    public enum Type {
        ADD, SUB, MULT, NUM, LP, RP, VAR, EXP, SIN, COS, SUM, FUNC, PAU
    }

    private Type type;
    private final String info;
    private int pos = 0;
    private Type curToken;
    private String curInfo;
    private String source;

    public Token(String source) throws Exception {
        this.source = source;
        this.info = null;
        try {
            this.extractTokens();
        } catch (Exception e) {
            throw e;
        }
        this.next();

    }

    public void next() {
        if (pos == tokens.size()) {
            return;
        }
        curToken = tokens.get(pos).getType();
        curInfo = tokens.get(pos).getInfo();
        pos += 1;
    }

    public Type getCurToken() {
        return this.curToken;
    }

    public String getCurInfo() {
        return this.curInfo;
    }

    public String getInfo() {
        return this.info;
    }

    public Token(Type fact, String info) {
        this.type = fact;
        this.info = info;
    }

    public void add(Token token) {
        this.tokens.add(token);
    }

    public Type getType() {
        return this.type;
    }

    public void extractTokens() throws Exception {
        Matcher m = TOKEN_PATTERN.matcher(source);
        lab:
        while (m.find()) {
            for (Token.Type t : Token.Type.values()) {
                if (m.group(t.toString()) != null) {
                    String information = m.group(t.toString()).trim();
                    tokens.add(new Token(t, information));
                    continue lab;
                }
            }
            throw new Exception("Illegal");
        }
    }

    public boolean reachEnd() {
        return (pos == tokens.size());
    }

}
