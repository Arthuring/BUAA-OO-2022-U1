package expression;

import java.util.Objects;

public class Sin extends Factor {
    private final Power inner;
    private final String info;

    public Sin(Power inner) {
        Power simpInner;
        //this.inner = inner;
        inner.analyse();
        simpInner = inner.reducePackege();
        this.inner = simpInner;
        this.info = inner.toStringInSin();
    }

    public Power getInner() {
        return this.inner;
    }

    public String toString() {
        String sb = "sin(" +
                inner.toStringInSin() +
                ")";
        return sb;
    }

    public Sin substitute(Variable x, Factor factor) {
        return new Sin(inner.substitute(x, factor));
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
        Sin sin = (Sin) o;
        return Objects.equals(info, sin.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }
}
