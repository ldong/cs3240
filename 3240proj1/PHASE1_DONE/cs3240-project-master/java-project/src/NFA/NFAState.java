package NFA;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a NFA state.
 * @author Surenkumar Nihalani
 */

public class NFAState {
  /** All the possible states this state does epsilon transitions on */
  private Set<NFAState> epsilonTransitions;

  /** The unique identifier for each NFAState */
  private String uniqueIdentifier;

  /** Transition map */
  private HashMap<Character, NFAState> transition;

  /** whether is state is an accepting one or not */
  private boolean isFinal;

  /** The next state number to be taken */
  private static int nextStateNumber = 0;

  /** The identifier if this is a final state or not */
  private String tokenTypeIfFinalState;

  /** All the NFAStates ever created */
  public static ArrayList<NFAState> allStates = new ArrayList<NFAState>();

  /**
   * Constructor method.
   */
  public NFAState() {
    epsilonTransitions = new HashSet<NFAState>();
    transition = new HashMap<Character, NFAState>();
    uniqueIdentifier = "state-" + nextStateNumber++;
    setTokenTypeIfFinalState("");
  }

  /**
   * Getter for the transition
   * @return
   */
  public HashMap<Character, NFAState> getTransition() {
    return transition;
  }

  /**
   * Whether this state an accepting state or not.
   * @return
   */
  public boolean isFinal() {
    return isFinal;
  }

  /**
   * Setter for final state.
   * @param isFinal
   */
  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  /**
   * Getter for epsilon transition
   * @return
   */
  public Set<NFAState> getEpsilonTransitions() {
    return epsilonTransitions;
  }

  public String toString() {
    StringBuilder s = new StringBuilder();

    s.append("State: q" + uniqueIdentifier + (isFinal ? " (F)" : "") + "\n");

    if (transition.size() > 0) {
      s.append("Transitions:");
      Character[] allTransitions = transition.keySet().toArray(
          (Character[]) Array.newInstance(Character.class, transition.size()));
      Arrays.sort(allTransitions);
      char start = allTransitions[0];
      char end = ' ';
      for (int i = 1; i < allTransitions.length; i++) {
        s.append("\n");
        if (!transition.get(start).equals(transition.get(allTransitions[i]))) {
          s.append("\n" + start + "-" + end + "-----> q"
              + transition.get(start).uniqueIdentifier);
          start = allTransitions[i];
          end = allTransitions[i];
        } else {
          end = allTransitions[i];
        }
      }
      s.append("\n" + start + "-" + end + "-----> q"
          + transition.get(start).uniqueIdentifier);
    }
    if (epsilonTransitions.size() > 0) {
      NFAState[] eps = epsilonTransitions.toArray((NFAState[]) Array
          .newInstance(
              NFAState.class, epsilonTransitions.size()));
      String[] a = (String[]) Array.newInstance(String.class, eps.length);
      for (int i = 0; i < eps.length; i++) {
        a[i] = eps[i].uniqueIdentifier;
      }
      s.append("\nepsilon-----> q" + Arrays.toString(a));
    }
    return s.toString();
  }

  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (!(o instanceof NFAState)) {
      return false;
    }
    NFAState casted = (NFAState) o;
    return casted.uniqueIdentifier.equalsIgnoreCase(casted.uniqueIdentifier);
  }

  /**
   * The identifier to be printed if it is a final state or not.
   * @return
   */
  public String getTokenTypeIfFinalState() {
    return tokenTypeIfFinalState;
  }

  /**
   * Setting the identifier for this NFAState
   * @param tokenTypeIfFinalState
   */
  public void setTokenTypeIfFinalState(String tokenTypeIfFinalState) {
    this.tokenTypeIfFinalState = tokenTypeIfFinalState;
  }

}
