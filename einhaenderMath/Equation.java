package einhaenderMath;

import helper.PrettyLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

// TODO here's a theory. I think I heard reference on the internet to using strings to identify methods (although I recal that it was
// laggy). So is it possibly to compile a string to a method? if so that would be SO much easier. and probably less laggy, depending on
// how laggy compiling is.

@SuppressWarnings("unused")
public class Equation {
  private static final String[]	FUNCTIONS		 = { "sin", "ln" };
  private static final char[]	OPERATORS		 = { '*', '/', '+', '-', '^' };
  private static final int	TERMTYPEFUNCTION	 = 2;
  private static final int	TERMTYPENUMBER		 = 1;
  private static final int	TERMTYPEOPERATOR	 = 3;
  private static final int	TERMTYPEPARENTHESISCLOSE = 7;
  private static final int	TERMTYPEPARENTHESISOPEN	 = 6;
  // TODO these had warning "unused". are they relics of an older version?
  private static final int	TERMTYPEUNKNOWN		 = 0;
  private static final int	TERMTYPEVARIABLE	 = 5;
  
  private String       equationOrigional;
  private String       equationReformated;
  private final Logger log;
  private final Level  logLevel	   = Level.ALL;
  private String       printPrefix = "";
  private char[]       variables;
  
  public static boolean containsChar(String s, char c) {
    for (int x = 0; x < s.length(); x++)
      if (s.charAt(x) == c)
	return true;
    return false;
  }
  
  private static int[] findInnerParenthesis(String eq) {
    int mostNested = 0;
    int curNested = 0;
    int[] values = new int[2];
    for (int x = 0; x < eq.length(); x++) {
      if (eq.charAt(x) == '(')
	curNested++;
      if (eq.charAt(x) == ')')
	curNested--;
      if (curNested > mostNested)
	mostNested = curNested;
    }
    if (mostNested == 0)
      return null;
    for (int x = 0; x < eq.length(); x++) {
      if (eq.charAt(x) == '(')
	curNested++;
      if (eq.charAt(x) == ')')
	curNested--;
      if (curNested == mostNested) {
	values[0] = x;
	break;
      }
    }
    for (int x = values[0]; x < eq.length(); x++)
      if (eq.charAt(x) == ')') {
	values[1] = x;
	break;
      }
    return values;
  }
  
  @Deprecated
  public static boolean hasArithmetic(String eq) {
    for (int x = 0; x < eq.length(); x++)
      if (((eq.charAt(x) == '+') || (eq.charAt(x) == '-')) && (x != 0))
	return true;
    return false;
  }
  
  public static boolean hasExponent(String eq) {
    boolean has = false;
    for (int c = 0; c < eq.length(); c++)
      if (eq.charAt(c) == '^')
	has = true;
    return has;
  }
  
  @Deprecated
  private static boolean hasMultiplicationOrDivision(String eq) {
    for (int c = 0; c < eq.length(); c++)
      if ((eq.charAt(c) == '*') || (eq.charAt(c) == '/'))
	return true;
    return false;
  }
  
  @Deprecated
  private static boolean hasParenthesis(String eq) {
    for (int x = 0; x < eq.length(); x++)
      if ((eq.charAt(x) == '(') || (eq.charAt(x) == ')'))
	return true;
    return false;
  }
  
  private static boolean isInArray(char letter, char[] charArray) {
    for (int x = 0; x < charArray.length; x++)
      if (charArray[x] == letter)
	return true;
    return false;
  }
  
  public static void main(String[] args) throws Exception {
    char[] cArry = { 'x' };
    Equation e = new Equation("(x-123)(x+3)", cArry);
    e.log.fine(e.equationReformated);
  }
  
  public Equation(char[] variables) throws Exception {
    this("0", variables);
  }
  
  public Equation(String equation, char[] variables) throws Exception {
    equationOrigional = equation;
    equationReformated = equation;
    this.variables = variables;
    log = new PrettyLogger(getClass(), logLevel, true);
    log.setLevel(logLevel);
    reformat();
  }
  
  public double evaluate(double[] values) throws Exception {
    printPrefix = "";
    
    String tempEq = equationReformated;
    for (int x = 0; x < variables.length; x++)
      for (int s = 0; s < tempEq.length(); s++)
	if (tempEq.charAt(s) == variables[x])
	  tempEq = tempEq.substring(0, s) + values[x] + tempEq.substring(s + 1);
    log.fine("Evaluating... " + equationReformated + "=>" + tempEq + "...");
    return evaluateStri(tempEq);
  }
  
  private double evaluateArithmetic(String eq) throws Exception {
    log.finest(printPrefix + "doing arithmetic");
    for (int x = 0; x < eq.length(); x++) {
      if (eq.charAt(x) == '+') {
	int[] a = getTermAt(eq, x - 1);
	String term1 = eq.substring(a[0], a[1] + 1);
	int[] b = getTermAt(eq, x + 1);
	String term2 = eq.substring(b[0], b[1] + 1);
	String rtrnString = eq.substring(0, a[0]) + String.valueOf(evaluateStri(term1) + evaluateStri(term2)) + eq.substring(b[1] + 1);
	return evaluateStri(rtrnString);
      }
      if ((eq.charAt(x) == '-') && (x != 0)) {
	int[] a = getTermAt(eq, x - 1);
	String term1 = eq.substring(a[0], a[1] + 1);
	int[] b = getTermAt(eq, x + 1);
	String term2 = eq.substring(b[0], b[1] + 1);
	String rtrnString = eq.substring(0, a[0]) + String.valueOf(evaluateStri(term1) - evaluateStri(term2)) + eq.substring(b[1] + 1);
	return evaluateStri(rtrnString);
      }
    }
    throw new Error("tried to evaluate a non-existant plus or minus");
  }
  
  private double evaluateExponent(String eq) throws Exception {
    log.finest(printPrefix + "doing exponent");
    for (int x = 0; x < eq.length(); x++)
      if (eq.charAt(x) == '^') {
	int[] a = getTermAt(eq, x - 1);
	String term1 = eq.substring(a[0], a[1] + 1);
	int[] b = getTermAt(eq, x + 1);
	String term2 = eq.substring(b[0], b[1] + 1);
	String rtrnString = eq.substring(0, a[0]) + String.valueOf(Math.pow(evaluateStri(term1), evaluateStri(term2)))
	  + eq.substring(b[1] + 1);
	return evaluateStri(rtrnString);
      }
    throw new Error("tried to evaluate a non-existant  exponent");
  }
  
  private double evaluateMultDevi(String eq) throws Exception {
    log.finest(printPrefix + "doing multiplication||division on " + eq);
    for (int x = 0; x < eq.length(); x++) {
      if (eq.charAt(x) == '*') {
	int[] a = getTermAt(eq, x - 1);
	String term1 = eq.substring(a[0], a[1] + 1);
	int[] b = getTermAt(eq, x + 1);
	String term2 = eq.substring(b[0], b[1] + 1);
	String rtrnString = eq.substring(0, a[0]) + String.valueOf(evaluateStri(term1) * evaluateStri(term2)) + eq.substring(b[1] + 1);
	return evaluateStri(rtrnString);
      }
      if (eq.charAt(x) == '/') {
	int[] a = getTermAt(eq, x - 1);
	String term1 = eq.substring(a[0], a[1] + 1);
	int[] b = getTermAt(eq, x + 1);
	String term2 = eq.substring(b[0], b[1] + 1);
	String rtrnString = eq.substring(0, a[0]) + String.valueOf(evaluateStri(term1) / evaluateStri(term2)) + eq.substring(b[1] + 1);
	return evaluateStri(rtrnString);
      }
    }
    throw new Error("there wasn't a multiplication||division sign in " + eq);
  }
  
  private double evaluatePare(String eq) throws Exception {
    log.finest(printPrefix + "doing parenthesis on " + eq);
    int[] x = findInnerParenthesis(eq);
    String beforeP = eq.substring(0, x[0]);
    String insideP = eq.substring(x[0] + 1, x[1]);
    double insidePF = evaluateStri(insideP);
    String afterP = eq.substring(x[1] + 1);
    String rtrn = beforeP + String.valueOf(insidePF) + afterP;
    return evaluateStri(rtrn);
  }
  
  private double evaluateStri(String eq) throws Exception {
    printPrefix = (" " + printPrefix);
    log.finer(printPrefix + "Evaluateing partial equation \"" + eq + "\"");
    if ((eq.length() == 0) || (eq == null))
      return 0.0D;
    boolean isOneNum = true;
    for (int x = 0; (x < eq.length()) && (isOneNum); x++) {
      char c = eq.charAt(x);
      if (((c < '0') || (c > '9')) && (c != '.') && (c != 'E'))
	isOneNum = false;
      if ((x == 0) && (c == '-'))
	isOneNum = true;
      if ((x > 0) && (c == '-') && (eq.charAt(x - 1) == 'E'))
	isOneNum = true;
    }
    if (isOneNum)
      return Double.valueOf(eq).doubleValue();
    printPrefix = printPrefix.substring(1);
    if ((containsChar(eq, '(')) || (containsChar(eq, ')')))
      return evaluatePare(eq);
    if (containsChar(eq, '^'))
      return evaluateExponent(eq);
    if ((containsChar(eq, '*')) || (containsChar(eq, '/')))
      return evaluateMultDevi(eq);
    if ((containsChar(eq, '+')) || (containsChar(eq, '-')))
      return evaluateArithmetic(eq);
    log.warning(
      eq + " was recognized as not being a number but an operation cannot be identified. returning Double.valueOf(eq)".toUpperCase());
    return Double.valueOf(eq).doubleValue();
  }
  
  private int findChar(char c) {
    return findChar(c, 0);
  }
  
  private int findChar(char c, int start) {
    for (int x = start + 1; x < equationReformated.length(); x++)
      if (equationReformated.charAt(x) == c)
	return x;
    return -1;
  }
  
  public String getEquation() {
    return equationReformated;
  }
  
  private int[] getTermAt(String eq, int i) {
    int[] term = { i, i };
    boolean found = false;
    char chrI = eq.charAt(i);
    if ((isInArray(chrI, OPERATORS)) && ((i != 0) || (chrI != '-')))
      if ((i == 0) || (chrI != '-') || (!isInArray(eq.charAt(i - 1), OPERATORS)))
	throw new IllegalArgumentException(
	  "given argument '" + i + "' corresponds to the character '" + eq.charAt(i) + "' which cannot be part of a term.");
    for (int x = i; (x >= 0) && (!found); x--) {
      if (isInArray(eq.charAt(x), OPERATORS))
	found = true;
      if ((x == 0) && (eq.charAt(x) == '-'))
	found = false;
      if ((x != 0) && (isInArray(eq.charAt(x - 1), OPERATORS)) && (eq.charAt(x) == '-'))
	found = false;
      term[0] = x;
    }
    if (found)
      term[0] += 1;
    if ((term[0] > 1) && (eq.charAt(term[0] - 1) == 'E'))
      term[0] = getTermAt(eq, term[0] - 2)[0];
    found = false;
    for (int x = i; (x < eq.length()) && (!found); x++) {
      if ((isInArray(eq.charAt(x), OPERATORS)) || (isInArray(eq.charAt(x), variables)))
	found = true;
      if ((x == 0) && (eq.charAt(x) == '-'))
	found = false;
      if ((x != 0) && (isInArray(eq.charAt(x - 1), OPERATORS)) && (eq.charAt(x) == '-'))
	found = false;
      term[1] = x;
    }
    if (found)
      term[1] -= 1;
    if ((term[1] < eq.length() - 2) && (eq.charAt(term[1]) == 'E'))
      term[1] = getTermAt(eq, term[1] + 2)[1];
    return term;
  }
  
  private int getTermType(String eq, int i) {
    char c = eq.charAt(i);
    if ((c >= '0') && (c <= '9'))
      return 1;
    if (isInArray(c, OPERATORS))
      return 3;
    if (isInArray(c, variables))
      return 5;
    if (c == '(')
      return 6;
    if (c == ')')
      return 7;
    for (int x = 0; x < FUNCTIONS.length; x++) {
      int ind = eq.indexOf(FUNCTIONS[x]);
      int funcLen = FUNCTIONS[x].length();
      if ((i >= ind) && (i < ind + funcLen))
	return 2;
    }
    return 0;
  }
  
  public char[] getVariables() {
    return variables;
  }
  
  @Deprecated
  public boolean hasDivisionOrMultiplication() {
    for (int x = 0; x < equationReformated.length(); x++)
      if ((equationReformated.charAt(x) == '*') || (equationReformated.charAt(x) == '/'))
	return true;
    return false;
  }
  
  public boolean hasMultiplicationOrDivision() {
    boolean has = false;
    for (int c = 0; c < equationReformated.length(); c++)
      if ((equationReformated.charAt(c) == '*') || (equationReformated.charAt(c) == '/'))
	has = true;
    return has;
  }
  
  @Deprecated
  public boolean hasParenthis() {
    boolean has = false;
    for (int c = 0; c < equationReformated.length(); c++)
      if ((equationReformated.charAt(c) == '(') || (equationReformated.charAt(c) == ')'))
	has = true;
    return has;
  }
  
  private boolean needsMultiplicationRight(int currLoc) {
    int typeThis = getTermType(equationReformated, currLoc);
    int typeRight = getTermType(equationReformated, currLoc + 1);
    if (typeRight == typeThis)
      return false;
    if ((typeRight == 3) || (typeThis == 3))
      return false;
    if ((typeRight == 7) || (typeThis == 6))
      return false;
    return true;
  }
  
  private void reformat() throws Exception {
    equationReformated = equationReformated.trim();
    for (int x = 0; x < equationReformated.length(); x++)
      if (equationReformated.charAt(x) == '-')
	equationReformated = (equationReformated.substring(0, x) + "-1*" + equationReformated.substring(x + 1));
    for (int x = 0; x < equationReformated.length(); x++)
      if ((x != equationReformated.length() - 1) && (needsMultiplicationRight(x)))
	equationReformated = (equationReformated.substring(0, x + 1) + "*" + equationReformated.substring(x + 1));
    int parenthesisCount = 0;
    for (int x = 0; x < equationReformated.length(); x++) {
      if (equationReformated.charAt(x) == '(')
	parenthesisCount++;
      if (equationReformated.charAt(x) == ')')
	parenthesisCount--;
      if (parenthesisCount < 0)
	throw new Exception(
	  "invalid equation " + equationOrigional + ": close parenthesis at index " + x + " has no corresponding open parenthisis");
    }
    if (parenthesisCount != 0)
      throw new Exception("invalid equation " + equationOrigional + ": parenthisies were unbalenced");
    for (int x = 0; x < equationReformated.length(); x++)
      if (getTermType(equationReformated, x) == 0)
	throw new Exception("The character at index " + x + " in the equation " + equationReformated
	  + " cannot be identified. The origional equation was " + equationOrigional + ".");
  }
  
  public void setEquation(String equation) throws Exception {
    equationReformated = equation;
    reformat();
  }
  
  @Override
  public String toString() {
    String allVars = "";
    for (char c : variables)
      allVars = allVars + c + ",";
    allVars = allVars.substring(0, allVars.length() - 1);
    return "f(" + allVars + ") = " + equationOrigional;
  }
}