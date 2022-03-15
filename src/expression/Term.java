package expression;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Term extends Factor {
    private final ArrayList<Power> factors = new ArrayList<>();

    public Term() {

    }

    public void addFactor(Power factor) {
        factors.add(factor);
    }

    public String toString() {
        StringJoiner sj = new StringJoiner("*");
        for (Factor factor : factors) {
            if (factor instanceof Expr) {
                sj.add("(" + factor + ")");
            } else {
                sj.add(factor.toString());
            }
        }
        return sj.toString();
    }

    public ArrayList<BasicTerm> calculate() throws Exception {
        ArrayList<BasicTerm> ans = new ArrayList<>();
        Power one = new Power(BigInteger.ONE, BigInteger.ZERO);
        BasicTerm o = new BasicTerm(one);
        ans.add(o);
        for (Power p : factors) {
            if (p.getBase() instanceof Expr) {
                ArrayList<BasicTerm> vars;
                vars = ((Expr) p.getBase()).getAnswer();
                if (vars.isEmpty()) {
                    vars = ((Expr) p.getBase()).calculate();
                }
                BigInteger times = p.getExp();
                for (BigInteger i = BigInteger.ZERO;
                     i.compareTo(times) < 0; i = i.add(BigInteger.ONE)) {
                    ans = mult(ans, vars);
                }
            } else if (p.getBase() instanceof Variable
                    || p.getBase() instanceof Sin
                    || p.getBase() instanceof Cos) {
                BasicTerm b = new BasicTerm(p);
                ans = mult(ans, b);
            }
        }
        return ans;
    }

    public boolean isVar(Power power) {
        return !(power.getBase() instanceof Expr);
    }

    public ArrayList<BasicTerm> mult(BasicTerm a, BasicTerm b) {
        ArrayList<BasicTerm> vars = new ArrayList<>();
        BigInteger coe = a.getCoe().multiply(b.getCoe());
        HashMap<Power, BigInteger> afactors = a.getHashfactors();
        HashMap<Power, BigInteger> bfactors = b.getHashfactors();
        Map<Power, BigInteger> ans = Stream.concat(afactors.entrySet().stream(),
                bfactors.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue, BigInteger::add));
        BasicTerm var = new BasicTerm(ans, coe);
        vars.add(var);
        return vars;
    }

    public ArrayList<BasicTerm> mult(ArrayList<BasicTerm> vars, BasicTerm var) throws Exception {
        ArrayList<BasicTerm> ans = new ArrayList<>();
        try {
            for (BasicTerm v : vars) {
                ans.addAll(mult(v, var));
            }
        } catch (Exception e) {
            throw e;
        }
        return ans;
    }

    public ArrayList<BasicTerm>
    mult(ArrayList<BasicTerm> vars1, ArrayList<BasicTerm> vars2) throws Exception {
        ArrayList<BasicTerm> ans = new ArrayList<>();
        try {
            for (BasicTerm v : vars2) {
                ans.addAll(mult(vars1, v));
            }
        } catch (Exception e) {
            throw e;
        }
        return ans;
    }

    public Term substitute(Variable x, Factor factor) {
        Term term = new Term();
        for (Power p : factors) {
            term.addFactor(p.substitute(x, factor));
        }
        return term;
    }

    public BasicTerm toBasicTerm(Power power) {
        return new BasicTerm(power);
    }
}
