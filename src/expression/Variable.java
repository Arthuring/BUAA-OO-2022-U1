package expression;

import java.util.Objects;

public class Variable extends Factor {
    public enum Type {
        x, y, z, i
    }

    private final Type name;

    public Variable() {
        this.name = Type.x;
    }

    public Variable(Type name) {
        this.name = name;
    }

    public Variable(String name) {
        this.name = Type.valueOf(name);
    }

    public Variable.Type getName() {
        return this.name;
    }

    public Variable substitute(Variable x, Factor factor) {
        return null;
    }

    public String toString() {
        return this.name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Variable variable = (Variable) o;
        return name == variable.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
