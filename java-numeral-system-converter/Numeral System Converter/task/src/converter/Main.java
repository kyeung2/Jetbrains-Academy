package converter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            convertNumberSystem();
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    private static void convertNumberSystem() {
        Scanner s = new Scanner(System.in);
        int inputRadix = checkRadix(s.nextInt());
        String input = s.next();
        int outputRadix = checkRadix(s.nextInt());
        checkRadix(outputRadix);

        if (input.contains(".")) {
            String[] splits = input.split("\\.");

            String integerDigits = splits[0];
            String fractionDigits = splits[1];
            String outputIntegerPart = getIntegerPart(inputRadix, integerDigits, outputRadix);
            String outputFractionPart = getFractionPart(inputRadix, fractionDigits, outputRadix);
            System.out.printf("%s.%s%n", outputIntegerPart, outputFractionPart);
        } else {
            System.out.println(getIntegerPart(inputRadix, input, outputRadix));
        }
    }

    private static int checkRadix(int radix) {
        if (radix < 1 || radix > 36) {
            throw new IllegalArgumentException("radix outside of range");
        }
        return radix;
    }

    private static String getFractionPart(int inputRadix, String fractionDigits, int outputRadix) {

        double base10Fraction = getBase10Fraction(inputRadix, fractionDigits);
        return getFractionPartOfBase(outputRadix, base10Fraction, 5);
    }

    private static double getBase10Fraction(int inputRadix, String fractionDigits) {
        if (inputRadix != 10) {
            double sum = 0;
            char[] chars = fractionDigits.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                sum += Character.getNumericValue(chars[i]) / Math.pow(inputRadix, i + 1);
            }

            return sum;
        }
        return Double.parseDouble("0." + fractionDigits);

    }

    private static String getFractionPartOfBase(int outputRadix, double base10Fraction, int precision) {
        StringBuilder b = new StringBuilder(precision);
        for (int i = 0; i < precision; i++) {
            double result = base10Fraction * outputRadix;
            int result1Integer = (int) result;
            base10Fraction = result - result1Integer;
            b.append(Character.forDigit(result1Integer, outputRadix));
        }

        return b.toString();
    }

    private static String getIntegerPart(int inputRadix, String input, int outputRadix) {
        int base10 = (inputRadix == 1) ? input.length() : Integer.parseInt(input, inputRadix);
        return (outputRadix == 1) ? "1".repeat(base10) : Long.toString(base10, outputRadix);
    }
}
