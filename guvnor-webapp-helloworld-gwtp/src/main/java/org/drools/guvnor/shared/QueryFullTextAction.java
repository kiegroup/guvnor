package org.drools.guvnor.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;


public class QueryFullTextAction extends
    UnsecuredActionImpl<QueryFullTextResult> {

  private String searchText;

  public QueryFullTextAction(final String searchText) {
    this.searchText = searchText;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private QueryFullTextAction() {
  }

  public String getSearchText() {
    return searchText;
  }
}
