package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

interface EncryptionDecryptionAlgorithm {
    String run(String target, int shift);

    static EncryptionDecryptionAlgorithm AlgorithmFactory(String mode, String type) {
        String algoName = mode + type;
        switch (algoName) {
            case ("encshift"):
                return new EncryptionShift();
            case ("encunicode"):
                return new EncryptionUnicode();
            case ("decshift"):
                return new DecryptionShift();
            case ("decunicode"):
                return new DecryptionUnicode();
            default:
                System.out.println("Error");
                System.exit(0);
                return null;
        }
    }
}

class EncryptionShift implements EncryptionDecryptionAlgorithm {
    @Override
    public String run(String target, int shift) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            char newLetter = target.charAt(i);
            if (target.charAt(i) >= 97 && target.charAt(i) < 97 + 26) {
                newLetter = (char) ('a' + (target.charAt(i) - 97 + shift) % 26);
            }
            if (target.charAt(i) >= 65 && target.charAt(i) < 65 + 26) {
                newLetter = (char) ('A' + (target.charAt(i) - 65 + shift) % 26);
            }
            ret.append(newLetter);
        }
        return ret.toString();
    }
}

class DecryptionShift implements EncryptionDecryptionAlgorithm {
    @Override
    public String run(String target, int shift) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            char newLetter = target.charAt(i);
            if (target.charAt(i) >= 97 && target.charAt(i) < 97 + 26) {
                newLetter = (char) ('a' + (target.charAt(i) - 97 - shift + 26) % 26);
            }
            if (target.charAt(i) >= 65 && target.charAt(i) < 65 + 26) {
                newLetter = (char) ('A' + (target.charAt(i) - 65 - shift + 26) % 26);
            }
            ret.append(newLetter);
        }
        return ret.toString();
    }
}

class EncryptionUnicode implements EncryptionDecryptionAlgorithm {
    @Override
    public String run(String target, int shift) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            ret.append((char) ((int) target.charAt(i) + shift));
        }
        return ret.toString();
    }
}

class DecryptionUnicode implements EncryptionDecryptionAlgorithm {
    @Override
    public String run(String target, int shift) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            ret.append((char) ((int) target.charAt(i) - shift));
        }
        return ret.toString();
    }
}

public class Main {
    static String findByMode(String mode, String[] args) {
        if (mode.charAt(0) != '-') {
            System.out.println("Error");
            System.exit(0);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(mode)) {
                if (i == args.length - 1 || args[i + 1].charAt(0) == '-') {
                    System.out.println("Error");
                    System.exit(0);
                }
                return args[i + 1];
            }
        }
        return "-";
    }

    static String getmode(String[] args) {
        String ret = findByMode("-mode", args);
        if (ret.equals("-")) {
            ret = "enc";
        }
        return ret;
    }

    static String getdata(String[] args) {
        String ret = findByMode("-data", args);
        if (ret.equals("-")) {
            ret = "";
        }

        String path = getin(args);
        if (ret.length() == 0 && path.length() != 0) {
            File file = new File(path);
            try (Scanner scanner = new Scanner(file)) {
                ret = scanner.nextLine();
            } catch (FileNotFoundException e) {
                System.out.println("Error");
                System.exit(0);
            }
        }
        return ret;
    }

    static int getvalue(String[] args) {
        String ret = findByMode("-key", args);
        if (ret.equals("-")) {
            ret = "0";
        }
        return Integer.parseInt(ret);
    }

    static String getin(String[] args) {
        String ret = findByMode("-in", args);
        if (ret.equals("-")) {
            ret = "";
        }
        return ret;
    }

    static String getOut(String[] args) {
        String ret = findByMode("-out", args);
        if (ret.equals("-")) {
            ret = "";
        }
        return ret;
    }

    static String getAlgo(String[] args) {
        String ret = findByMode("-alg", args);
        if (ret.equals("-")) {
            ret = "shift";
        }
        return ret;
    }

    static void writeAnswer(String destination, String result) {
        if (destination.length() == 0) {
            System.out.println(result);
        } else {
            File f = new File(destination);
            try (FileWriter file = new FileWriter(f, false)) {
                file.write(result);
            } catch (IOException e) {
                System.out.println("Error");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        String mode = getmode(args);
        String inputData = getdata(args);
        String strategy = getAlgo(args);
        String destination = getOut(args);
        int shift = getvalue(args);

        EncryptionDecryptionAlgorithm Algorithm = EncryptionDecryptionAlgorithm.AlgorithmFactory(mode, strategy);
        String result = Algorithm.run(inputData, shift);
        writeAnswer(destination, result);
    }
}