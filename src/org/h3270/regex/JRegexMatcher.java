package org.h3270.regex;

/**
 * An implementation of org.h3270.regex.Matcher that delegates to a
 * jregex.Matcher.
 * 
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class JRegexMatcher extends Matcher {

  private jregex.Matcher delegate = null;
  private int appendIndex = 0;
  
  protected JRegexMatcher(jregex.Matcher delegate) {
    this.delegate = delegate;
  }

  public Pattern pattern() {
    jregex.Pattern result = delegate.pattern();
    return new JRegexPattern (result);
  }

  public Matcher reset() {
    delegate.setPosition(0);
    appendIndex = 0;
    return this;
  }

  public Matcher reset(String input) {
    delegate.setTarget(input);
    appendIndex = 0;
    return this;
  }

  public Matcher reset(StringBuffer input) {
    delegate.setTarget(input.toString());
    appendIndex = 0;
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
    delegate.setPosition(start);
    return delegate.find();
  }

  public boolean lookingAt() {
    delegate.setPosition(0);
    return delegate.find(jregex.Matcher.ANCHOR_START);
  }

  public Matcher appendReplacement(StringBuffer sb, String replacement) {
    String target = delegate.target();
    sb.append (target.substring (appendIndex, this.start()));
    jregex.Substitution s = new jregex.PerlSubstitution (replacement);
    s.appendSubstitution (delegate, jregex.Replacer.wrap(sb));
    appendIndex = this.end();
    return this;
  }

  public StringBuffer appendTail(StringBuffer sb) {
    String target = delegate.target();
    sb.append (target.substring(appendIndex));
    return sb;
  }

  public String replaceAll(String replacement) {
    jregex.Replacer r = 
      delegate.pattern().replacer(new jregex.PerlSubstitution(replacement));
    return r.replace (delegate.target());
  }

  public String replaceFirst(String replacement) {
    this.reset();
    if (this.find()) {
      StringBuffer result = new StringBuffer();
      appendReplacement(result, replacement);
      appendTail(result);
      return result.toString();
    } else
      return delegate.target();
  }

}
