package org.h3270.regex;

/**
 * An implementation of org.h3270.regex.Pattern that delegates to 
 * java.util.regex.Pattern.
 *
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class JDKPattern extends Pattern implements java.io.Serializable {

  private java.util.regex.Pattern delegate = null;

  public JDKPattern(String regex, int flags) {
    this.delegate = java.util.regex.Pattern.compile(regex, flags);
  }

  protected JDKPattern(java.util.regex.Pattern delegate) {
    this.delegate = delegate;
  }

  public String pattern() {
    return delegate.pattern();
  }

  public Matcher matcher(String input) {
    java.util.regex.Matcher result = delegate.matcher(input);
    return new JDKMatcher(result);
  }

  public Matcher matcher(StringBuffer input) {
    java.util.regex.Matcher result = delegate.matcher(input);
    return new JDKMatcher(result);
  }

  public int flags() {
    return delegate.flags();
  }

  public String[] split(String input, int limit) {
    return delegate.split(input, limit);
  }

  public String[] split(StringBuffer input, int limit) {
    return delegate.split(input, limit);
  }

  public String[] split(String input) {
    return delegate.split(input);
  }

  public String[] split(StringBuffer input) {
    return delegate.split(input);
  }

}
