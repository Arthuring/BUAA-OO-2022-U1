package expression;

import java.math.BigInteger;

public class Number extends Factor {
    private BigInteger number;

    public BigInteger number() {
        return this.number;
    }

    public Number(String number) {
        this.number = new BigInteger(number);
    }

    public Number(BigInteger number) {
        this.number = number;
    }

    public String toString() {
        return number.toString();
    }

    public Factor simplify() {
        return new Number(this.number());
    }

    public Factor extend() {
        return null;
    }

    public Factor substitute(Variable x, Factor factor) {
        return null;
    }
}
