package expression;

public abstract class Factor {
    public abstract String toString();

    public abstract Factor substitute(Variable x, Factor factor);

    public abstract Factor reducePackege();
}
