package org.h3270.regex;

import java.lang.reflect.Constructor;

/**
 * An abstract version of class java.util.regex.Pattern that allows
 * for different implementations to be plugged in.  References
 * to CharSequence have been replaced with overloaded methods.
 * 
 * When you create an instance via Pattern.compile(), this class selects
 * either the native JDK regex implementation (if you have JDK 1.4 or better),
 * or an implementation based on the JRegex package for earlier JDKs.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public abstract class Pattern implements java.io.Serializable {

  public static final int UNIX_LINES = 0x01;
  public static final int CASE_INSENSITIVE = 0x02;
  public static final int COMMENTS = 0x04;
  public static final int MULTILINE = 0x08;
  public static final int DOTALL = 0x20;
  public static final int UNICODE_CASE = 0x40;
  public static final int CANON_EQ = 0x80;

  /**
   * Stores either the JDKPattern class or the JRegexPattern class,
   * depending on which JDK version we have.  This field is initialized
   * on demand in getPatternClass().
   */
  private static Class       patternClass       = null;

  /**
   * Stores a reference to the (String, int) constructor of the class
   * that is stored in the patternClass field.
   */
  private static Constructor patternConstructor = null;

  public static Pattern compile(String regex) {
    return Pattern.compile(regex,0);
  }

  public static Pattern compile(String regex, int flags) {
    try {
      return (Pattern)getPatternConstructor()
                .newInstance (new Object[] { regex, new Integer(flags) });
    } catch (IllegalAccessException ex) {
      throw new RuntimeException("error: " + ex);
    } catch (InstantiationException ex) {
      throw new RuntimeException("error: " + ex);
    } catch (java.lang.reflect.InvocationTargetException ex) {
      throw (RuntimeException)ex.getTargetException();
    }
  }

  /**
   * Returns the constructor for (String, int) for the class that is
   * returned by getPatternClass().  Using this constructor to create a
   * Pattern object is a JDK-independent way to use regular expressions.
   * 
   * @return the appropriate constructor
   */
  private static Constructor getPatternConstructor() {
    if (patternConstructor == null) {
      Class c = getPatternClass();
      try {
        patternConstructor = 
          c.getConstructor (new Class[]{ String.class, int.class });
      } catch (NoSuchMethodException ex) {
        throw new RuntimeException
          ("no constructor for (String, int) in Pattern class, " + c);
      }
    }
    return patternConstructor;
  }

  /**
   * Returns either the class JDKPattern or JRegexPattern from this package,
   * depending on which JDK version we have.
   *
   * @return the loaded class
   */
  private static Class getPatternClass() {
    if (patternClass == null) {
      String version = System.getProperty("java.version");
      if (version.startsWith("1.2") || version.startsWith("1.3"))
        patternClass = loadClass ("org.h3270.regex.JRegexPattern");
      else
        patternClass = loadClass ("org.h3270.regex.JDKPattern");
    }
    return patternClass; 
  }

  /**
   * Attempts to load the Java class with the given name and returns it,
   * or null if no such class can be found.
   * 
   * @param name the fully qualified name of the class to load
   * @return the loaded class, or null if the class could not be found
   */
  private static Class loadClass (String name) {
    try {
      Class result = 
        Thread.currentThread().getContextClassLoader().loadClass(name);
      return result;
    } catch (ClassNotFoundException ex) {
      return null;
    }      
  }

  public abstract String pattern();

  public abstract Matcher matcher(String input);
  public abstract Matcher matcher(StringBuffer input);

  public abstract int flags();

  public static boolean matches(String regex, String input) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
  }

  public static boolean matches(String regex, StringBuffer input) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
  }

  public abstract String[] split(String input, int limit);
  public abstract String[] split(StringBuffer input, int limit);

  public abstract String[] split(String input);
  public abstract String[] split(StringBuffer input);

}
