package expression;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class BasicTerm {
    private final Map<Power, BigInteger> hashfactors;
    private final HashSet<Power> ans = new HashSet<>();
    private final BigInteger coe;

    public BasicTerm(Power power) {
        if (power.isConst()) {
            coe = power.getCoe();
            this.hashfactors = new HashMap<>();
        } else if (power.getBase() instanceof Variable
                || power.getBase() instanceof Sin
                || power.getBase() instanceof Cos) {
            hashfactors = new HashMap<>();
            hashfactors.put(power, power.getExp());
            this.coe = power.getCoe();
        } else {
            this.coe = BigInteger.ONE;
            this.hashfactors = new HashMap<>();
        }
    }

    public BasicTerm(Map<Power, BigInteger> hashfactors, BigInteger coe) {
        this.coe = coe;
        this.hashfactors = hashfactors;
    }

    public BasicTerm() {
        this.coe = BigInteger.ONE;
        this.hashfactors = new HashMap<>();
    }

    public BigInteger getCoe() {
        return this.coe;
    }

    public HashMap<Power, BigInteger> getHashfactors() {
        HashMap<Power, BigInteger> factors = new HashMap<>();
        for (Map.Entry<Power, BigInteger> item : hashfactors.entrySet()) {
            Power power = item.getKey();
            BigInteger exp = item.getValue();
            factors.put(power, exp);
        }
        return factors;
    }

    public String toString() {
        Iterator<Map.Entry<Power, BigInteger>> it;
        Power zero = new Power(BigInteger.ZERO);
        Sin sin0 = new Sin(zero);
        Cos cos0;
        cos0 = new Cos(zero);
        for (it = hashfactors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Power, BigInteger> item = it.next();
            if (item.getKey().getBase().equals(sin0)) {
                return "0";
            }
            if (!item.getKey().getBase().equals(cos0)) {
                Power p = new Power(BigInteger.ONE, item.getKey().getBase(), item.getValue());
                ans.add(p);
            }
        }
        StringBuilder sb = new StringBuilder();
        StringJoiner sj = new StringJoiner("*");
        if (this.coe.equals(BigInteger.ZERO)) {
            return "0";
        } else if (ans.isEmpty()) {
            return this.coe.toString();
        } else if (!this.coe.equals(BigInteger.ONE)) {
            if (this.coe.equals(BigInteger.ONE.negate())) {
                sb.append("-");
            } else {
                sj.add(coe.toString());
            }
        }
        for (Power p : ans) {
            sj.add(p.toString());
        }
        sb.append(sj);
        return sb.toString();
    }

    public Power containSinCos2() {
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

    public HashMap<Power, BigInteger> getFectorExcept(Power power) {
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

    public boolean canReducePackage() {
        if (this.coe.equals(BigInteger.ONE) || this.coe.equals(BigInteger.ZERO)) {
            return this.hashfactors.size() == 1;
        } else {
            return false;
        }
    }

    public Factor reducePackege() {
        Iterator<Map.Entry<Power, BigInteger>> iterator = hashfactors.entrySet().iterator();
        if (this.coe.equals(BigInteger.ZERO)) {
            return new Power(BigInteger.ZERO);
        } else if (iterator.hasNext()) {
            Map.Entry<Power, BigInteger> item = iterator.next();
            return new Power(BigInteger.ONE, item.getKey().getBase().reducePackege(),
                    item.getValue());
        } else {
            return new Power(BigInteger.ONE);
        }
    }

    public BasicTerm betterTwoTri() {
        /*TODO:dedign the method to solve*/
        Power inner;
        BigInteger coe = this.coe;
        Map<Power, BigInteger> newFactors = new HashMap<>();
        while ((inner = containsTriPair(coe)) != null) {
            Power sin = new Power(new Sin(inner));
            Power cos = new Power(new Cos(inner));
            BigInteger sinExp = hashfactors.get(sin);
            BigInteger cosExp = hashfactors.get(cos);
            hashfactors.remove(sin);
            hashfactors.remove(cos);
            Power newInner = getNewinner(inner);
            Power newSin = new Power(new Sin(newInner));
            if (sinExp.compareTo(cosExp) > 0) {
                hashfactors.put(sin, sinExp.subtract(cosExp));
                hashfactors.put(newSin, cosExp);
                coe = coe.divide(BigInteger.valueOf(2));
            } else if (sinExp.compareTo(cosExp) < 0) {
                hashfactors.put(cos, cosExp.subtract(sinExp));
                hashfactors.put(newSin, sinExp);
                coe = coe.divide(BigInteger.valueOf(2));
            } else {
                hashfactors.put(newSin, sinExp);
                coe = coe.divide(BigInteger.valueOf(2));
            }
        }
        Iterator<Map.Entry<Power, BigInteger>> it;
        for (it = hashfactors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Power, BigInteger> item = it.next();
            newFactors.put(item.getKey(), item.getValue());
        }
        return new BasicTerm(newFactors,coe);
    }

    public Power containsTri() {
        Iterator<Map.Entry<Power, BigInteger>> it;
        for (it = hashfactors.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Power, BigInteger> item = it.next();
            if (item.getKey().getBase() instanceof Sin
                    || item.getKey().getBase() instanceof Cos) {
                return new Power(BigInteger.ONE, item.getKey().getBase(), BigInteger.ONE);
            }
        }
        return null;
    }

    public Power containsTriPair(BigInteger coe) {
        /*TODO: find TriPair and return thire base*/
        Iterator<Map.Entry<Power, BigInteger>> it;
        it = hashfactors.entrySet().iterator();
        if (!coe.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            return null;
        }
        while (it.hasNext()) {
            Map.Entry<Power, BigInteger> item = it.next();
            if (item.getKey().getBase() instanceof Sin
                    || item.getKey().getBase() instanceof Cos) {
                Power power = item.getKey();
                Power powerPar;
                if (power.getBase() instanceof Sin) {
                    powerPar = new Power(new Cos(((Sin) power.getBase()).getInner()));
                    if (hashfactors.containsKey(powerPar)) {
                        return ((Sin) power.getBase()).getInner();
                    }
                } else {
                    powerPar = new Power(new Sin(((Cos) power.getBase()).getInner()));
                    if (hashfactors.containsKey(powerPar)) {
                        return ((Cos) power.getBase()).getInner();
                    }
                }
            }

        }
        return null;
    }

    public Power getNewinner(Power inner) {
        Power two = new Power(BigInteger.valueOf(2));
        Term term = new Term();
        term.addFactor(two);
        term.addFactor(inner);
        Expr expr = new Expr();
        expr.addTerm(term);
        expr.analise();
        Power newInner = new Power(BigInteger.ONE, expr, BigInteger.ONE);
        newInner.analyse();
        return newInner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicTerm basicTerm = (BasicTerm) o;
        return Objects.equals(hashfactors, basicTerm.hashfactors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashfactors);
    }
}
