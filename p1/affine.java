import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

public class affine {
  /*
   * If the pair
   * of integers (a, b) is a valid encryption key (see exercise 7.1, the utility
   * should encrypt the text in the file at the path specified by the user in pa-
   * rameter [plaintext-file] and output the ciphertext to the file specified
   * in [output-file]. If (a, b) is not a valid key, then the utility should print
   * the following message (with the user-supplied values for a and b in place of
   * 63
   * {a} and {b}), The key pair ({a}, {b}) is invalid, please select
   * another key.
   */
  private static void encrypt(File input, File output, int a, int b) throws IOException {
    FileWriter fw = new FileWriter(output);
    FileReader fr = new FileReader(input);

    int r;
    while ((r = fr.read()) != -1) {
      int out = (a * r + b) % 128;
      fw.write((char) out);
    }
    fw.close();
    fr.close();
  }

  /*
   * If the
   * pair of integers (a, b) is a valid encryption key (see exercise 7.1, the
   * utility
   * should decrypt the text in the file at the path specified by the user in pa-
   * rameter [ciphertext-file] and output the plaintext to the file specified
   * in [output-file]. If (a, b) is not a valid key, then the utility should print
   * the following message (with the user-supplied values for a and b in place of
   * {a} and {b}), The key pair ({a}, {b}) is invalid, please select
   * another key
   */
  private static void decrypt(File input, File output, int a, int b) throws IOException {
    FileWriter fw = new FileWriter(output);
    FileReader fr = new FileReader(input);

    int r;
    int inverse = inverseA(a);

    while ((r = fr.read()) != -1) {
      // m≡(a^−1⋅(r−b))mod128
      int out = (inverse * ((r - b+ 128) % 128)) %128 ;
      fw.write((char) out);
    }
    fw.close();
    fr.close();
  }

  /*
   * The program should load the specified ciphertext from [ciphertext-file]
   * and attempt find the key pair (a, b) that was used to encrypt the file. To
   * do this, you should search the possible set of key pairs (a, b), decrypt the
   * file using each key-pair and then count the number of valid words you
   * get in the resulting decrypted text. A word is valid if it is contained in
   * the dictionary file specified by the [dictionary-file] parameter. The
   * [dictionary-file] will contain a bunch of words, one word to a line.
   * You should find the pair (a, b) that maximizes the number of valid word
   * matches. The first line of your output file should list the key pair (a, b)
   * in
   * the format "{a} {b}", i.e. a and b appear on the same line separated by a
   * space. Then a line with the text "DECRYPTED MESSAGE:", followed by the
   * decrypted message on the next line.
   */
  private static void decipher(File input, File output, File dict) throws IOException {
    FileWriter fw = new FileWriter(output);
    BufferedReader fr = new BufferedReader(new FileReader(input));
    fr.mark(10000);
    Scanner fDict = new Scanner(dict);
    StringBuilder sb = new StringBuilder();
    HashSet<String> set = new HashSet<String>();
    int possA[] = new int[64];

    int g = 1;
    for (int k = 0; k < 64; k++) {
      possA[k] = g;
      g += 2;
    }
    // step one: put diction in hashset
    // decyprt then tokenize words cheking current a&b
    // if word is in dictionary +1 to word count
    // max word vs current word count
    // after testing create a threshold

    // Step 1

    String word;
    while (fDict.hasNextLine()) {
      word = fDict.nextLine();
      // System.out.println(word);
      set.add(word.toLowerCase());
    }

    int maxWords = Integer.MIN_VALUE;
    int currWords = 0;
    int currB = 0, a = 1, b = 1;
    int i = 0;

    while (i < 63) {
      // System.out.println("loop");
      // step 2

      int r;
      int inverse = inverseA(possA[i]);
      while ((r = fr.read()) != -1) {
        // m≡(a^−1⋅(r−b))mod128
        int out = (inverse * ((r - currB+ 128) % 128)) %128 ;
        sb.append((char) out);
      }

      // System.out.println(sb.toString());
      String[] st = sb.toString().split("[,\\s\\.]");
      //System.out.println(st.length);
      for (int j = 0; j < st.length; j++) {
        if (set.contains(st[j].toLowerCase()) && st[j].length() >= 3) { // error is here
          //System.out.println(st[j]);
          currWords++;
        }
      }

      // System.out.println(currWords);
      if (currWords > maxWords) {
        maxWords = currWords;
        a = possA[i];
        b = currB;
      }

      // increment values & reset before next loop
      //System.out.println("a: " + possA[i] + " | b: " + currB);

      // increment
      currB++;
      if (currB >= 128) {
        currB = 0;
        i++;
      }

      // reset
      currWords = 0;
      fr.reset();
      sb.delete(0, sb.length());

    }

    // step 3:
    // System.out.println(maxWords);
    // System.out.println(a + " " + b);
    fw.append(a + " " + b);
    fw.append("\nDECRYPTED MESSAGE:\n");

    int r;
    int inverse = inverseA(a);
    while ((r = fr.read()) != -1) {
      int out = (inverse * ((r - b + 128) % 128)) %128 ;
      fw.append((char) out);
    }

    fw.close();
    fr.close();
    fDict.close();
  }

  public static void main(String[] args) {
    // step 1:
    // obtain command arguments
    if (args[0].equals("encrypt")) {

      File input = new File(args[1]);
      File output = new File(args[2]);
      try {
        output.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      int a = Integer.parseInt(args[3]);
      int b = Integer.parseInt(args[4]);
      isValid(a, b);
      try {
        encrypt(input, output, a, b);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (args[0].equals("decrypt")) {

      File input = new File(args[1]);
      File output = new File(args[2]);
      try {
        output.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      int a = Integer.parseInt(args[3]);
      int b = Integer.parseInt(args[4]);
      // System.out.println(inverseA(a));
      isValid(a, b);
      try {
        decrypt(input, output, a, b);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (args[0].equals("decipher")) {
      File input = new File(args[1]);
      File output = new File(args[2]);
      try {
        output.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      File dict = new File(args[3]);
      try {
        decipher(input, output, dict);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

  }

  private static int inverseA(int a) {
    int M = 128;
    int y = 0, x = 1;

    while (a > 1) {
      int q = a / M;

      int t = M;

      M = a % M;
      a = t;
      t = y;

      y = x - q * y;
      x = t;
    }

    if (x < 0)
      x += 128;

    return x;

  }

  private static void isValid(int a, int b) {
    if (a % 2 == 0) {
      System.out.println("The key pair " + "( " + a + " and " + b + ") is invalid, please select another key.");
      System.exit(0);
    }
  }
}
