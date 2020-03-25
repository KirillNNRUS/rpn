package rpn;

import java.util.Stack;

final class Expr {
    private final static long LONG_MIN = Long.MIN_VALUE / 10L;
    private final static long LONG_MAX = Long.MAX_VALUE / 10L;


    public static long Calc(String s) throws Exception {
        s = s.replaceAll("\\s+", ""); // удалим пробелы, '\n', '\t'...

        if (s.length() == 0)
            throw new IllegalArgumentException("Строка пуста!");
        if (!is_rules(s))
            throw new IllegalArgumentException("Ошибка в расстоновки скобок!");

        int[] index = {0};
        return SubExpr(s, index);
    }

    ///////////////////////////////////////////////////////////////////////////

    private static long SubExpr(String s, int[] index) {
        boolean neg, loop;
        long val, num;
        char ch;
        Stack<Character> sops = new Stack<Character>();
        Stack<Long> sval = new Stack<Long>();

        neg = false;
        loop = true;

        for (int i = index[0]; i < s.length(); ) {

            switch (s.charAt(i)) {
                case '+':
                case '/':
                case '*':
                    sops.push(s.charAt(i));
                    ++i;
                    break;
                case '-':

                    if ((i == 0) || ((i > 0) && is_math(s.charAt(i - 1)))) {
                        neg = true;
                        ++i;
                        break;
                    }
                    sops.push(s.charAt(i));
                    ++i;

                    break;
                case '(':

                    index[0] = i + 1;
                    val = SubExpr(s, index);
                    i = index[0];

                    if (neg)
                        val = 0L - val;
                    neg = false;

                    sval.push(val);
                    calc_muldiv(sval, sops);
                    break;
                case ')':
                    index[0] = i + 1;
                    loop = false;
                    break;
                default:

                    if (!Character.isDigit(s.charAt(i)))
                        throw new NumberFormatException("Неизвестный символ!");

                    index[0] = i;
                    val = ToLong(s, index);
                    i = index[0];

                    if (neg)
                        val = 0L - val;
                    neg = false;

                    sval.push(val);
                    calc_muldiv(sval, sops);
                    break;
            }

            if (!loop)
                break;
        }


        if (sval.size() == 0)
            throw new IllegalArgumentException("Ошибка синтаксиса!");

        num = sval.get(0);
        sval.remove(0);

        for (int i = 0; (i < sops.size()) && (i < sval.size()); ++i) {
            switch (sops.get(i)) {
                case '+':
                    num += sval.get(i);
                    break;
                case '-':
                    num -= sval.get(i);
                    break;
            }
        }

        sops.clear();
        sval.clear();
        sops = null;
        sval = null;
        return num;
    }


    //приоритетные операции: умножение, деление
    private static void calc_muldiv(Stack<Long> sval, Stack<Character> sops) {
        long val;
        char ch;

        while (!sops.isEmpty()) {
            ch = sops.peek();
            if ((ch == '+') || (ch == '-'))
                break;

            val = sval.peek();
            sval.pop();
            if (sval.size() == 0)
                throw new IllegalArgumentException("Ошибка синтаксиса!");

            if (ch == '*')
                sval.set(sval.size() - 1, sval.peek() * val);
            else if (ch == '/') {
                if (val == 0L)
                    throw new ArithmeticException("Деление на нуль!");
                sval.set(sval.size() - 1, sval.peek() / val);
            }
            sops.pop();
        }
    }


    // конвертирование числа из строки
    private static long ToLong(String s, int[] index) {
        long n = 0L;
        int i;

        for (i = index[0]; i < s.length(); ++i) {
            if (Character.isDigit(s.charAt(i))) {

                if (n > LONG_MAX || n < LONG_MIN)
                    throw new NumberFormatException("Переполнение числа!");

                n = n * 10L + (long) (s.charAt(i) - '0');
            } else
                break;
        }
        index[0] = i;
        return n;
    }

    //проверка правильности расстановки скобочных выражений
    private static boolean is_rules(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '(')
                ++n;
            else if (s.charAt(i) == ')')
                --n;
        }
        return (n == 0);
    }


    private static boolean is_math(char ch) {
        return (ch == '+' || ch == '-' ||
                ch == '*' || ch == '/' || ch == '(');
    }
}


class Sec {
    public static void main(String[] args) {
        String s = "2*-(-100*100-(-(-(-(10-2)/-2*-3+1)/4)+2)+2012/(1+(-(-2)+3+(-4))+5))+1";
//        long   n = 2*-(-100*100-(-(-(-(10-2)/-2*-3+1)/4)+2)+2012/(1+(-(-2)+3+(-4))+5))+1;

        try {
            long res = Expr.Calc(s);
            System.out.println(res);
//            System.out.println(n);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
