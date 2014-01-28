package DFA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import NFA.NFAState;

/**
 * This class represents a DFA state.
 * @author Surenkumar Nihalani
 */

public class DFAState {
  /** The unique identifier for each state */
  private int identifier;
  /** The set of NFA states represented by this DFA state */
  private Set<NFAState> theNFAStatesRepresented;
  /**
   * The map to find out the next state after taking transition of a given
   * character
   */
  private Map<Character, DFAState> transitions;
  /**
   * Static count to keep track of how many states have been created so far
   */
  public static int count = 0;
  /** whether this is an accept state or not */
  private boolean isFinal;
  /** The identifier associated with this state if its a final state */
  private String finalToken;
  /**
   * The list that contains all the DFAStates created so far. Easier to debug in
   * eclipse interface
   */
  public static List<DFAState> dbug = new ArrayList<DFAState>();

  /**
   * constructor method. private. Refer to factory method.
   * @param allStates
   *        The NFAStates represented by this DFA
   */

  public DFAState(int id) {
    identifier = id;
  }

  public void setFinalToken(String finalToken) {
    this.finalToken = finalToken;
  }

  private DFAState(Set<NFAState> allStates) {

    identifier = count++;
    theNFAStatesRepresented = allStates;
    transitions = new HashMap<Character, DFAState>();
    isFinal = false;
    finalToken = "";
    for (NFAState n : allStates) {
      if (n == null) {
        continue;
      }
      if (n.isFinal()) {
        isFinal = true;
        if (n.getTokenTypeIfFinalState().equals("null")) {
          System.err.println(n);
        }
        finalToken += n.getTokenTypeIfFinalState() + " ";
      }
    }
  }

  /**
   * Adding a transition to this State
   * @param c
   *        the character to change state on
   * @param end
   *        the state to go to on this transition
   */
  public void addTransition(char c, DFAState end) {
    transitions.put(c, end);
  }

  /**
   * The next state on taking transition of c
   * @param c
   * @return null if no such transition exists
   */
  public DFAState getTransition(char c) {
    return transitions.get(c);
  }

  public String toString() {
    return "State-" + identifier + (isFinal ? (" (F): " + finalToken) : "");
  }

  /**
   * Checks whether ths given DFAState backs up the same set of NFAStates or
   * not.
   * @param d
   * @return
   */
  public boolean equals(DFAState d) {
    return theNFAStatesRepresented.equals(d.theNFAStatesRepresented);
  }

  /**
   * Converts a given set of states to a DFA. Factory method.
   * @param states
   *        The NFAStates
   * @param cache
   *        caching DFAState creation
   * @return The DFAState created
   */
  public static DFAState
      NFAStatesToDFA(Set<NFAState> states, Map<Set<NFAState>, DFAState> cache) {
    if (cache.containsKey(states)) {
      return cache.get(states);
    }
    DFAState returnValue = new DFAState(states);
    dbug.add(returnValue);
    cache.put(states, returnValue);
    return returnValue;
  }

  /**
   * Returns the unique identifier for this DFAState
   * @return
   */
  public int getIdentifier() {
    return identifier;
  }

  /**
   * Extracting all the transitions for table creation.
   * @return
   */
  public Set<Entry<Character, DFAState>> transitionsEntrySet() {
    return transitions.entrySet();
  }

  /**
   * Tells whether this is an accepting state or not.
   * @return
   */
  public boolean isFinal() {
    return isFinal;
  }

  /**
   * The identifier for this stage should it be an accepting one.
   * @return
   */
  public String getFinalToken() {
    return finalToken;
  }

  public void setFinal(boolean t) {
    isFinal = t;
  }
}
