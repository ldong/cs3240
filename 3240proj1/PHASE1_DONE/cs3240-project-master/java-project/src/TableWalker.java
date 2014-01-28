import java.io.PrintStream;

import DFA.DFAState;

/**
 * This class handles the table walking. Given a 2D DFAState table, a start
 * state, and a line to tokenize this class will print the tokens on the given
 * line.
 * @author Surenkumar Nihalani
 */
public class TableWalker {

  /**
   * Walks the "table" on the "lineToTokenize" from "startState" and prints it
   * out to p.
   * @param startState
   *        The DFAState to start from
   * @param table
   *        The table to walk
   * @param p
   *        PrintWriter to write to.
   * @param lineToTokenize
   *        The line to tokenize.
   */
  public static
      void
      printTokens(DFAState startState, DFAState[][] table, PrintStream p, String lineToTokenize) {
    String currentToken = "", lastKnownGoodToken = "", lastKnownIdentifier = "";
    int lastKnownGoodState = 0;
    DFAState currentState = startState;
    for (int i = 0; i < lineToTokenize.length(); i++) {
      char currentChar = lineToTokenize.charAt(i);
      if (currentChar == ' '
          && table[currentState.getIdentifier()][currentChar] == null) {
        currentState = startState;
        if (!lastKnownIdentifier.equals("") && !lastKnownGoodToken.equals("")) {
          p.println(lastKnownIdentifier + " " + lastKnownGoodToken);
        }
        lastKnownIdentifier = "";
        lastKnownGoodToken = "";
        currentToken = "";
        continue;
      }
      DFAState nextState = table[currentState.getIdentifier()][currentChar];
      currentToken += currentChar;
      if (nextState == null) {
        p.println(lastKnownIdentifier + " " + lastKnownGoodToken);
        lastKnownIdentifier = "";
        lastKnownGoodToken = "";
        currentToken = "";
        i = lastKnownGoodState;
        currentState = startState;
        continue;
      } else if (nextState.isFinal()) {
        lastKnownGoodState = i;
        lastKnownGoodToken = currentToken;
        lastKnownIdentifier = nextState.getFinalToken().substring(1);
      }
      currentState = nextState;
    }
    if (currentState.isFinal()) {
      p.println(lastKnownIdentifier + " " + lastKnownGoodToken);
    } else {
      System.err.println("ERROR: " + currentToken);
    }
  }
}
