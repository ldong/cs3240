package NFA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import Parser.RegexParser;
import Parser.RegexParserInput;
import Parser.RegexParserOutput;

/**
 * This class represents an NFA. This class also provides methods for the
 * concatenate, union, and kleene star operations for NFAs.
 */

public class NFA {
  /** The start and the end state */
  private NFAState startState, endState;

  /**
   * Constructor method.
   */
  public NFA() {
    startState = new NFAState();
    endState = new NFAState();
    endState.setFinal(true);
    NFAState.allStates.add(startState);
    NFAState.allStates.add(endState);
  }

  public String toString() {
    return "NFA (\nstart_state=" + startState.toString() + ", \nend_state="
        + endState.toString()
        + ");";
  }

  /**
   * Getter for the start state of this NFA.
   * @return
   */
  public NFAState getStartState() {
    return startState;
  }

  /**
   * Setter for the start state of the NFA
   * @param startState
   */
  public void setStartState(NFAState startState) {
    this.startState = startState;
  }

  /**
   * Getter of the ending state
   * @return
   */
  public NFAState getEndState() {
    return endState;
  }

  /**
   * Setter for the ending state.
   * @param endState
   */
  public void setEndState(NFAState endState) {
    this.endState = endState;
  }

  /**
   * Takes a file and generates and NFA out of it like a boss.
   * @param scanner
   * @return
   */
  public static NFA getNFAFromSpecFile(Scanner scanner) {
    while (scanner.hasNextLine()) {
      Scanner lineScanner = new Scanner(scanner.nextLine());
      if (!lineScanner.hasNext()) {
        break;
      }
      String type = lineScanner.next();
      String regex = lineScanner.nextLine();
      RegexParserOutput lol = RegexParser
          .char_class(new RegexParserInput(regex));
      RegexParser.defineClass(type, lol.getCharSet());
//      System.out.println(lol);
    }

    List<NFA> allNFAsSoFar = new ArrayList<NFA>();
    while (scanner.hasNextLine()) {
      Scanner regexLineScanner = new Scanner(scanner.nextLine());
      if (!regexLineScanner.hasNext()) {
        break;
      }

      String identifier = regexLineScanner.next();
      String regex = regexLineScanner.nextLine();
      RegexParserOutput lol = RegexParser.reg_ex(regex);
      if (!lol.isWorkedOrNot() || lol.wasEpsilonTransition()) {
        System.err.println("Cant work with regex :" + regex);
        System.exit(1);
      }
      NFA currentNFA = lol.getNFA();
      currentNFA.getEndState().setTokenTypeIfFinalState(identifier);
//      System.out.println(identifier);
//      System.out.println(lol.getNFA());
      allNFAsSoFar.add(currentNFA);
    }
    NFA all = null;
    if (allNFAsSoFar.size() > 0) {
      all = allNFAsSoFar.get(0);
      for (int i = 1; i < allNFAsSoFar.size(); i++) {
        all = NFA.unionNFA(all, allNFAsSoFar.get(i), false, false);
      }
    }
    return all;
  }

  /**
   * Performs regular operation of concatenating two NFAs
   * @param nfa
   * @param nfa2
   * @return the concatenated NFA
   */
  public static NFA concatenateNFA(NFA nfa, NFA nfa2) {
    nfa.getStartState().setFinal(false);
    nfa.getEndState().setFinal(false);
    nfa2.getStartState().setFinal(false);
    nfa2.getEndState().setFinal(true);

    nfa.getEndState().getEpsilonTransitions().add(nfa2.getStartState());
    nfa.setEndState(nfa2.getEndState());
    return nfa;
  }

  /**
   * unions two NFAs
   * @param nfa
   * @param nfa2
   * @param toClearFlags
   *        whether to clear the flag of the older end states
   * @param toJoinToEnd
   *        whether to add epsilon transitions for the end states to the new end
   *        state.
   * @return
   */
  public static NFA
      unionNFA(NFA nfa, NFA nfa2, boolean toClearFlags, boolean toJoinToEnd) {
    NFA newNFA = new NFA();

    NFAState start = newNFA.getStartState(), end = newNFA.getEndState();

    start.getEpsilonTransitions().add(nfa.getStartState());
    start.getEpsilonTransitions().add(nfa2.getStartState());

    if (toJoinToEnd) {
      nfa.getEndState().getEpsilonTransitions().add(end);
      nfa2.getEndState().getEpsilonTransitions().add(end);
    }
    if (toClearFlags) {
      nfa.getEndState().setFinal(false);
      nfa2.getEndState().setFinal(false);
    }
    return newNFA;
  }

  /**
   * Applies the kleeny star operation on a given NFA
   * @param current
   * @return
   */
  public static NFA KleeneStar(NFA current) {
    NFA updatedNFA = new NFA();

    NFAState start = updatedNFA.getStartState();
    NFAState end = updatedNFA.getEndState();

    start.getEpsilonTransitions().add(end);
    start.getEpsilonTransitions().add(current.getStartState());

    current.getEndState().getEpsilonTransitions().add(end);
    current.getEndState().getEpsilonTransitions().add(start);

    current.getEndState().setFinal(false);

    return updatedNFA;
  }

  /**
   * Creates a NFA for a given character
   * @param characterParsed
   * @return
   */
  public static NFA getNFAFromCharacter(char characterParsed) {
    NFA theNFA = new NFA();
    theNFA.getStartState().getTransition()
        .put(characterParsed, theNFA.getEndState());
    return theNFA;
  }

  /**
   * Gets the epsilon closure set for a given NFAState
   * @param d
   * @return all the set of NFAStates this NFAState could be in.
   */
  public static Set<NFAState> getEpsilonClosure(NFAState d) {
    Set<NFAState> rv = new HashSet<NFAState>();
    if (d == null) {
      return rv;
    }
    rv.add(d);
    boolean toContinue = true;
    while (true) {
      if (!toContinue) {
        break;
      }
      HashSet<NFAState> temp = new HashSet<NFAState>();

      for (NFAState n : rv) {
        if (n == null) {
          continue;
        }
        temp.addAll(n.getEpsilonTransitions());
      }
      toContinue = rv.addAll(temp);
    }
    return rv;
  }

}
