package expression;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class Power extends Factor {
    private final Factor base;
    private final BigInteger exp;
    private final BigInteger coe;

    public Power(BigInteger coe, Factor base, BigInteger exp) {
        this.coe = coe;
        this.base = base;
        this.exp = exp;
    }

    public Power(BigInteger coe, BigInteger exp) {
        this.coe = coe;
        this.base = new Variable();
        this.exp = exp;
    }

    public Power(BigInteger coe) {
        this.coe = coe;
        this.base = new Variable();
        this.exp = BigInteger.ZERO;
    }

    public Power(Factor base) {
        this.base = base;
        this.coe = BigInteger.ONE;
        this.exp = BigInteger.ONE;
    }

    public Factor getBase() {
        return this.base;
    }

    public BigInteger getExp() {
        return this.exp;
    }

    public BigInteger getCoe() {
        return this.coe;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.exp.intValue() == 2 && this.base instanceof Variable) {
            if (!this.coe.equals(BigInteger.ONE)) {
                if (this.coe.equals(BigInteger.ONE.negate())) {
                    sb.append("-");
                } else {
                    sb.append(this.coe).append("*");
                }
            }
            sb.append("x*x");
            return sb.toString();
        }

        if (!this.coe.equals(BigInteger.ZERO)) {
            if (!this.exp.equals(BigInteger.ZERO)) {
                if (!this.coe.equals(BigInteger.ONE)) {
                    if (this.coe.equals(BigInteger.ONE.negate())) {
                        sb.append("-");
                    } else {
                        sb.append(this.coe).append("*");
                    }
                }
                if (this.base instanceof Expr) {
                    sb.append("(").append(base).append(")");
                } else {
                    sb.append(base.toString());
                }
                if (!exp.equals(BigInteger.ONE)) {
                    sb.append("**").append(exp);
                }
            } else {
                sb.append(this.coe);
            }
        } else {
            return ("0");
        }
        return sb.toString();
    }

    public String toStringInSin() {
        StringBuilder sb = new StringBuilder();
        if (!this.coe.equals(BigInteger.ZERO)) {
            if (!this.exp.equals(BigInteger.ZERO)) {
                if (!this.coe.equals(BigInteger.ONE)) {
                    if (this.coe.equals(BigInteger.ONE.negate())) {
                        sb.append("-");
                    } else {
                        sb.append(this.coe).append("*");
                    }
                }
                if (this.base instanceof Expr) {
                    sb.append("(").append(base).append(")");
                } else {
                    sb.append(base.toString());
                }
                if (!exp.equals(BigInteger.ONE)) {
                    sb.append("**").append(exp);
                }
            } else {
                sb.append(this.coe);
            }
        } else {
            return ("0");
        }
        return sb.toString();
    }

    public Power substitute(Variable x, Factor factor) {
        if (exp.equals(BigInteger.ZERO)) {
            return new Power(coe);
        }
        if (base instanceof Variable) {
            Variable ref = (Variable) base;
            if (ref.getName().equals(x.getName())) {
                if (factor instanceof Power) {
                    if (((Power) factor).exp.equals(BigInteger.ZERO)) {
                        BigInteger coe = ((Power) factor).coe.pow(this.exp.intValue());
                        return new Power(coe);
                    }
                    return new Power(BigInteger.ONE, ((Power) factor).base,
                            ((Power) factor).exp.multiply(exp));
                }
                return new Power(BigInteger.ONE, factor, this.getExp());
            } else {
                return new Power(this.getCoe(), this.base, this.getExp());
            }
        } else {
            return new Power(BigInteger.ONE, base.substitute(x, factor), this.getExp());
        }
    }

    public boolean isConst() {
        return this.exp.equals(BigInteger.ZERO);
    }

    public String toStringInCos() {
        StringBuilder sb = new StringBuilder();
        if (!this.coe.equals(BigInteger.ZERO)) {
            if (!this.exp.equals(BigInteger.ZERO)) {
                if (!this.coe.equals(BigInteger.ONE) &&
                        !this.coe.equals(BigInteger.ONE.negate())) {
                    sb.append(this.coe).append("*");
                }
                if (this.base instanceof Expr) {
                    sb.append("(").append(base).append(")");
                } else {
                    sb.append(base.toString());
                }
                if (!exp.equals(BigInteger.ONE)) {
                    sb.append("**").append(exp);
                }
            } else {
                sb.append(this.coe.abs());
            }
        } else {
            return ("0");
        }
        return sb.toString();
    }

    public Power analyse() {
        if (this.base instanceof Expr) {
            /* ArrayList<BasicTerm> ans = new ArrayList<>();
            Power old = new  Power(this.coe,this.base,this.exp);
            Term term  = new Term();
            term.addFactor(old);
            Power one = new Power(BigInteger.ONE, BigInteger.ZERO);
            BasicTerm o = new BasicTerm(one);
            ans.add(o);
            ArrayList<BasicTerm> vars = ((Expr) this.getBase()).calculate();
            BigInteger times = this.getExp();
            for (BigInteger i = BigInteger.ZERO;
                 i.compareTo(times) < 0; i = i.add(BigInteger.ONE)) {
                ans = Multer.mult(ans, vars);
            }
            Expr expr = new Expr(ans);
            expr.addTerm(term);
            */
            Power factor = new Power(this.coe, this.base, this.exp);
            Term term = new Term();
            term.addFactor(factor);
            Expr expr = new Expr();
            expr.addTerm(term);
            expr.analise();
            return new Power(BigInteger.ONE, expr, BigInteger.ONE);
        } else {
            return new Power(this.getCoe(), this.getBase(), this.getExp());
        }
    }

    @Override
    public Power reducePackege() {
        if (this.coe.equals(BigInteger.ZERO)) {
            return new Power(BigInteger.ZERO);
        }
        if (this.base instanceof Expr && this.exp.equals(BigInteger.ONE)) {
            Factor simpleFactor = this.base.reducePackege();
            if (simpleFactor instanceof Expr) {
                return new Power(this.coe, simpleFactor, this.exp);
            } else {
                return (Power)simpleFactor;
            }
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Power power = (Power) o;
        return Objects.equals(base, power.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base);
    }
}
