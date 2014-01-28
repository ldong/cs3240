package Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

/**
 * Our JUnit test suite is contained here.
 * @author Robert Harrison
 * @author Lin Dong
 */

public class RegexParserTest {

  /**
   * Tests the char set list method.
   */
  @Test
  public void Test_CharSetList() {
    String testStr = "A-Za-z]";
    RegexParserInput input = new RegexParserInput(testStr);
    RegexParserOutput output = RegexParser.char_set_list(input);
    assertTrue(output.isWorkedOrNot());
    HashSet<Character> my_chars = new HashSet<Character>();
    for (Character c : output.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());
    assertTrue(my_chars.equals(output.getCharSet()));
    testStr = "a-z8-2]";
    input = new RegexParserInput(testStr);
    output = RegexParser.char_set_list(input);
    assertTrue(output.isWorkedOrNot());
    testStr = "1-6a-z3-4B-Da-e]";
    input = new RegexParserInput(testStr);
    output = RegexParser.char_set_list(input);
    assertTrue(output.isWorkedOrNot());
    my_chars = new HashSet<Character>();
    for (Character c : output.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());
    testStr = "6-1]";
    input = new RegexParserInput(testStr);
    output = RegexParser.char_set_list(input);
    assertTrue(output.isWorkedOrNot());
    testStr = "a-Z]";
    input = new RegexParserInput(testStr);
    output = RegexParser.char_set_list(input);
    assertTrue(output.isWorkedOrNot());
  }

  /**
   * Tests the exclude_set
   */
  @Test
  public void Test_ExcludeSetTail() {
    String testStr = "[hello]";
    RegexParserInput testIn = new RegexParserInput(testStr);
    RegexParserOutput testOut = RegexParser.exclude_set_tail(testIn);
    assertFalse(testOut.isWorkedOrNot());
    testStr = "[A-Z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertTrue(testOut.isWorkedOrNot());
    testStr = "[a-Z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertTrue(testOut.isWorkedOrNot());
    testStr = "[[]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertFalse(testOut.isWorkedOrNot());
    testStr = "[[]]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertFalse(testOut.isWorkedOrNot());
    testStr = "[X]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertTrue(testOut.isWorkedOrNot());
    testStr = "[f-a]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertTrue(testOut.isWorkedOrNot());
    testStr = "[l-z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set_tail(testIn);
    assertTrue(testOut.isWorkedOrNot());
  }

  /**
   * Tests the exclude set.
   */
  @Test
  public void Test_ExcludeSet() {
    String testStr = "^a] IN [a-z]";
    RegexParserInput testIn = new RegexParserInput(testStr);
    RegexParserOutput testOut = RegexParser.exclude_set(testIn);
    assertTrue(testOut.isWorkedOrNot());
    HashSet<Character> my_chars = new HashSet<Character>();
    for (Character c : testOut.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());
    testStr = "^a-c] IN [a-z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.exclude_set(testIn);
    assertTrue(testOut.isWorkedOrNot());
  }

  /**
   * tests character class
   */
  @Test
  public void Test_CharClass() {
    String testStr = ".";
    RegexParserInput testIn = new RegexParserInput(testStr);
    RegexParserOutput testOut = RegexParser.char_class(testIn);
    assertTrue(testOut.isWorkedOrNot());
    HashSet<Character> my_chars = new HashSet<Character>();
    for (Character c : testOut.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());

    testStr = "[^a] IN [a-z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.char_class(testIn);
    assertTrue(testOut.isWorkedOrNot());
    my_chars = new HashSet<Character>();
    for (Character c : testOut.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());

    testStr = "[^1-3] IN [1-9]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.char_class(testIn);
    assertTrue(testOut.isWorkedOrNot());
    my_chars = new HashSet<Character>();
    for (Character c : testOut.getCharSet()) {
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: " + my_chars.size());
  }

  /**
   * Tests char class1
   */
  @Test
  public void Test_CharClass1()
  {
    String[] str = { "A-Za-z]",
        "a-zA-Z]",
        "A-z]",
        "a-Z]",

        "a-c]",
        "!-*]",
        "1-34-9]",
        "1-9]",

        "9-1]",
        "^]",

        "^0] IN [0-9]",
        "^1-3] in [1-9]",
        "^a-b] in [a-b]",
        "^a-b] IN [a-b]",

        "^b] IN [a-c]"
    };

    ArrayList<RegexParserInput> list = new ArrayList<RegexParserInput>();

    boolean[] expected = { true, true, true, true,
        true, true, true, true,
        true, false,
        true, false, false, true,
        true };

    for (int i = 0; i < str.length; i++) {
      // add RPI to list as ordered
      RegexParserInput temp = new RegexParserInput(str[i]);
      list.add(temp);
    }

    for (int i = 0; i < str.length; i++) {
//      System.out.println(list.get(i));
      RegexParserOutput r = RegexParser.char_class1(list.get(i));

      if (expected[i] != r.isWorkedOrNot())
      {
        System.out.println("#" + i + " expected: " + expected[i]
            + " vs actual " + r.isWorkedOrNot());
        System.out.println("TestCase# " + i + ": " + str[i] + " Failed");
//        System.out.println(r.getCharSet());
      }

      assertEquals(expected[i], r.isWorkedOrNot());
      // if true, print out the charSet

    }

  }

  /**
   * Tests reg_ex
   */
  @Test
  public void Test_Reg_Ex() {
    String testStr = "a";
    ;
    RegexParserOutput testOut = RegexParser.reg_ex(testStr);
    System.out.println("regex: " + testStr);
    System.out.println(testOut.getNFA());
    assertTrue(testOut.isWorkedOrNot());
    testStr = "a*";
    testOut = RegexParser.reg_ex(testStr);
    System.out.println("regex: " + testStr);
    System.out.println(testOut.getNFA());
    assertTrue(testOut.isWorkedOrNot());
  }

  /**
   * Test rexp3
   */
  @Test
  public void Test_Rexp3() {
    String testStr = "";
    RegexParserInput testIn = new RegexParserInput(testStr);
    RegexParserOutput testOut = RegexParser.rexp3(testIn);
    assertTrue(testOut.isWorkedOrNot());
    HashSet<Character> my_chars = new HashSet<Character>();
    if (testOut.getCharSet() != null) {
      for (Character c : testOut.getCharSet()) {
        my_chars.add(c);
      }
      System.out.println(my_chars.toString());
      System.out.println("# of chars: " + my_chars.size());
    }
    Test_CharClass();

    /*
    testStr = "";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.char_class(testIn);
    assertTrue(testOut.isWorkedOrNot());
    my_chars = new HashSet<Character>();
    for(Character c : testOut.getCharSet()){
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: "+ my_chars.size());
    
    testStr = "[^a] IN [a-z]";
    testIn = new RegexParserInput(testStr);
    testOut = RegexParser.char_class(testIn);
    assertTrue(testOut.isWorkedOrNot());
    my_chars = new HashSet<Character>();
    for(Character c : testOut.getCharSet()){
      my_chars.add(c);
    }
    System.out.println(my_chars.toString());
    System.out.println("# of chars: "+ my_chars.size());
    */
  }

  /**
   * Test char_set
   */
  @Test
  public void Test_CharSet() {
    RegexParserInput setTest = new RegexParserInput(
        "A-Z a-z a-Z A-z 1-9 9-1 a-9 9-a");
    boolean[] expected = { true, true, true, true,
        true, true, true, true };
    for (int i = 0; i < expected.length; i++) {
      RegexParserOutput r = RegexParser.char_set(setTest);
      if (expected[i] != r.isWorkedOrNot())
        System.out.println(r.getCharacterParsed() + " " + i);
      assertEquals(expected[i], r.isWorkedOrNot());
    }
  }

  /**
   * test cls_char
   */
  @Test
  public void TestCLS_CHAR() {
    RegexParserInput test = new RegexParserInput(
        "!2#qAZ\\a^-][\\\\\\^\\-\\[\\]");
    boolean[] expected = { true, true, true, true, true, true, false, true,
        false, false, false, false, true, true, true, true, true };
    for (int i = 0; i < expected.length; i++) {
      RegexParserOutput r = RegexParser.CLS_CHAR(test);
      assertEquals(expected[i], r.isWorkedOrNot());
    }
  }

  /**
   * Test re_Char
   */
  @Test
  public void TestRE_CHAR() {
    RegexParserOutput rpout = new RegexParserOutput();
    RegexParserInput testInput = new RegexParserInput("8");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("A");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("b");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("a");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("z");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("Z");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("\\\\");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("\\?");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("\\*");
    rpout = RegexParser.RE_CHAR(testInput);
    assertTrue(rpout.isWorkedOrNot());
    testInput = new RegexParserInput("?");
    rpout = RegexParser.RE_CHAR(testInput);
    assertFalse(rpout.isWorkedOrNot());
  }

  /**
   * tests whitespace advance.
   */
  @Test
  public void TestWhiteSpace() {
    RegexParserInput test1Input = new RegexParserInput("  \n\r\t  sl ");
    assertEquals(test1Input.getNextCharacter(), ' ');
    test1Input.goBackOneCharacter();
    RegexParser.advanceUselessSpace(test1Input);
    assertEquals('s', test1Input.getNextCharacter());
    RegexParser.advanceUselessSpace(test1Input);
    assertEquals('l', test1Input.getNextCharacter());
    RegexParser.advanceUselessSpace(test1Input);
  }

  /**
   * Testing to see if accpeting: -CLS_CHAR | e Since e is cannot be represented
   * in the test case Really, we only test see if format of <- CLS_CHAR> is
   * accepted or not
   */
  @Test
  public void TestCharSetTail() {
    String str = " - A";
    RegexParserInput charSetTailTest = new RegexParserInput(str);
    boolean[] expected = { true };

    for (int i = 0; i < expected.length; i++) {
      RegexParserOutput r = RegexParser.char_set_tail(charSetTailTest);
      assertEquals(expected[i], r.isWorkedOrNot());
      // comparing two strings see if they are equal
      assertEquals(str.charAt(3), r.getCharacterParsed());
    }
  }
}
