package DFA;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import NFA.NFA;
import NFA.NFAState;

/**
 * This class represents a DFA.
 * @author Surenkumar Nihalani
 */

public class DFA {
  private DFAState startState;

  /**
   * Constructor method.
   * @param s
   *        The start state.
   */
  public DFA(DFAState s) {
    setStartState(s);
  }

  /**
   * Getter for the start state.
   * @return
   */
  public DFAState getStartState() {
    return startState;
  }

  /**
   * Setter for the start state.
   * @param startState
   */
  public void setStartState(DFAState startState) {
    this.startState = startState;
  }

  /**
   * Converts an NFA to a DFA.
   * @param n
   * @return
   */
  public static DFA getDFAFromNFA(NFA n) {
    Queue<Set<NFAState>> bfs = new LinkedList<Set<NFAState>>();
    bfs.add(NFA.getEpsilonClosure(n.getStartState()));
    Map<Set<NFAState>, DFAState> setOfNFAStatesToDFAStateCache = new HashMap<Set<NFAState>, DFAState>();
    Set<Set<NFAState>> visitedSetOfDFAStates = new HashSet<Set<NFAState>>();
    DFA returnableDFA = new DFA(DFAState.NFAStatesToDFA(bfs.element(),
        setOfNFAStatesToDFAStateCache));

    while (!bfs.isEmpty()) {
      Set<NFAState> currentSetOfNFAStates = bfs.poll();
      if (visitedSetOfDFAStates.contains(currentSetOfNFAStates)) {
        continue;
      }
      Set<Character> allTransitionsWeCanTake = new HashSet<Character>();
      DFAState currentDFAState = DFAState.NFAStatesToDFA(currentSetOfNFAStates,
          setOfNFAStatesToDFAStateCache);
      for (NFAState aState : currentSetOfNFAStates) {
        if (aState == null) {
          continue;
        }
        allTransitionsWeCanTake
            .addAll(aState.getTransition().keySet());
      }
      for (Character aTransitionThatWeCanTake : allTransitionsWeCanTake) {
        if (aTransitionThatWeCanTake == null) {
          continue;
        }
        Set<NFAState> nextSetOfStates = new HashSet<NFAState>();
        for (NFAState aState : currentSetOfNFAStates) {
          nextSetOfStates.addAll(NFA.getEpsilonClosure(aState.getTransition()
              .get(aTransitionThatWeCanTake)));
        }
        DFAState nextDFAState = DFAState.NFAStatesToDFA(nextSetOfStates,
            setOfNFAStatesToDFAStateCache);
        currentDFAState.addTransition(aTransitionThatWeCanTake, nextDFAState);
        bfs.add(nextSetOfStates);
      }
      visitedSetOfDFAStates.add(currentSetOfNFAStates);
    }
    return returnableDFA;
  }

  /**
   * converts a given DFA to a Walkable DFA table.
   * @param d
   *        The DFA to convert.
   * @return The DFA table
   */
  public static DFAState[][] getDFATable(DFA d) {
    DFAState[][] dfaTable = new DFAState[DFAState.count][256];
    for (int i = 0; i < dfaTable.length; i++) {
      Arrays.fill(dfaTable[i], null);
    }
    Queue<DFAState> bfs = new LinkedList<DFAState>();
    bfs.add(d.getStartState());
    Set<DFAState> visited = new HashSet<DFAState>();
    while (!bfs.isEmpty()) {
      DFAState current = bfs.poll();
      if (visited.contains(current)) {
        continue;
      }
      Set<Entry<Character, DFAState>> currentTransitions = current
          .transitionsEntrySet();
      for (Entry<Character, DFAState> transition : currentTransitions) {
        dfaTable[current.getIdentifier()][(int) transition.getKey()
            .charValue()] = transition.getValue();
        bfs.add(transition.getValue());

      }
      visited.add(current);
    }
    return dfaTable;
  }

  public static void printTable(DFAState[][] d, PrintStream p) {
    p.println(d.length);
    p.println(d[0].length);
    HashSet<DFAState> finals = new HashSet<DFAState>();
    for (int i = 0; i < d.length; i++) {
      for (int j = 0; j < d[i].length; j++) {
        if (d[i][j] != null) {
          p.println(i + " " + j + " " + d[i][j].getIdentifier());
          if (d[i][j].isFinal()) {
            finals.add(d[i][j]);
          }
        }
      }
    }
    p.println("-1 -1 -1");
    p.println(finals.size());
    for (DFAState ds : finals) {
      if (ds != null) {
        p.println(ds.getIdentifier() + " " + ds.getFinalToken());
      }
    }
  }

  public static DFAState[][] getTableFromScanner(Scanner s) {

    DFAState[][] rv = new DFAState[s.nextInt()][s.nextInt()];
    HashMap<Integer, DFAState> cache = new HashMap<Integer, DFAState>();
    while (s.hasNextInt()) {
      int state = s.nextInt();
      int cha = s.nextInt();
      int to = s.nextInt();
      if (state == -1 && cha == -1 && to == -1) {
        break;
      }
      DFAState toPut;
      if (cache.containsKey(to)) {
        toPut = cache.get(to);
      } else {
        toPut = new DFAState(to);
        cache.put(to, toPut);
      }
      rv[state][cha] = toPut;
    }
    int count = s.nextInt();
    while (count-- > 0) {
      DFAState sl = cache.get(s.nextInt());
      sl.setFinalToken(s.nextLine().trim());
      sl.setFinal(true);
    }
    return rv;
  }
}
