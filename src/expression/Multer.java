package expression;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Multer {
    public static ArrayList<BasicTerm> mult(BasicTerm a, BasicTerm b) {
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

    public static ArrayList<BasicTerm>
        mult(ArrayList<BasicTerm> vars, BasicTerm var) throws Exception {
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

    public static ArrayList<BasicTerm> mult(ArrayList<BasicTerm> vars1,
                                            ArrayList<BasicTerm> vars2)
            throws Exception {
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

}
