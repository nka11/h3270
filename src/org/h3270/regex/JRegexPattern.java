package org.h3270.regex;

import jregex.REFlags;

/**
 * An implementation of org.h3270.regex.Pattern that delegates to 
 * a jregex.Pattern.
 * 
 * @author <a href="mailto:andre.spiegel@it-fws.de">Andre Spiegel</a>
 * @version $Id$
 */
public class JRegexPattern extends Pattern {

  private jregex.Pattern delegate = null;
  
  private static final int compatibleFlags = 
    MULTILINE | DOTALL | CASE_INSENSITIVE | COMMENTS;
  
  public JRegexPattern(String regex, int jdkFlags) {
    if (jdkFlags == 0)
      delegate = new jregex.Pattern (regex);
    else if (   ((jdkFlags & CANON_EQ) != 0)
             || ((jdkFlags & UNICODE_CASE) != 0)
             || ((jdkFlags & UNIX_LINES) != 0))
      throw new RuntimeException 
        ("JRegex does not support CANON_EQ, UNICODE_CASE, and UNIX_LINES");
    else {
      int flags = 0;
      if ((jdkFlags & MULTILINE)        != 0) flags |= REFlags.MULTILINE;
      if ((jdkFlags & DOTALL)           != 0) flags |= REFlags.DOTALL;
      if ((jdkFlags & CASE_INSENSITIVE) != 0) flags |= REFlags.IGNORE_CASE;
      if ((jdkFlags & COMMENTS)         != 0) flags |= REFlags.IGNORE_SPACES; 
      delegate = new jregex.Pattern (regex, flags);
    }
  }
  
  protected JRegexPattern(jregex.Pattern delegate) {
    this.delegate = delegate;
  }

  public String pattern() {
    return delegate.toString();
  }

  public Matcher matcher(String input) {
    jregex.Matcher result = delegate.matcher (input);
    return new JRegexMatcher (result);
  }

  public Matcher matcher(StringBuffer input) {
    return matcher(input.toString());
  }

  public int flags() {
    throw new RuntimeException ("flags() not implemented for JRegex");
  }

  public String[] split(String input, int limit) {
    throw new RuntimeException ("split() not implemented for JRegex");
  }

  public String[] split(StringBuffer input, int limit) {
    throw new RuntimeException ("split() not implemented for JRegex");
  }

  public String[] split(String input) {
    throw new RuntimeException ("split() not implemented for JRegex");
  }

  public String[] split(StringBuffer input) {
    throw new RuntimeException ("split() not implemented for JRegex");
  }

}
