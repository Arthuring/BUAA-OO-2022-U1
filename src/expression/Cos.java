package expression;

import java.util.Objects;

public class Cos extends Factor {
    private final Power inner;
    private final String info;

    public Cos(Power inner) {
        Power simpInner;
        //this.inner = inner;
        inner.analyse();
        simpInner = inner.reducePackege();
        this.inner = simpInner;
        this.info = simpInner.toStringInCos();
    }

    public Power getInner() {
        return this.inner;
    }

    public String toString() {
        return "cos(" +
                inner.toStringInCos() +
                ")";
    }

    public Cos substitute(Variable x, Factor factor) {
        return new Cos(inner.substitute(x, factor));
    }

    @Override
    public Factor reducePackege() {
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
        Cos cos = (Cos) o;
        return Objects.equals(inner.toStringInCos(), cos.inner.toStringInCos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(inner.toStringInCos());
    }
}
