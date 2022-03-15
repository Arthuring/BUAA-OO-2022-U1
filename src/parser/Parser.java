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
    private Func funclist;

    public Parser(Token token, Func func) {
        this.token = token;
        this.funclist = func;
    }

    public Parser(Token token) {
        this.token = token;
    }

    public Expr parseExpr() throws Exception {
        Expr expr = new Expr();
        BigInteger sign = BigInteger.ONE;
        if (token.getCurToken().equals(Token.Type.SUB)) {
            token.next();
            sign = sign.negate();
        } else if (token.getCurToken().equals(Token.Type.ADD)) {
            token.next();
        }
        Power power = new Power(sign);
        Term term = pareTerm();
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
            term = pareTerm();
            term.addFactor(power);
            expr.addTerm(term);
        }
        return expr;
    }

    public Term pareTerm() throws Exception {
        Term term = new Term();
        term.addFactor(parseFactor());

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
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Power parseFactor() throws Exception {
        if (token.getCurToken().equals(Token.Type.NUM)) {               //Number
            return parseNum();
        } else if (token.getCurToken().equals(Token.Type.VAR)) {
            return parseVar();
        } else if (token.getCurToken().equals(Token.Type.LP)) {
            return parseExprFactor();
        } else if (token.getCurToken().equals(Token.Type.SIN)) {
            return parseSin();
        } else if (token.getCurToken().equals(Token.Type.COS)) {
            return parseCos();
        } else if (token.getCurToken().equals(Token.Type.SUM)) {
            return parseSum();
        } else if (token.getCurToken().equals(Token.Type.FUNC)) {
            return parseFunc();
        } else {
            throw new Exception("Not Factor");
        }
    }

    public Power parseNum() {

        Power power = new Power(new BigInteger(token.getCurInfo()));
        token.next();
        return power;
    }

    public Power parseVar() throws Exception {
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

    public Power parseExprFactor() throws Exception {
        token.next();
        Expr exprFactor = parseExpr();
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
                return power.analyse();
            } else {
                throw new Exception("EXP not num!");
            }
        } else {
            Power power = new Power(BigInteger.ONE, exprFactor, BigInteger.ONE);
            return power.analyse();
            //return exprFactor;
        }
    }

    public Power parseSum() throws Exception {
        token.next();
        if (token.getCurToken().equals(Token.Type.LP)) {
            token.next();
            parseVar();
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
            Expr expr = parseExpr();
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

    public Power parseSin() throws Exception {
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
            Power inner = parseFactor();
            sin = new Sin(inner);
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

    public Power parseCos() throws Exception {
        token.next();
        Cos cos;
        if (token.getCurToken().equals(Token.Type.LP)) {
            token.next();
            if (token.getCurToken().equals(Token.Type.SUB)) {
                token.next();
            } else if (token.getCurToken().equals(Token.Type.ADD)) {
                token.next();
            }
            Power inner = parseFactor();
            cos = new Cos(inner);
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

    public Power parseFunc() throws Exception {
        final String type = token.getCurInfo();
        token.next();
        if (!token.getCurToken().equals(Token.Type.LP)) {
            throw new Exception("no LP in function call");
        }
        token.next();
        ArrayList<Factor> exactParameters = new ArrayList<>();
        Power power = parseFactor();
        exactParameters.add(power);
        while (!token.getCurToken().equals(Token.Type.RP)) {
            if (!token.getCurToken().equals(Token.Type.PAU)) {
                throw new Exception("need pause");
            }
            token.next();
            power = parseFactor();
            exactParameters.add(power);
        }
        token.next();
        Expr expr = funclist.getFunc(type, exactParameters);
        Power ans = new  Power(BigInteger.ONE,expr,BigInteger.ONE);
        return ans.analyse();
    }

    public Power parseExectParameter() throws Exception {
        BigInteger sign = BigInteger.ONE;
        if (token.getCurToken().equals(Token.Type.SUB)) {
            sign = sign.negate();
            token.next();
        } else if (token.getCurToken().equals(Token.Type.ADD)) {
            token.next();
        }
        if (token.getCurToken().equals(Token.Type.NUM)) {
            BigInteger num = new BigInteger(token.getCurInfo());
            num = num.multiply(sign);
            token.next();
            return new Power(num);
        } else if (token.getCurToken().equals(Token.Type.VAR)) {
            return parseVar();
        } else if (token.getCurToken().equals(Token.Type.SIN)) {
            return parseSin();
        } else if (token.getCurToken().equals(Token.Type.COS)) {
            return parseCos();
        } else if (token.getCurToken().equals(Token.Type.SUM)) {
            return null;
        } else if (token.getCurToken().equals(Token.Type.FUNC)) {
            return null;
        } else {
            throw new Exception("worng parameter");
        }
    }

}

