package org.drools.brms.client.common;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This nifty utility provides auto completion. Drop in replacement for a text box.
 * I recall lifting it from somewhere on http://del.icio.us/michaelneale/GWT, plus some 
 * small tweaks. 
 * 
 * If this gives any back chat, I will shut it down and we can just use a regular text box.
 * 
 */
public class AutoCompleteTextBoxAsync extends TextBox
    implements KeyboardListener {

  protected CompletionItemsAsync items = null;
  protected boolean popupAdded = false;
  protected boolean visible = false;
  protected PopupPanel choicesPopup = new PopupPanel(true);
  protected ListBox choices = new ListBox() {
    public void onBrowserEvent(Event event) {
      if (Event.ONCLICK == DOM.eventGetType(event)) {
        complete();
      }
    }
  };

  /**
   * Default Constructor
   */
  public AutoCompleteTextBoxAsync(CompletionItemsAsync comp)
  {
    super();
    this.addKeyboardListener(this);
    choices.sinkEvents(Event.ONCLICK);
    this.setStyleName("AutoCompleteTextBox");

    choicesPopup.add(choices);
    choicesPopup.addStyleName("AutoCompleteChoices");

    choices.setStyleName("list");
    this.items = comp;
  }

  /**
   * Sets an "algorithm" returning completion items
   * You can define your own way how the textbox retrieves
autocompletion items
   * by implementing the CompletionItems interface and setting the
according object
   * @see SimpleAutoCompletionItem
   * @param items CompletionItem implementation
   */
  public void setCompletionItems(CompletionItemsAsync items)
  {
    this.items = items;
  }

  /**
   * Returns the used CompletionItems object
   * @return CompletionItems implementation
   */
  public CompletionItemsAsync getCompletionItems()
  {
    return this.items;
  }

  /**
   * Handle events that happen when keys are pressed.
   */
  public void onKeyDown(Widget arg0, char arg1, int arg2) {
    if(arg1 == KEY_ENTER)
    {
      enterKey(arg0, arg1, arg2);
    }
    else if(arg1 == KEY_TAB)
    {
      tabKey(arg0, arg1, arg2);
    }
    else if(arg1 == KEY_DOWN)
    {
      downKey(arg0, arg1, arg2);
    }
    else if(arg1 == KEY_UP)
    {
      upKey(arg0, arg1, arg2);
    }
    else if(arg1 == KEY_ESCAPE)
    {
      escapeKey(arg0, arg1, arg2);
    }
  }

  /**
   * Not used at all
   */
  public void onKeyPress(Widget arg0, char arg1, int arg2) {
  }

  /**
   * Handle events that happen when keys are released.
   */
  public void onKeyUp(Widget arg0, char arg1, int arg2) {
    switch(arg1) {
      case KEY_ALT:
      case KEY_CTRL:
      case KEY_DOWN:
      case KEY_END:
      case KEY_ENTER:
      case KEY_ESCAPE:
      case KEY_HOME:
      case KEY_LEFT:
      case KEY_PAGEDOWN:
      case KEY_PAGEUP:
      case KEY_RIGHT:
      case KEY_SHIFT:
      case KEY_TAB:
      case KEY_UP:
        break;
      default:
        otherKey(arg0, arg1, arg2);
        break;
    }
  }

  // The down key was pressed.
  protected void downKey(Widget arg0, char arg1, int arg2) {
    int selectedIndex = choices.getSelectedIndex();
    selectedIndex++;
    if (selectedIndex >= choices.getItemCount())
    {
      selectedIndex = 0;
    }
    choices.setSelectedIndex(selectedIndex);
  }

  // The up key was pressed.
  protected void upKey(Widget arg0, char arg1, int arg2) {
    int selectedIndex = choices.getSelectedIndex();
    selectedIndex--;
    if(selectedIndex < 0)
    {
      selectedIndex = choices.getItemCount() - 1;
    }
    choices.setSelectedIndex(selectedIndex);
  }

  // The enter key was pressed.
  protected void enterKey(Widget arg0, char arg1, int arg2) {
    complete();
  }

  // The tab key was pressed.
  protected void tabKey(Widget arg0, char arg1, int arg2) {
    complete();
  }

  // The escape key was pressed.
  protected void escapeKey(Widget arg0, char arg1, int arg2) {
    choices.clear();
    choicesPopup.hide();
    this.visible = false;
  }

  // Any other non-special key was pressed.
  protected void otherKey(Widget arg0, char arg1, int arg2) {
    // Update the existing choices in the list box to reflect the user's entry.
    updateChoices(this.getText());

    // If any text was entered, start an async callback.
    if (this.getText().length() > 0 && items != null) {
      items.getCompletionItems(this.getText(), new CompletionItemsAsyncReturn() {
        public void itemReturn(String[] matches) {
          updateChoices(matches, getText());
        }
      });
    }
  }

  // Hides/shows the choice box as needed.
  // Assumes all choices are currently valid.
  protected void hideChoicesIfNeeded(String text) {
    // Hide the list box under any of these conditions:
    // - the text box is empty
    // - there are no matching choices
    // - there is only one choice that exactly matches the text box entry
    // Show the list box under any other condition.
    if (0 == text.length() || 0 == choices.getItemCount() ||
      (1 == choices.getItemCount() &&
choices.getItemText(0).equals(text))) {
      choices.clear();
      choicesPopup.hide();
      visible = false;
    } else {
      choices.setSelectedIndex(0);
      choices.setVisibleItemCount(choices.getItemCount() + 1);

      if(!popupAdded) {
        RootPanel.get().add(choicesPopup);
        popupAdded = true;
      }
      choicesPopup.show();
      visible = true;
      choicesPopup.setPopupPosition(this.getAbsoluteLeft(),
        this.getAbsoluteTop() + this.getOffsetHeight());
      choices.setWidth(this.getOffsetWidth() + "px");
    }
  }

  // Removes all items in the choices menu that do not start with the specified text.
  protected void updateChoices(String text) {
    int i = 0;
    while (i < choices.getItemCount()) {
      if (choices.getItemText(i).toLowerCase().startsWith(text.toLowerCase())) {
        ++i;
      } else {
        choices.removeItem(i);
      }
    }
    hideChoicesIfNeeded(text);
  }

  // Update the choices menu using the provided matches and entered text.
  protected void updateChoices(String[] matches, String text) {
    choices.clear();
    for(int i = 0; i < matches.length; i++)
    {
      choices.addItem((String) matches[i]);
    }
    updateChoices(text);
  }

  // add selected item to textbox
  protected void complete()
  {
    if(this.visible && choices.getItemCount() > 0)
    {
      this.setText(choices.getItemText(choices.getSelectedIndex()));
    }
    choices.clear();
    choicesPopup.hide();
    this.visible = false;
  }

} 