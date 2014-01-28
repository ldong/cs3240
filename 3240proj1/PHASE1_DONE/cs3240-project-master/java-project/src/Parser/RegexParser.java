package Parser;

import static java.lang.Math.max;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import NFA.NFA;

/**
 * This class implements the Regular Expression Parser with all the specified
 * rules we need for this projects CFG.
 * @author Robert Harrison
 * @author Surenkumar Nihalani
 * @author Lin Dong
 */

public class RegexParser {

  private static boolean areWeInsideSquareBrackets = false;
  private static HashMap<String, Set<Character>> definedClasses = new HashMap<String, Set<Character>>();
  private static int maxDefinedClassLength = 0;
  private static final RegexParserOutput epsilonTransitionOutput = new RegexParserOutput(
      true, true);
  private static final RegexParserOutput didntWorkOutput = new RegexParserOutput(
      false, true);

  /**
   * This method takes a regular expression and returns a RegexParserOutput
   * Implementation of the following grammar rule: <reg-ex> -> <rexp>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput reg_ex(String input) {
    return rexp(new RegexParserInput(input));
  }

  /**
   * This method implements the following grammar rule: <rexp> -> <rexp1>
   * <rexp�>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp(RegexParserInput input) {
    RegexParserOutput rexp1Output = rexp1(input.clone());
    if (!rexp1Output.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    input = rexp1Output.getFinalState();
    RegexParserOutput rexpPrimeOutput = rexp_prime(input.clone());
    if (rexpPrimeOutput.wasEpsilonTransition()) {
      return rexp1Output;
    }
    input = rexpPrimeOutput.getFinalState();
    return new RegexParserOutput(true, false, NFA.unionNFA(
        rexp1Output.getNFA(), rexpPrimeOutput.getNFA(), true, true), input);
  }

  /**
   * This method implements the following grammar rule: <rexp�> -> UNION <rexp1>
   * <rexp�> | epsilon
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp_prime(RegexParserInput input) {
    advanceUselessSpace(input);
    if (!match(input, '|')) {
      return new RegexParserOutput(true, true);
    }
    // if rexp1 is another union or goes to epsilon
    RegexParserOutput rexp1_output = rexp1(input.clone());
    if (!rexp1_output.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    if (!rexp1_output.wasEpsilonTransition()) {
      input = rexp1_output.getFinalState();
    }
    RegexParserOutput recursedOutput = rexp_prime(input.clone());
    if (!recursedOutput.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    input = recursedOutput.getFinalState();
    if (recursedOutput.wasEpsilonTransition()
        ^ rexp1_output.wasEpsilonTransition()) {
      if (recursedOutput.wasEpsilonTransition()) {
        return rexp1_output;
      }
      if (rexp1_output.wasEpsilonTransition()) {
        return recursedOutput;
      }
    } else if (recursedOutput.wasEpsilonTransition()
        && rexp1_output.wasEpsilonTransition()) {
      return epsilonTransitionOutput;
    }
    return new RegexParserOutput(true, false, NFA.unionNFA(
        rexp1_output.getNFA(), recursedOutput.getNFA(), true, true), input);
  }

  /**
   * This method checks to see if the next character in the input matches a
   * given terminal or character.
   * @param input
   * @param c
   * @return True if next character of input matches the char passed in.
   */
  public static boolean match(RegexParserInput input, char c) {
    advanceUselessSpace(input);
    if (input.endOfInput()) {
      return false;
    }
    char zc = input.getNextCharacter();
    return zc == c;
  }

  /**
   * This method implements the following grammar rule: <rexp1> -> <rexp2>
   * <rexp1�>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp1(RegexParserInput input) {
    RegexParserOutput rexp2_output = rexp2(input.clone());
    if (!rexp2_output.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    if (rexp2_output.wasEpsilonTransition()) {
      return rexp1_prime(input);
    }
    input = rexp2_output.getFinalState();
    RegexParserOutput rexp1_prime_output = rexp1_prime(input);
    if (!rexp1_prime_output.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    if (rexp1_prime_output.wasEpsilonTransition()) {
      return rexp2_output;
    }
    return new RegexParserOutput(true, false, NFA.concatenateNFA(
        rexp2_output.getNFA(), rexp1_prime_output.getNFA()),
        rexp1_prime_output.getFinalState());
  }

  /**
   * This method implements the following grammar rule: <rexp1�> -> <rexp2>
   * <rexp1�> | epsilon
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp1_prime(RegexParserInput input) {
    RegexParserOutput rexp2_output = rexp2(input.clone());
    if (!rexp2_output.isWorkedOrNot() || rexp2_output.wasEpsilonTransition()) {
      return new RegexParserOutput(true, true);
    }
    // rexp2 worked and it wasnt epsilon
    input = rexp2_output.getFinalState();
    RegexParserOutput rexp1_prime_output = rexp1_prime(input);
    if (!rexp1_prime_output.isWorkedOrNot()) {
      return new RegexParserOutput(true, true);
    }
    if (rexp1_prime_output.wasEpsilonTransition()) {
      return rexp2_output;
    }
    return new RegexParserOutput(true, false, NFA.concatenateNFA(
        rexp2_output.getNFA(), rexp1_prime_output.getNFA()),
        rexp1_prime_output.getFinalState());
  }

  /**
   * This method implements the following grammar rule: <rexp2> -> (<rexp>)
   * <rexp2-tail> | RE_CHAR <rexp2-tail> | <rexp3>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp2(RegexParserInput input) {
    RegexParserInput currentInput = input.clone();
    if (!match(currentInput, '(')) {
      return rexp2_term2(input);
    }
    RegexParserOutput rexpOutput = rexp(currentInput);
    if (!rexpOutput.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    if (rexpOutput.wasEpsilonTransition()) {
      return new RegexParserOutput(true, true, currentInput);
    }
    currentInput = rexpOutput.getFinalState();
    if (!match(currentInput, ')')) {
      return didntWorkOutput;
    }
    RegexParserOutput tailOutput = rexp2_tail(currentInput.clone(),
        rexpOutput.getNFA());
    if (tailOutput.wasEpsilonTransition()) {
      tailOutput.setFinalState(currentInput);
      tailOutput.setWasEpsilonTransition(false);
    }
    return tailOutput;
  }

  /**
   * This method implements the following grammar rule: <rexp2-tail> -> * | + |
   * epsilon
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput
      rexp2_tail(RegexParserInput input, NFA current) {
    advanceUselessSpace(input);
    if (input.endOfInput()) {
      return new RegexParserOutput(true, true, current, input);
    }
    switch (input.getNextCharacter()) {
      case '*':
        return new RegexParserOutput(true, false, NFA.KleeneStar(current),
            input);
      case '+':
        return new RegexParserOutput(true, false, NFA.concatenateNFA(current,
            NFA.KleeneStar(current)), input);
      default:
        input.goBackOneCharacter();
        return new RegexParserOutput(true, true, current, input);
    }
  }

  /**
   * This is a helper method for rexp2_tail.
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp2_term2(RegexParserInput input) {
    RegexParserInput currentInput = input.clone();
    RegexParserOutput getRE_CHAR = RE_CHAR(currentInput);
    if (!getRE_CHAR.isWorkedOrNot()) {
      return rexp3(input);
    }
    NFA newNFA = NFA.getNFAFromCharacter(getRE_CHAR.getCharacterParsed());
    RegexParserOutput rexpTailOutput = rexp2_tail(currentInput, newNFA);
    if (rexpTailOutput.wasEpsilonTransition()) {
      rexpTailOutput.setFinalState(currentInput);
      rexpTailOutput.setWasEpsilonTransition(false);
    }
    return rexpTailOutput;
  }

  /**
   * This method implements the following grammar rule: <rexp3> -> <char-class>
   * | epsilon
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput rexp3(RegexParserInput input) {
    advanceUselessSpace(input);
    RegexParserOutput characterClassForNFA = char_class(input.clone());
    if (!characterClassForNFA.isWorkedOrNot()
        || characterClassForNFA.wasEpsilonTransition()) {
      // We took epsilon transition;
      return new RegexParserOutput(true, true, input);
    }
    input = characterClassForNFA.getFinalState();
    NFA newNFA = new NFA();
    Iterator<Character> allTransitions = characterClassForNFA.getCharSet()
        .iterator();
    while (allTransitions.hasNext()) {
      newNFA.getStartState().getTransition()
          .put(allTransitions.next(), newNFA.getEndState());
    }
    return new RegexParserOutput(true, false, newNFA,
        characterClassForNFA.getFinalState());
  }

  /**
   * This method implements the following grammar rule: <char-class> -> . | [
   * <char-class1> | <defined-class>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput char_class(RegexParserInput input) {
    advanceUselessSpace(input);
    RegexParserInput dotOrCharClassOneInput = input.clone();
    if (dotOrCharClassOneInput.endOfInput()) {
      return didntWorkOutput;
    }
    RegexParserOutput returnValue = new RegexParserOutput();
    switch (dotOrCharClassOneInput.getNextCharacter()) {
      case '.':
        Set<Character> allCharacters = new HashSet<Character>();
        for (int i = 32; i <= 126; i++) {
          allCharacters.add((char) i);
        }
        returnValue.setCharSet(allCharacters);
        returnValue.setWorkedOrNot(true);
        returnValue.setFinalState(dotOrCharClassOneInput);
        return returnValue;
      case '[':
        assert (!areWeInsideSquareBrackets);
        areWeInsideSquareBrackets = true;
        return char_class1(dotOrCharClassOneInput);
      default:
        return defined_class(input);
    }
  }

  /**
   * This method will clear the currently defined classes.
   */
  public static void clearDefinedClass() {
    definedClasses.clear();
  }

  /**
   * This method takes a defined class such as $DIGIT and adds the appropriate
   * set of characters to this defined class.
   * @param key
   * @param set
   */
  public static void defineClass(String key, Set<Character> set) {
    definedClasses.put(key, set);
    maxDefinedClassLength = max(maxDefinedClassLength, key.length());
  }

  /**
   * This method implements the following grammar rule: <exclude-set-tail> ->
   * [<char-set>] | <defined-class>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput exclude_set_tail(RegexParserInput input) {
    advanceUselessSpace(input);
    // set up an empty return output
    RegexParserOutput returnValue = new RegexParserOutput();
    char next = input.getNextCharacter();

    // see if its [
    if (next != '[') {
      input.goBackOneCharacter();
      return defined_class(input);
    }

    RegexParserOutput startingCharacterOutput = char_set(input.clone());

    if (!startingCharacterOutput.isWorkedOrNot()) {
      returnValue.setWorkedOrNot(false);
      return returnValue;
    }
    // get rid of the space before take the right bracket ]
    advanceUselessSpace(input);
    next = startingCharacterOutput.getFinalState().getNextCharacter();

    if (next != ']') {
      returnValue.setWorkedOrNot(false);
      return returnValue;
    } // failed at the last step, dont overwrite anything

    //advance the input pointer
    returnValue.setWorkedOrNot(true);
    returnValue.setFinalState(input);
    returnValue.setCharSet(startingCharacterOutput.getCharSet());

    return returnValue;

  }

  @SuppressWarnings("unchecked")
  public static RegexParserOutput defined_class(RegexParserInput input) {
    advanceUselessSpace(input);
    String definedClass = "";
    boolean worked = false;
    for (int i = 0; i < maxDefinedClassLength; i++) {
      if (input.endOfInput()) {
        if (i == 0) {
          return epsilonTransitionOutput;
        } else {
          return didntWorkOutput;
        }
      }
      definedClass += input.getNextCharacter();
      if (definedClasses.containsKey(definedClass)) {
        worked = true;
        break;
      }
    }
    RegexParserOutput returnValue = new RegexParserOutput();
    returnValue.setWorkedOrNot(worked);

    if (worked) {
      returnValue.setFinalState(input);
      returnValue
          .setCharSet((Set<Character>) ((HashSet<Character>) definedClasses
              .get(definedClass)).clone());
    }

    return returnValue;
  }

  /**
   * @param input
   * @return
   */
  public static RegexParserOutput CLS_CHAR(RegexParserInput input) {
    advanceUselessSpace(input);
    char currentCharacter = input.getNextCharacter();
    boolean isThisCharacterValidOrNot = false;
    char theCharacterToConsider = ' ';

    switch (currentCharacter) {
      case '\\':
        char nextCharacter = input.getNextCharacter();
        switch (nextCharacter) {
          case '\\':
          case '^':
          case '-':
          case '[':
          case ']':
            theCharacterToConsider = nextCharacter;
            isThisCharacterValidOrNot = true;
            break;
          default:
            isThisCharacterValidOrNot = false;
            input.goBackOneCharacter();
            break;
        }
        break;
      case '^':
      case '-':
      case '[':
      case ']':
        isThisCharacterValidOrNot = false;
        break;
      default:
        int currentCharactersAsciiValue = (int) currentCharacter;
        if (currentCharactersAsciiValue >= 32
            && currentCharactersAsciiValue <= 126) {
          theCharacterToConsider = currentCharacter;
          isThisCharacterValidOrNot = true;
        }
        break;
    }
    return new RegexParserOutput(isThisCharacterValidOrNot, false,
        theCharacterToConsider, input);
  }

  public static RegexParserOutput RE_CHAR(RegexParserInput rpi) {
    RegexParserOutput rpout = new RegexParserOutput();
    advanceUselessSpace(rpi);
    if (rpi.endOfInput()) {
      return didntWorkOutput;
    }
    char next = rpi.getNextCharacter();
    switch (next) {
      case '\\':
        next = rpi.getNextCharacter();
        switch (next) {
          case '\\':
          case '*':
          case '+':
          case '?':
          case '|':
          case '[':
          case '(':
          case ')':
          case '.':
          case '\'':
          case '"':
          case '$':
          case ' ':
            rpout.setWorkedOrNot(true);
            break;
          default:
            return didntWorkOutput;
        }
        break;
      case '$':
        // It's defined class. 
        return didntWorkOutput;
      default:
        if (next == '\\' || next == '*' || next == '+' || next == '?'
            || next == '|'
            || next == '[' || next == ']' || next == '(' || next == ')'
            || next == '.' || next == '\'' || next == '"' || next == ' ') {
          rpout.setWorkedOrNot(false);
        }
        else {
          int currentCharactersAsciiValue = (int) next;
          if (currentCharactersAsciiValue >= 32
              && currentCharactersAsciiValue <= 126) {
            rpout.setWorkedOrNot(true);
          }
          else {
            rpout.setWorkedOrNot(false);
          }
        }
        break;
    }
    if (rpout.isWorkedOrNot()) {
      rpout.setCharacterParsed(next);
      rpout.setFinalState(rpi);
    }
    return rpout;
  }

  /**
   * <char-class1> -> <char-set-list> | <exclude-set>
   * @param input
   *        The input string to work on.
   * @return output holder object containing the work done.
   */
  public static RegexParserOutput char_class1(RegexParserInput input) {
    RegexParserOutput charSetListOutput = char_set_list(input.clone());
    if (!charSetListOutput.isWorkedOrNot()) {
      return exclude_set(input);
    }
    return charSetListOutput;
  }

  /**
   * This method implements the following grammar rule: <char-set-list> ->
   * <char-set> <char-set-list> | ]
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput char_set_list(RegexParserInput input) {
    RegexParserOutput returnValue = new RegexParserOutput();
    Set<Character> allCharacters = new HashSet<Character>();
    RegexParserOutput singleCharacterSetOutput = char_set(input.clone());
    if (!singleCharacterSetOutput.isWorkedOrNot()) {
      // Time to match ]
      advanceUselessSpace(input);
      char possibleClosingSquareBracket = input.getNextCharacter();
      if (possibleClosingSquareBracket == ']') {
        assert (areWeInsideSquareBrackets);
        areWeInsideSquareBrackets = false;
        returnValue.setWorkedOrNot(true);
        returnValue.setFinalState(input);
        returnValue.setCharSet(allCharacters);
        return returnValue;
      } else {
        returnValue.setWorkedOrNot(false);
        return returnValue;
      }
    }
    input = singleCharacterSetOutput.getFinalState();
    allCharacters.addAll(singleCharacterSetOutput.getCharSet());
    RegexParserOutput recursiveRestofTheSet = char_set_list(input.clone());
    if (!recursiveRestofTheSet.isWorkedOrNot()) {
      returnValue.setWorkedOrNot(false);
      return returnValue;
    }
    input = recursiveRestofTheSet.getFinalState();
    allCharacters.addAll(recursiveRestofTheSet.getCharSet());
    returnValue.setWorkedOrNot(true);
    returnValue.setCharSet(allCharacters);
    returnValue.setFinalState(input);
    return returnValue;
  }

  /**
   * This method implements the following grammar rule: <char-set> -> CLS_CHAR
   * <char-set-tail>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput char_set(RegexParserInput input) {
    RegexParserOutput returnValue = new RegexParserOutput();
    Set<Character> characters = new HashSet<Character>();
    RegexParserOutput startingCharacterOutput = CLS_CHAR(input.clone());
    if (!startingCharacterOutput.isWorkedOrNot()) {
      returnValue.setWorkedOrNot(false);
      return returnValue;
    }
    input = startingCharacterOutput.getFinalState();
    Character startCharacter = startingCharacterOutput.getCharacterParsed();
    // Let's check if it's a range or not. 
    RegexParserOutput endingCharacterOutput = char_set_tail(input.clone());
    if (endingCharacterOutput.wasEpsilonTransition()) {
      // there is only one character to parse
      characters.add(startCharacter);
      returnValue.setCharSet(characters);
      returnValue.setWorkedOrNot(true);
      returnValue.setFinalState(input);
      return returnValue;
    }
    input = endingCharacterOutput.getFinalState();
    Character endingCharacter = endingCharacterOutput.getCharacterParsed();
    if (startCharacter.compareTo(endingCharacter) > 0) {
      Character temp = startCharacter;
      startCharacter = endingCharacter;
      endingCharacter = temp;
    }
    for (int start = (int) startCharacter.charValue(); start <= (char) endingCharacter
        .charValue(); start++) {
      characters.add((char) start);
    }
    return new RegexParserOutput(true, false, characters, input);
  }

  /**
   * This method implements the following grammar rule: <exclude-set> -> ^
   * <char-set>] IN <exclude-set-tail>
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput exclude_set(RegexParserInput input) {
    RegexParserOutput returnValue = new RegexParserOutput();
    advanceUselessSpace(input);
    if (input.getNextCharacter() != '^') {
      return didntWorkOutput;
    }
    advanceUselessSpace(input);
    RegexParserOutput charSetOutput = char_set(input.clone());
    Set<Character> charactersToRemove = charSetOutput.getCharSet();
    if (!charSetOutput.isWorkedOrNot()) {
      returnValue.setWorkedOrNot(false);
      return returnValue;
    }
    input = charSetOutput.getFinalState();
    advanceUselessSpace(input);
    if (input.getNextCharacter() != ']') {
      return didntWorkOutput;
    }
    areWeInsideSquareBrackets = false;
    advanceUselessSpace(input);
    if (input.getNextCharacter() != 'I') {
      return didntWorkOutput;
    }
    boolean copyOfAreWeInsideBrackets = areWeInsideSquareBrackets;
    areWeInsideSquareBrackets = true;
    advanceUselessSpace(input);
    areWeInsideSquareBrackets = copyOfAreWeInsideBrackets;
    if (input.getNextCharacter() != 'N') {
      return didntWorkOutput;
    }
    RegexParserOutput excludeSetOutput = exclude_set_tail(input);
    if (!excludeSetOutput.isWorkedOrNot()) {
      return didntWorkOutput;
    }
    Set<Character> finalSet = excludeSetOutput.getCharSet();

    finalSet.removeAll(charactersToRemove);

    returnValue.setCharSet(finalSet);
    returnValue.setFinalState(input);
    returnValue.setWorkedOrNot(true);
    returnValue.setWasEpsilonTransition(false);
    return returnValue;

  }

  /**
   * This method implements the following grammar rule: <char-set-tail> -> �
   * CLS_CHAR | epsilon
   * @param input
   * @return RegexParserOutput
   */
  public static RegexParserOutput char_set_tail(RegexParserInput input) {
    RegexParserOutput returnValue = new RegexParserOutput();
    advanceUselessSpace(input);
    if (input.getNextCharacter() != '-') {
      return epsilonTransitionOutput;
    }
    advanceUselessSpace(input);
    RegexParserOutput endRangeOutput = CLS_CHAR(input);
    if (endRangeOutput.isWorkedOrNot()) {
      returnValue.setCharacterParsed(endRangeOutput.getCharacterParsed());
      returnValue.setFinalState(endRangeOutput.getFinalState());
      returnValue.setWorkedOrNot(true);
      returnValue.setWasEpsilonTransition(false);
      return returnValue;
    }
    return didntWorkOutput;
  }

  /**
   * Given a RegexParserInput with pointer to next char to be parsed. Advance
   * the next char pointer passed all the useless whitespace.
   * @param input
   */
  public static void advanceUselessSpace(RegexParserInput input) {
    while (!input.endOfInput()) {
      char current = input.getNextCharacter();
      if (Character.isWhitespace(current)) {
        if (current == ' ' && areWeInsideSquareBrackets) {
          input.goBackOneCharacter();
          break;
        }
      } else {
        input.goBackOneCharacter();
        break;
      }
    }
  }
}
