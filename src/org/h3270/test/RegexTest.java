package org.h3270.test;

import junit.framework.TestCase;

import org.h3270.regex.*;

public class RegexTest extends TestCase {

  public RegexTest(String name) {
    super(name);
  }

  public void test_find_1() {
    Pattern p = Pattern.compile ("abc");
    Matcher m = p.matcher("abc abc abc");

    assertTrue(m.find());
    assertEquals(0, m.start());
    assertEquals(3, m.end());

    assertTrue(m.find());
    assertEquals(4, m.start());
    assertEquals(7, m.end());

    assertTrue(m.find());
    assertEquals(8, m.start());
    assertEquals(11, m.end());

    assertFalse(m.find());
  }

  public void test_find_2() {
    Pattern p = Pattern.compile ("abc");
    Matcher m = p.matcher("abc abc abc");
  
    assertTrue(m.find(2));
    assertEquals(4, m.start());
    assertEquals(7, m.end());
  }
  
  public void test_matches_1() {
    Matcher m = Pattern.compile("a..").matcher("abc");
    assertTrue (m.matches());
  }
  
  public void test_matches_2() {
    Matcher m = Pattern.compile("a..").matcher("abc ");
    assertFalse (m.matches());
  }
  
  public void test_matches_3() {
    Matcher m = Pattern.compile("a..").matcher(" abc");
    assertFalse (m.matches());
  }
  
  public void test_lookingAt_1() {
    Matcher m = Pattern.compile("a..").matcher("abc ");
    assertTrue (m.lookingAt());
  }
  
  public void test_lookingAt_2() {
    Matcher m = Pattern.compile("a..").matcher(" abc ");
    assertFalse (m.lookingAt());
  }
  
  public void test_case_insensitive() {
    Pattern p = Pattern.compile("abc", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher("abc ABC ABc");
     
    assertTrue(m.find());
    assertEquals(0, m.start());
    assertEquals(3, m.end());

    assertTrue(m.find());
    assertEquals(4, m.start());
    assertEquals(7, m.end());

    assertTrue(m.find());
    assertEquals(8, m.start());
    assertEquals(11, m.end());

    assertFalse(m.find());
  }

  public void test_case_sensitive() {
    Pattern p = Pattern.compile("abc");
    Matcher m = p.matcher("ABC abc ABc");
     
    assertTrue(m.find());
    assertEquals(4, m.start());
    assertEquals(7, m.end());

    assertFalse(m.find());
  }

  public void test_group_1() {
    Pattern p = Pattern.compile("abc");
    Matcher m = p.matcher("abc abc abc");
  
    assertTrue(m.find());
    assertEquals("abc", m.group());

    assertTrue(m.find());
    assertEquals("abc", m.group());

    assertTrue(m.find());
    assertEquals("abc", m.group());
  }

  public void test_group_2() {
    Pattern p = Pattern.compile("a(bc)?");
    Matcher m = p.matcher("abc abc a");
  
    assertTrue(m.find());
    assertEquals("bc", m.group(1));

    assertTrue(m.find());
    assertEquals("bc", m.group(1));

    assertTrue(m.find());
    assertEquals(null, m.group(1));
  }

  public void test_replaceAll() {
    Pattern p = Pattern.compile("a(b)c");
    Matcher m = p.matcher("abc abc abc");

    String result = m.replaceAll("-$1-");
    assertEquals("-b- -b- -b-", result);
  }
  
  public void test_replaceFirst() {
    Pattern p = Pattern.compile("a(b)c");
    Matcher m = p.matcher("abx abc abc");

    String result = m.replaceFirst("-$1-");
    assertEquals("abx -b- abc", result);
  }
  
  public void test_appendReplacement() {
    Pattern p = Pattern.compile("a(bc)");
    Matcher m = p.matcher("abc abc abc");
    
    StringBuffer result = new StringBuffer();
    while (m.find())
      m.appendReplacement (result, "x$1");
    m.appendTail(result);
    
    assertEquals("xbc xbc xbc", result.toString()); 
  }
  
  /**
   * Taken from "Mastering Regular Expressions", 2nd ed., p. 389.
   */
  public void test_fahrenheit() {
    String metric = "from 36.3C to 40.1C.";
    Matcher m = Pattern.compile("(\\d+(?:\\.(\\d))?)C\\b").matcher(metric);
    
    StringBuffer result = new StringBuffer();
    while (m.find()) {
      float celsius = Float.parseFloat(m.group(1));
      int fahrenheit = (int)(celsius * 9/5 + 32);
      m.appendReplacement (result, fahrenheit + "F");
    }
    m.appendTail(result);
    
    assertEquals ("from 97F to 104F.", result.toString());
  }
}
