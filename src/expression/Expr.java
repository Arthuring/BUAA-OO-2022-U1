package expression;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

public class Expr extends Factor {
    private final HashSet<Term> terms = new HashSet<>();
    private final ArrayList<BasicTerm> variables = new ArrayList<>();
    private final Map<Map<Power, BigInteger>, BigInteger> simplifyer = new HashMap<>();
    private final Map<Map<Power, BigInteger>, BigInteger> simplifyerTri = new HashMap<>();
    private final ArrayList<BasicTerm> answer = new ArrayList<>();

    public void addTerm(Term factor) {
        terms.add(factor);
    }

    public Expr(ArrayList<BasicTerm> variables) throws Exception {
        this.variables.addAll(variables);
        calculate();
        simplify();
        simlifyTri();
        toAnswer();
    }

    public Expr() {

    }

    public String toString() {
        StringJoiner sj = new StringJoiner("+");
        for (BasicTerm basicTerm : answer) {
            if (!basicTerm.getCoe().equals(BigInteger.ZERO)) {
                if (!basicTerm.toString().equals("0")) {
                    sj.add(basicTerm.toString());
                }
            }
        }
        if (sj.toString().equals("")) {
            sj.add("0");
        }
        return sj.toString();
    }

    public void simplify() {
        for (BasicTerm v : variables) {
            simplifyer.merge(v.getHashfactors(), v.getCoe(),
                    BigInteger::add);
        }
    }

    public void simlifyTri() {
        Iterator<Map.Entry<Map<Power, BigInteger>, BigInteger>> it;
        Iterator<Map.Entry<Map<Power, BigInteger>, BigInteger>> exceptItem;
        it = simplifyer.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Map<Power, BigInteger>, BigInteger> item = it.next();
            Power power = containSinCos2(item.getKey());
            if (power != null) {
                Power powerPar;
                if (power.getBase() instanceof Sin) {
                    powerPar = new Power(new Cos(((Sin) power.getBase()).getInner()));
                } else {
                    powerPar = new Power(new Sin(((Cos) power.getBase()).getInner()));
                }
                int flag = 0;
                simplifyer.remove(item.getKey());
                for (exceptItem = simplifyer.entrySet().iterator(); exceptItem.hasNext(); ) {
                    Map.Entry<Map<Power, BigInteger>, BigInteger> item2 = exceptItem.next();
                    if (item2.getKey().containsKey(powerPar) &&
                            item2.getKey().get(powerPar).equals(BigInteger.valueOf(2))) {
                        HashMap<Power, BigInteger> excpept1 = getFectorExcept(power, item.getKey());
                        HashMap<Power, BigInteger> excpept2 =
                                getFectorExcept(powerPar, item2.getKey());
                        if (excpept1.equals(excpept2)) {
                            simplifyer.remove(item2.getKey());
                            if (item.getValue().compareTo(item2.getValue()) > 0) {
                                simplifyerTri.merge(item.getKey(), item.getValue()
                                        .subtract(item2.getValue()), BigInteger::add);
                                simplifyerTri.merge(excpept1, item2.getValue(), BigInteger::add);
                            } else {
                                simplifyerTri.merge(item2.getKey(), item2.getValue().
                                        subtract(item.getValue()), BigInteger::add);
                                simplifyerTri.merge(excpept1, item.getValue(), BigInteger::add);
                            }
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag == 0) {
                    simplifyerTri.merge(item.getKey(), item.getValue(), BigInteger::add);
                }
            } else {
                simplifyerTri.merge(item.getKey(), item.getValue(), BigInteger::add);
                simplifyer.remove(item.getKey());
            }
            it = simplifyer.entrySet().iterator();
        }

    }

    public ArrayList<BasicTerm> calculate() throws Exception {
        for (Term t : terms) {
            variables.addAll(t.calculate());
        }
        return variables;
    }

    public Expr substitute(Variable x, Factor factor) {
        Expr expr = new Expr();
        for (Term t : terms) {
            expr.addTerm(t.substitute(x, factor));
        }
        return expr;
    }

    public String testInput() {
        StringJoiner sj = new StringJoiner("+");
        for (Term t : terms) {
            sj.add(t.toString());
        }
        return sj.toString();
    }

    public void toAnswer() {
        Iterator<Map.Entry<Map<Power, BigInteger>, BigInteger>> it;
        for (it = simplifyerTri.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Map<Power, BigInteger>, BigInteger> item = it.next();
            BasicTerm b = new BasicTerm(item.getKey(), item.getValue());
            answer.add(b);
        }
        answer.sort((o1, o2) -> o2.getCoe().compareTo(o1.getCoe()));
    }

    public Power containSinCos2(Map<Power, BigInteger> hashfactors) {
        Iterator<Map.Entry<Power, BigInteger>> it;
        for (it = hashfactors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Power, BigInteger> item = it.next();
            if (item.getKey().getBase() instanceof Sin
                    || item.getKey().getBase() instanceof Cos) {
                if (item.getValue().equals(BigInteger.valueOf(2))) {
                    return new Power(BigInteger.ONE, item.getKey().getBase(), BigInteger.ONE);
                }
            }
        }
        return null;
    }

    public HashMap<Power, BigInteger> getFectorExcept(Power power, Map<Power,
            BigInteger> hashfactors) {
        HashMap<Power, BigInteger> ans = new HashMap<>();
        Iterator<Map.Entry<Power, BigInteger>> it;
        for (it = hashfactors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Power, BigInteger> item = it.next();
            if (!item.getKey().equals(power)) {
                ans.put(item.getKey(), item.getValue());
            }
        }
        return ans;
    }

    public void analise() throws Exception {
        calculate();
        simplify();
        simlifyTri();
        toAnswer();
    }

    public ArrayList<BasicTerm> getAnswer() {
        return new ArrayList<>(this.answer);
    }
}
