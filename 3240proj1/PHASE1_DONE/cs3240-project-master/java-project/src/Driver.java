import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import DFA.DFA;
import DFA.DFAState;
import NFA.NFA;
import NFA.NFAState;

/**
 * This is the driver for our scanner generator. This is where program execution
 * begins.
 * @author Surenkumar Nihalani
 */
public class Driver {

  /**
   * @param args
   *        Can specify --specs-file, --input-file, --output-file
   */
  public static void main(String[] args) {
    String specificationFilename = "", inputFilename = "", outputFileName = "";
    for (int i = 0; i < args.length; i++) {
      String currentArgument = args[i];
      if (currentArgument.equals("--specs-file")) {
        specificationFilename = args[++i];
      } else if (currentArgument.equals("--input-file")) {
        inputFilename = args[++i];
      } else if (currentArgument.equals("--output-file")) {
        outputFileName = args[++i];
      } else {
        System.err.println("Unknown argument: " + args[i]);
        System.exit(1);
      }
    }
//    System.out.println(specificationFilename);
    try {
//      ArrayList<NFAState> all = NFAState
//          .allStates;
      NFA stuffToMatch = NFA.getNFAFromSpecFile(new Scanner(new File(
          specificationFilename)));
      DFA leDFA = DFA.getDFAFromNFA(stuffToMatch);
//      List<DFAState> DFAStates = DFAState.dbug;
      DFAState[][] table = DFA.getDFATable(leDFA);
      PrintStream ps = new PrintStream(new File("table"));
      DFA.printTable(table, ps);
      ps.close();
      DFAState[][] copy = DFA
          .getTableFromScanner(new Scanner(new File("table")));
      Scanner inputFileScanner = new Scanner(new File(inputFilename));
      PrintStream outputFileWriter = new PrintStream(new File(outputFileName));
      while (inputFileScanner.hasNextLine()) {
        String currentLine = inputFileScanner.nextLine();
        if (currentLine == null || currentLine == "" || currentLine.equals("")) {
          System.out.println("Skipping: " + currentLine);
          continue;
        }
        TableWalker.printTokens(leDFA.getStartState(), copy, outputFileWriter,
            currentLine);
      }
      outputFileWriter.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("Couldn't find specifications file.");
    }
  }
}
