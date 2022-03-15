package expression;

import java.util.Objects;

public class Cos extends Factor {
    private final Power inner;
    private final String info;

    public Cos(Power inner) {
        this.inner = inner;
        this.info = inner.toStringInCos();
    }

    public Power getInner() {
        return this.inner;
    }

    public String toString() {
        return "cos(" +
                info +
                ")";
    }

    public Cos substitute(Variable x, Factor factor) {
        return new Cos(inner.substitute(x, factor));
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
        return Objects.equals(info, cos.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info);
    }
}
