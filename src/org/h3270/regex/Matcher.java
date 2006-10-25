package org.h3270.regex;

/**
 * An abstract version of class java.util.regex.Matcher that allows
 * for different implementations to be plugged in.  References
 * to CharSequence have been replaced with overloaded methods.
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public abstract class Matcher {

  public abstract Pattern pattern();

  public abstract Matcher reset();

  public abstract Matcher reset(String input);
  public abstract Matcher reset(StringBuffer input);

  public abstract int start();

  public abstract int start(int group);

  public abstract int end();

  public abstract int end(int group);

  public abstract String group();

  public abstract String group(int group);

  public abstract int groupCount();

  public abstract boolean matches();

  public abstract boolean find();

  public abstract boolean find(int start);

  public abstract boolean lookingAt();

  public abstract Matcher appendReplacement(
    StringBuffer sb,
    String replacement);

  public abstract StringBuffer appendTail(StringBuffer sb);

  public abstract String replaceAll(String replacement);

  public abstract String replaceFirst(String replacement);

}
