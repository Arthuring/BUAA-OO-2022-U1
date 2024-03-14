import expression.Expr;
import expression.Func;
import parser.Parser;
import parser.Token;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int cnt = 0;
        String input;
        PrintWriter outputStream;
        int dataNum = 0;
        for (cnt = 6; cnt < 7; cnt = cnt + 1) {
            System.out.println("File" + cnt);
            dataNum = 0;
            FileReader reader = new FileReader("D:\\courses\\gradeTwoSpring\\OO\\Homework3\\TestData" +
                    "\\interestingData" + cnt + ".in");
            outputStream = new PrintWriter(new FileOutputStream("D:\\courses\\gradeTwoSpring\\OO\\Homework3\\TestData\\stdout" + cnt + ".txt"));
            BufferedReader inputStream = new BufferedReader(reader);
            while ((input = inputStream.readLine()) != null) {
                dataNum += 1;
                System.out.println("Test:" + dataNum);
                if (dataNum % 5000 == 0) {
                    System.out.println("You have test " + dataNum + "data");
                }
                int time = Integer.valueOf(input);
                //System.out.println(time);
                Func func = new Func();
                for (int i = 0; i < time; i++) {
                    String function = inputStream.readLine();
                    //System.out.println(function);
                    function = simplifyString(function);
                    String[] functionBuilder = function.split("=");
                    Token token1 = new Token(functionBuilder[0]);
                    Token token2 = new Token(functionBuilder[1]);
                    Parser parser = new Parser(token2);
                    Expr expr = parser.parseExpr(Parser.Mod.FUNC_DEFINE);
                    func.funcDefine(token1, expr);
                }
                input = inputStream.readLine();
                //System.out.println(input);
                input = simplifyString(input);
                Token token = new Token(input);
                Parser parser = new Parser(token,func);
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
                        //outputStream.println("chose no 2tri");
                    } else {
                        out = choise2;
                        //outputStream.println("chose 2tri");
                    }
                    out = simplifyString(out);
                    token = new Token(out);
                    parser = new Parser(token,func);
                    expr = parser.parseExpr(Parser.Mod.EXPR_CULC);
                    expr.calculate();
                    expr.simplify();
                    expr.simlifyTri();
                    expr.toAnswer();
                    out = expr.toString();
                    out = simplifyString(out);
                    //System.out.println(out);
                    outputStream.println(out);
                    //System.out.println(out);
                } catch (Exception e) {
                    System.out.println("file " + cnt);
                    System.out.println("line" + dataNum);
                    System.out.println(e);
                    System.out.println("wrong at : "+input);
                }
            }
            outputStream.close();
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
