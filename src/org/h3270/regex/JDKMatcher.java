package org.h3270.regex;

/**
 * An implementation of org.h3270.regex.Matcher that delegates to
 * java.util.regex.Matcher.
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class JDKMatcher extends Matcher {

  private java.util.regex.Matcher delegate = null;

  JDKMatcher(java.util.regex.Matcher delegate) {
    this.delegate = delegate;
  }

  public Pattern pattern() {
    java.util.regex.Pattern result = delegate.pattern();
    return new JDKPattern(result);
  }

  public Matcher reset() {
    delegate.reset();
    return this;
  }

  public Matcher reset(String input) {
    delegate.reset(input);
    return this;
  }

  public Matcher reset(StringBuffer input) {
    delegate.reset(input);
    return this;
  }

  public int start() {
    return delegate.start();
  }

  public int start(int group) {
    return delegate.start(group);
  }

  public int end() {
    return delegate.end();
  }

  public int end(int group) {
    return delegate.end(group);
  }

  public String group() {
    return delegate.group(0);
  }

  public String group(int group) {
    return delegate.group(group);
  }

  public int groupCount() {
    return delegate.groupCount();
  }

  public boolean matches() {
    return delegate.matches();
  }

  public boolean find() {
    return delegate.find();
  }

  public boolean find(int start) {
    return delegate.find(start);
  }

  public boolean lookingAt() {
    return delegate.lookingAt();
  }

  public Matcher appendReplacement(StringBuffer sb, String replacement) {
    delegate.appendReplacement(sb, replacement);
    return this;
  }

  public StringBuffer appendTail(StringBuffer sb) {
    return delegate.appendTail(sb);
  }

  public String replaceAll(String replacement) {
    return delegate.replaceAll(replacement);
  }

  public String replaceFirst(String replacement) {
    return delegate.replaceFirst(replacement);
  }

}
