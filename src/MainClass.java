import expression.Expr;
import expression.Func;
import parser.Parser;
import parser.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oocourse.spec3.ExprInput;
import com.oocourse.spec3.ExprInputMode;

public class MainClass {
    public static void main(String[] args) throws Exception {
        //Scanner scanner = new Scanner(System.in);
        //Scanner scanner = new Scanner(System.in);
        ExprInput scanner = new ExprInput(ExprInputMode.NormalMode);
        //int time = Integer.parseInt(scanner.nextLine());
        int time = scanner.getCount();
        Func func = new Func();
        for (int i = 0; i < time; i++) {
            String function = scanner.readLine();
            function = simplifyString(function);
            String[] functionBuilder = function.split("=");
            Token token1 = new Token(functionBuilder[0]);
            Token token2 = new Token(functionBuilder[1]);
            Parser parser = new Parser(token2);
            Expr expr = parser.parseExpr(Parser.Mod.FUNC_DEFINE);
            func.funcDefine(token1, expr);
        }
        String input = scanner.readLine();
        input = simplifyString(input);
        Token token = new Token(input);
        Parser parser = new Parser(token, func);
        Expr expr;
        try {
            expr = parser.parseExpr(Parser.Mod.EXPR_CULC);
            expr.calculate();
            expr.simplify();
            expr.simlifyTri();
            expr.toAnswer();
            expr.betterTwoTri();
            String choise1 = expr.toString();
            String choise2 = expr.toStingBetterwTir();
            String out;
            if (choise1.length() <= choise2.length()) {
                out = choise1;
            } else {
                out = choise2;
            }
            out = simplifyString(out);
            token = new Token(out);
            parser = new Parser(token, func);
            expr = parser.parseExpr(Parser.Mod.EXPR_CULC);
            expr.calculate();
            expr.simplify();
            expr.simlifyTri();
            expr.toAnswer();
            out = expr.toString();
            out = simplifyString(out);
            System.out.println(out);
        } catch (Exception e) {
            //System.out.println(e);
        }
    }

    public static String simplifyString(String input) {
        String inputs = input.replaceAll(" ", "");
        String stringss = inputs.replaceAll("\t", "");
        Pattern pattern = Pattern.compile("(\\+-)+|(-\\+)+|(\\+\\+)+|(--)+");
        Matcher matcher = pattern.matcher(stringss);
        while (matcher.find()) {
            stringss = stringss.replaceAll("\\+-", "-");
            stringss = stringss.replaceAll("-\\+", "-");
            stringss = stringss.replaceAll("\\+\\+", "+");
            stringss = stringss.replaceAll("--", "+");
            matcher = pattern.matcher(stringss);
        }
        return stringss;
    }
}
