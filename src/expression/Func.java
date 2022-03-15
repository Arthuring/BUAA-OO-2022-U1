package expression;

import parser.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Func {
    private final HashMap<Type, Expr> funcList = new HashMap<>();
    private final HashMap<Type, ArrayList<Variable>> variableList = new HashMap<>();
    private final HashMap<Type, Integer> variableNum = new HashMap<>();

    public enum Type {
        f, g, h
    }

    public Expr getFunc(String funcName, ArrayList<Factor> vars) throws Exception {
        ArrayList<Variable> formalParameters = variableList.get(Type.valueOf(funcName));
        Expr expr = funcList.get(Type.valueOf(funcName));
        if (vars.size() != formalParameters.size()) {
            throw new Exception("not the number of parameters");
        } else {
            for (int i = 0; i < formalParameters.size(); i++) {
                expr = expr.substitute(formalParameters.get(i), vars.get(i));
            }
        }
        return expr;
    }

    public Integer getVarNum(String funcName) {
        return variableNum.get(Type.valueOf(funcName));
    }

    public Variable getVar(String funcName, int index) {
        return variableList.get(Type.valueOf(funcName)).get(index);
    }

    public void funcDefine(Token token, Expr expr) throws Exception {
        int varNum = 0;
        Type funcName = null;
        if (token.getCurToken().equals(Token.Type.FUNC)) {
            funcName = Type.valueOf(token.getCurInfo());
            funcList.put(funcName, expr);
            token.next();
            if (token.getCurToken().equals(Token.Type.LP)) {
                token.next();
            }
            ArrayList<Variable> vars = new ArrayList<>();
            if (token.getCurToken().equals(Token.Type.VAR)) {
                Variable var = new Variable(Variable.Type.valueOf(token.getCurInfo()));
                vars.add(var);
                token.next();
                varNum = varNum + 1;
            }
            while (token.getCurToken().equals(Token.Type.PAU)) {
                token.next();
                if (token.getCurToken().equals(Token.Type.VAR)) {
                    Variable var = new Variable(Variable.Type.valueOf(token.getCurInfo()));
                    vars.add(var);
                    token.next();
                } else {
                    throw new Exception("no VAR");
                }
                varNum = varNum + 1;
            }
            variableList.put(funcName, vars);
            variableNum.put(funcName, varNum);
        } else {
            throw new Exception("Not FUNC");
        }


    }
}
