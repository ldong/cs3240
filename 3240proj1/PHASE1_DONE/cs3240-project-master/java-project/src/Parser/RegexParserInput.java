package Parser;

/**
 * This class models the input into each RegexParser rule method.
 * @author Surenkumar Nihalani
 */

public class RegexParserInput {
  /** The input string to work on */
  private String input;

  /** The next character index to process */
  private int nextCharInInput;

  /**
   * Constructor method.
   * @param regexString
   *        the string to work on.
   * @param nextIndexToWorkOn
   *        the index to start parsing from.
   */
  private RegexParserInput(String regexString, int nextIndexToWorkOn) {
    input = new String(regexString);
    nextCharInInput = nextIndexToWorkOn;
  }

  /**
   * construct a new object starting at zero.
   * @param regexString
   *        Regular expression that we are going to parse.
   */
  public RegexParserInput(String regexString) {
    this(regexString, 0);
  }

  /**
   * Copy constructor.
   * @param regexParserInput
   */
  private RegexParserInput(RegexParserInput regexParserInput) {
    this(regexParserInput.input, regexParserInput.nextCharInInput);
  }

  /**
   * Getter for the current character we are at.
   * @return the character.
   */
  public char getNextCharacter() {
    return input.charAt(nextCharInInput++);
  }

  /**
   * moves back one character
   */
  public void goBackOneCharacter() {
    nextCharInInput--;
    assert (nextCharInInput >= 0);
  }

  /**
   * Makes a deep copy of the object.
   */
  public RegexParserInput clone() {
    return new RegexParserInput(this);
  }

  public String toString() {
    return "RegexParserInput { input = <" + input + "> with pointer at "
        + nextCharInInput;
  }

  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (!(o instanceof RegexParserInput)) {
      return false;
    }
    RegexParserInput other = (RegexParserInput) o;
    return other.input.equals(other.input)
        && other.nextCharInInput == nextCharInInput;
  }

  public int hashCode() {
    return nextCharInInput ^ input.hashCode();
  }

  /**
   * Whether we are end of the string.
   * @return
   */
  public boolean endOfInput() {
    return input.length() == nextCharInInput;
  }
}
