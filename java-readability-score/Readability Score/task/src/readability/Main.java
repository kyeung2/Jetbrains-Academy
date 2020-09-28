package readability;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Arrays.*;

public class Main {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    private static final Map<Integer, String> AGE_UPPER_BOUNDS;

    private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'y');

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
        AGE_UPPER_BOUNDS = Map.ofEntries(
                Map.entry(1, "6"),
                Map.entry(2, "7"),
                Map.entry(3, "9"),
                Map.entry(4, "10"),
                Map.entry(5, "11"),
                Map.entry(6, "12"),
                Map.entry(7, "13"),
                Map.entry(8, "14"),
                Map.entry(9, "15"),
                Map.entry(10, "16"),
                Map.entry(11, "17"),
                Map.entry(12, "18"),
                Map.entry(13, "24"),
                Map.entry(14, "24+")
        );
    }

    private static String getAgeUpperBound(double score) {
        return AGE_UPPER_BOUNDS.get((int) Math.ceil(score));
    }

    public static void main(String[] args) {
        String fileName = args[0];
        try {
            String text = new String(Files.readAllBytes(Paths.get(fileName)));
            System.out.println("The text is:");
            System.out.println(text);
            String[] words = stream(text.split("\\s"))
                    //words tokens such as "use." returned a syllable count of 2.
                    .map(word->word.replaceAll("[!?.]", ""))
                    .toArray(String[]::new);
            int sentenceCount = text.split("[!?.]").length;
            long characterCount = stream(text.split("")).filter(s -> !s.matches("\\s")).count();
            int syllableCount = countSyllables(words);
            long polysyllableCount = countPolysyllables(words);
            int wordCount = words.length;

            System.out.println("Words: " + wordCount);
            System.out.println("Sentences: " + sentenceCount);
            System.out.println("Characters: " + characterCount);
            System.out.println("Syllables: " + syllableCount);
            System.out.println("Polysyllables: " + polysyllableCount);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");

            Scanner scanner = new Scanner(System.in);
            String mode = scanner.nextLine();
            System.out.println();

            switch (mode) {
                case "ARI":
                    printARI(wordCount, sentenceCount, characterCount);
                    break;
                case "FK":
                    printFK(wordCount, sentenceCount, syllableCount);
                    break;
                case "SMOG":
                    printSMOG(sentenceCount, polysyllableCount);
                    break;
                case "CL":
                    printCL(wordCount, sentenceCount, characterCount);
                    break;
                default:
                    double ari = printARI(wordCount, sentenceCount, characterCount);
                    double fk = printFK(wordCount, sentenceCount, syllableCount);
                    double smog = printSMOG(sentenceCount, polysyllableCount);
                    double cl = printCL(wordCount, sentenceCount, characterCount);
                    System.out.println();
                    System.out.println("This text should be understood in average by " + DECIMAL_FORMAT.format(((ari + fk + smog + cl) / 4)) + " year olds.");
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static double getARI(double words, double sentences, double characters) {
        return 4.71 * (characters / words) + 0.5 * (words / sentences) - 21.43;
    }

    private static double printARI(int wordCount, int sentenceCount, long characterCount) {
        double ari = getARI(wordCount, sentenceCount, characterCount);
        System.out.println("Automated Readability Index: " + DECIMAL_FORMAT.format(ari) + " (about " + getAgeUpperBound(ari) + " year olds).");
        return ari;
    }

    public static double getFK(double words, double sentences, double syllables) {
        return 0.39 * (words / sentences) + 11.8 * (syllables / words) - 15.59;
    }

    private static double printFK(int wordCount, int sentenceCount, long syllableCount) {
        double fk = getFK(wordCount, sentenceCount, syllableCount);
        System.out.println("Flesch–Kincaid readability tests: " + DECIMAL_FORMAT.format(fk) + " (about " + getAgeUpperBound(fk) + " year olds).");
        return fk;
    }

    public static double getSMOG(double sentences, double polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * (30.0 / sentences)) + 3.1291;
    }

    private static double printSMOG(int sentenceCount, long polysyllableCount) {
        double smog = getSMOG(sentenceCount, polysyllableCount);
        System.out.println("Simple Measure of Gobbledygook: " + DECIMAL_FORMAT.format(smog) + " (about " + getAgeUpperBound(smog) + " year olds).");
        return smog;
    }

    public static double getCL(double words, double sentences, double characters) {
        double L = (characters / words) * 100;
        double S = (sentences / words) * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    private static double printCL(int wordsCount, int sentenceCount, long characterCount) {
        double cl = getCL(wordsCount, sentenceCount, characterCount);
        System.out.println("Coleman–Liau index: " + DECIMAL_FORMAT.format(cl) + " (about " + getAgeUpperBound(cl) + " year olds).");
        return cl;
    }

    // I know it's bad practice to return a Stream, don't want to spend more time on this tbh :)
    private static IntStream getSyllableIntStream(String[] words) {
        return stream(words)
                .mapToInt(word -> {
                    String formattedWord = word.endsWith("e") ? word.substring(0, word.length() - 1).toLowerCase() : word.toLowerCase();
                    boolean previouslyFoundVowel = false;
                    int syllableCount = 0;
                    // saw the following regex in another answer, didn't test though.
                    // ([aeiouyAEIOUY][^aeiouyAEIOUY\s]|[aiouyAIOUY]$) I hope you would find out how to use it...
                    for (char next : formattedWord.toCharArray()) {
                        if (VOWELS.contains(next)) {
                            if (!previouslyFoundVowel) {
                                syllableCount++;
                            }
                            previouslyFoundVowel = true;
                        } else {
                            previouslyFoundVowel = false;
                        }
                    }

//                    System.out.println(word + "=" + (syllableCount > 0 ? syllableCount : 1));
                    return syllableCount > 0 ? syllableCount : 1;
                });
    }

    private static int countSyllables(String[] words) {
        return getSyllableIntStream(words)
                .sum();
    }

    private static long countPolysyllables(String[] words) {
        return getSyllableIntStream(words)
                .filter(syllableCount -> syllableCount > 2)
                .count();

    }
}