package expression;

import java.math.BigInteger;

public class Sum {
    private final BigInteger start;
    private final BigInteger end;
    private final Expr expression;

    public Sum(BigInteger start, BigInteger end, Expr expr) {
        this.start = start;
        this.end = end;
        this.expression = expr;
    }

    public Power toExpr() throws Exception {
        Variable i = new Variable(Variable.Type.i);
        Expr expr = new Expr();
        BigInteger x;
        for (x = start; x.compareTo(end.add(BigInteger.ONE)) < 0; x = x.add(BigInteger.ONE)) {
            Term term = new Term();
            Power p = new Power(x);
            term.addFactor(new Power(BigInteger.ONE, expression.substitute(i, p), BigInteger.ONE));
            expr.addTerm(term);
        }
        Power ans = new Power(BigInteger.ONE,expr,BigInteger.ONE);
        return ans.analyse();
    }

}
