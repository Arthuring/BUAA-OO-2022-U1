package parser;

import expression.Cos;
import expression.Expr;
import expression.Factor;
import expression.Func;
import expression.Power;
import expression.Sin;
import expression.Sum;
import expression.Term;
import expression.Variable;

import java.math.BigInteger;
import java.util.ArrayList;

public class Parser {
    private final Token token;
    private final Func funclist;

    public enum Mod {
        FUNC_DEFINE, EXPR_CULC
    }

    public Parser(Token token, Func func) {
        this.token = token;
        this.funclist = func;
    }

    public Parser(Token token) {
        this.token = token;
        this.funclist = null;
    }

    public Expr parseExpr(Mod mod) throws Exception {
        Expr expr = new Expr();
        BigInteger sign = BigInteger.ONE;
        if (token.getCurToken().equals(Token.Type.SUB)) {
            token.next();
            sign = sign.negate();
        } else if (token.getCurToken().equals(Token.Type.ADD)) {
            token.next();
        }
        Power power = new Power(sign);
        Term term = pareTerm(mod);
        term.addFactor(power);
        expr.addTerm(term);

        while (!token.reachEnd() && !token.getCurToken().equals(Token.Type.RP)) {
            sign = BigInteger.ONE;
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
                sign = sign.negate();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            } else {
                throw new Exception("not found +/-");
            }
            power = new Power(sign);
            term = pareTerm(mod);
            term.addFactor(power);
            expr.addTerm(term);
        }
        return expr;
    }

    public Term pareTerm(Mod mod) throws Exception {
        Term term = new Term();
        term.addFactor(parseFactor(mod));

        while (token.getCurToken().equals(Token.Type.MULT)) {
            token.next();
            BigInteger sign = BigInteger.ONE;
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
                sign = sign.negate();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            term.addFactor(new Power(sign));
            term.addFactor(parseFactor(mod));
        }
        return term;
    }

    public Power parseFactor(Mod mod) throws Exception {
        if (token.getCurToken().equals(Token.Type.NUM) ||
                token.getCurToken().equals(Token.Type.ADD) ||
                token.getCurToken().equals(Token.Type.SUB)) {               //Number
            return parseNum(mod);
        } else if (token.getCurToken().equals(Token.Type.VAR)) {
            return parseVar(mod);
        } else if (token.getCurToken().equals(Token.Type.LP)) {
            return parseExprFactor(mod);
        } else if (token.getCurToken().equals(Token.Type.SIN)) {
            return parseSin(mod);
        } else if (token.getCurToken().equals(Token.Type.COS)) {
            return parseCos(mod);
        } else if (token.getCurToken().equals(Token.Type.SUM)) {
            return parseSum(mod);
        } else if (token.getCurToken().equals(Token.Type.FUNC)) {
            return parseFunc(mod);
        } else {
            throw new Exception("Not Factor");
        }
    }

    public Power parseNum(Mod mod) {
        BigInteger sign = BigInteger.ONE;
        if (token.getCurToken().equals(Token.Type.SUB)) {
            token.next();
            sign = sign.negate();
        } else if (token.getCurToken().equals(Token.Type.ADD)) {
            token.next();
        }
        Power power = new Power(new BigInteger(token.getCurInfo()).multiply(sign));
        token.next();
        return power;
    }

    public Power parseVar(Mod mod) throws Exception {
        Variable variable = new Variable(token.getCurInfo());
        token.next();
        if (token.getCurToken().equals(Token.Type.EXP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            if (token.getCurToken().equals(Token.Type.NUM)) {
                BigInteger exp = new BigInteger(token.getCurInfo());
                token.next();
                Power power = new Power(BigInteger.ONE, variable, exp);
                return power;
            } else {
                throw new Exception("EXP not num!");
            }

        } else {
            return new Power(BigInteger.ONE, variable, BigInteger.ONE);
        }
    }

    public Power parseExprFactor(Mod mod) throws Exception {
        token.next();
        Expr exprFactor = parseExpr(mod);
        if (token.getCurToken().equals(Token.Type.RP)) {
            token.next();
        } else {
            throw new Exception("RP not found");
        }
        if (token.getCurToken().equals(Token.Type.EXP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            if (token.getCurToken().equals(Token.Type.NUM)) {
                BigInteger exp = new BigInteger(token.getCurInfo());
                token.next();
                Power power = new Power(BigInteger.ONE, exprFactor, exp);
                if (mod.equals(Mod.FUNC_DEFINE)) {
                    return power;
                }
                return power.analyse();
            } else {
                throw new Exception("EXP not num!");
            }
        } else {
            Power power = new Power(BigInteger.ONE, exprFactor, BigInteger.ONE);
            if (mod.equals(Mod.FUNC_DEFINE)) {
                return power;
            }
            return power.analyse();
            //return exprFactor;
        }
    }

    public Power parseSum(Mod mod) throws Exception {
        token.next();
        if (token.getCurToken().equals(Token.Type.LP)) {
            token.next();
            parseVar(mod);
            token.next();
            BigInteger sign = BigInteger.ONE;
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
                sign = sign.negate();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            BigInteger start = new BigInteger(token.getCurInfo());
            start = start.multiply(sign);
            token.next();//,
            token.next();//end
            sign = BigInteger.ONE;
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
                sign = sign.negate();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            BigInteger end = new BigInteger(token.getCurInfo());
            end = end.multiply(sign);
            token.next();//,
            token.next();//expr
            Expr expr = parseExpr(Mod.FUNC_DEFINE);
            Sum sum = new Sum(start, end, expr);
            if (token.getCurToken().equals(Token.Type.RP)) {
                token.next();
            } else {
                throw new Exception("no RP found");
            }
            return sum.toExpr();

        } else {
            throw new Exception("not fond lp");
        }
    }

    public Power parseSin(Mod mod) throws Exception {
        token.next();
        Sin sin;
        BigInteger sign = BigInteger.ONE;
        if (token.getCurToken().equals(Token.Type.LP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.SUB)) {
                sign = sign.negate();
                token.next();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            Power inner = parseFactor(mod);
            sin = new Sin(inner);
            if (token.getCurToken().equals(Token.Type.MULT) ||
                    token.getCurToken().equals(Token.Type.ADD) ||
                    token.getCurToken().equals(Token.Type.SUB)) {
                throw new Exception("inner of sin " +
                        "must be an factor");
            }
        } else {
            throw new Exception();
        }

        if (token.getCurToken().equals(Token.Type.RP)) {
            token.next();
        }
        if (token.getCurToken().equals(Token.Type.EXP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            if (token.getCurToken().equals(Token.Type.NUM)) {
                BigInteger exp = new BigInteger(token.getCurInfo());
                token.next();
                if (exp.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                    Power power = new Power(BigInteger.ONE, sin, exp);
                    return power;
                } else {
                    Power power = new Power(sign, sin, exp);
                    return power;
                }
            } else {
                throw new Exception("EXP not num!");
            }

        } else {
            return new Power(sign, sin, BigInteger.ONE);
            //return exprFactor;
        }
    }

    public Power parseCos(Mod mod) throws Exception {
        token.next();
        Cos cos;
        if (token.getCurToken().equals(Token.Type.LP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            Power inner = parseFactor(mod);
            cos = new Cos(inner);
            if (token.getCurToken().equals(Token.Type.MULT) ||
                    token.getCurToken().equals(Token.Type.ADD) ||
                    token.getCurToken().equals(Token.Type.SUB)) {
                throw new Exception("inner of cos " +
                        "must be an factor");
            }
        } else {
            throw new Exception();
        }

        if (token.getCurToken().equals(Token.Type.RP)) {
            token.next();
        }
        if (token.getCurToken().equals(Token.Type.EXP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            if (token.getCurToken().equals(Token.Type.NUM)) {
                BigInteger exp = new BigInteger(token.getCurInfo());
                token.next();
                Power power = new Power(BigInteger.ONE, cos, exp);
                return power;
            } else {
                throw new Exception("EXP not num!");
            }

        } else {
            return new Power(BigInteger.ONE, cos, BigInteger.ONE);
            //return exprFactor;
        }
    }

    public Power parseFunc(Mod mod) throws Exception {
        final String type = token.getCurInfo();
        token.next();
        if (!token.getCurToken().equals(Token.Type.LP)) {
            throw new Exception("no LP in function call");
        }
        token.next();
        ArrayList<Factor> exactParameters = new ArrayList<>();
        Power power = parseFactor(mod);
        exactParameters.add(power);
        while (!token.getCurToken().equals(Token.Type.RP)) {
            if (!token.getCurToken().equals(Token.Type.PAU)) {
                throw new Exception("need pause");
            }
            token.next();
            power = parseFactor(mod);
            exactParameters.add(power);
        }
        token.next();
        assert funclist != null;
        Expr expr = funclist.getFunc(type, exactParameters);
        Power ans = new Power(BigInteger.ONE, expr, BigInteger.ONE);
        return ans.analyse();
    }
}

