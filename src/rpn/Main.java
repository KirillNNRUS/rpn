package rpn;

import java.util.*;

class ExpressionParser {
    private static String operators = "+-*/^";
    private static String delimiters = "() " + operators;
    public static boolean flag = true;

    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isOperator(String token) {
        if (token.equals("u-")) return true;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(0) == operators.charAt(i)) return true;
        }
        return false;
    }

    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/") || token.equals("^")) return 3;
        return 4;
    }

    public static List<String> parse(String infix) {
        List<String> listValue = new ArrayList<>();
        Deque<String> stackOperators = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr;
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Некорректное выражение.");
                flag = false;
                return listValue;
            }
            if (curr.equals(" ")) continue;
            else if (isDelimiter(curr)) {
                if (curr.equals("(")) stackOperators.push(curr);
                else if (curr.equals(")")) {
                    while (!stackOperators.peek().equals("(")) {
                        listValue.add(stackOperators.pop());
                        if (stackOperators.isEmpty()) {
                            System.out.println("Скобки не согласованы.");
                            flag = false;
                            return listValue;
                        }
                    }
                    stackOperators.pop();
                } else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev) && !prev.equals(")")))) {
                        curr = "u-";
                    } else {
                        while (!stackOperators.isEmpty() && (priority(curr) <= priority(stackOperators.peek()))) {
                            listValue.add(stackOperators.pop());
                        }
                    }
                    stackOperators.push(curr);
                }

            } else {
                listValue.add(curr);
            }
            prev = curr;
        }

        while (!stackOperators.isEmpty()) {
            if (isOperator(stackOperators.peek())) listValue.add(stackOperators.pop());
            else {
                System.out.println("Скобки не согласованы.");
                flag = false;
                return listValue;
            }
        }
        return listValue;
    }
}

class Ideone {
    public static Double calc(List<String> value) {
        Deque<Double> stackOperators = new ArrayDeque<>();
        for (String x : value) {
            if (x.equals("+")) {
                stackOperators.push(stackOperators.pop() + stackOperators.pop());
            } else if (x.equals("^")) {
                Double b = stackOperators.pop(), a = stackOperators.pop();
                stackOperators.push(Math.pow(a, b));
            } else if (x.equals("-")) {
                Double b = stackOperators.pop(), a = stackOperators.pop();
                stackOperators.push(a - b);
            } else if (x.equals("*")) {
                stackOperators.push(stackOperators.pop() * stackOperators.pop());
            } else if (x.equals("/")) {
                Double b = stackOperators.pop(), a = stackOperators.pop();
                stackOperators.push(a / b);
            } else if (x.equals("u-")) {
                stackOperators.push(-stackOperators.pop());
            } else {
                stackOperators.push(Double.valueOf(x));
            }
        }
        return stackOperators.pop();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//    String s = "( -7 ) ^ - 3 + ( ( 5 + 0.3 ) - ( (-7) + (-3) ) )";
        String s = "78.1 / 3";
        ExpressionParser n = new ExpressionParser();
        List<String> expression = n.parse(s);
        boolean flag = n.flag;
        if (flag) {
            for (String x : expression) System.out.print(x + " ");
            System.out.println();
            System.out.println(calc(expression));
        }
    }
}
