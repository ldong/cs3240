package Parser;

import java.util.HashSet;
import java.util.Set;

import NFA.NFA;

/**
 * This class models the output from each RegexParser rule method.
 * @author Surenkumar Nihalani
 */

public class RegexParserOutput {
  /** The NFA we have accumulated so far. */
  private NFA output;

  /** Whether it worked or not */
  private boolean workedOrNot;

  /** The final amount of input consumed */
  private RegexParserInput finalState;

  /** The character parsed */
  private char characterParsed;

  /** whether an epsilon transition was taken or not */
  private boolean wasEpsilonTransition;

  /** The char set parsed so far */
  private Set<Character> charSet;

  /**
   * Constructor method
   * @param worked
   *        whether it worked or not
   * @param wasEpsilon
   *        was this an epsilon transition
   * @param input
   *        the final state of input
   */
  public RegexParserOutput(boolean worked, boolean wasEpsilon,
      RegexParserInput input) {
    workedOrNot = worked;
    wasEpsilonTransition = wasEpsilon;
    finalState = input;
  }

  /**
   * Default constructor method
   */
  public RegexParserOutput() {
    output = new NFA();
    workedOrNot = false;
    finalState = new RegexParserInput("DEFAULT");
    characterParsed = ' ';
    wasEpsilonTransition = false;
    charSet = new HashSet<Character>();
  }

  /**
   * Constructor methos
   * @param worked
   *        whether it worked or not
   * @param epsilon
   *        whether it was an epsilon transition
   * @param theNFAtoBePassed
   *        the NFA to be upforwarded
   * @param updatedInput
   *        the final state of input state.
   */
  public RegexParserOutput(boolean worked, boolean epsilon,
      NFA theNFAtoBePassed,
      RegexParserInput updatedInput) {
    this(worked, epsilon, updatedInput);
    output = theNFAtoBePassed;
  }

  /**
   * Constructor to communicate failures.
   * @param worked
   * @param epsilon
   */
  public RegexParserOutput(boolean worked, boolean epsilon) {
    this(worked, epsilon, null);
  }

  /**
   * Constructor to communicate the set of characters
   * @param worked
   * @param epsilon
   * @param characters
   * @param input
   */
  public RegexParserOutput(boolean worked, boolean epsilon,
      Set<Character> characters,
      RegexParserInput input) {
    this(worked, epsilon, input);
    charSet = characters;
  }

  /**
   * To communicate whether the matching of the character worked or not.
   * @param isThisCharacterValidOrNot
   * @param epsilon
   * @param theCharacterToConsider
   * @param input
   */
  public RegexParserOutput(boolean isThisCharacterValidOrNot, boolean epsilon,
      char theCharacterToConsider, RegexParserInput input) {
    this(isThisCharacterValidOrNot, epsilon, input);
    characterParsed = theCharacterToConsider;
  }

  public NFA getNFA() {
    return output;
  }

  public void setNFA(NFA output) {
    this.output = output;
  }

  public boolean isWorkedOrNot() {
    return workedOrNot;
  }

  public void setWorkedOrNot(boolean workedOrNot) {
    this.workedOrNot = workedOrNot;
  }

  public RegexParserInput getFinalState() {
    return finalState;
  }

  public void setFinalState(RegexParserInput finalState) {
    this.finalState = finalState;
  }

  public char getCharacterParsed() {
    return characterParsed;
  }

  public void setCharacterParsed(char characterParsed) {
    this.characterParsed = characterParsed;
  }

  public boolean wasEpsilonTransition() {
    return wasEpsilonTransition;
  }

  public void setWasEpsilonTransition(boolean wasEpsilonTransition) {
    this.wasEpsilonTransition = wasEpsilonTransition;
  }

  public Set<Character> getCharSet() {
    return charSet;
  }

  public void setCharSet(Set<Character> charSet) {
    this.charSet = charSet;
  }

  public String toString() {
    return "outputNFA: " + output.toString() + "\nworkedOrNot: " + workedOrNot
        + "\nfinalState: " + finalState.toString() + "\nCharacter parsed: "
        + characterParsed + "\n wasEpsilonTransition: " + wasEpsilonTransition
        + "\nCharset: " + charSet.toString();
  }
}
