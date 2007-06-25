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



import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.Widget;

public class DatePicker extends Composite 
    implements ClickListener, SourcesChangeEvents {

  private class NavBar extends Composite implements ClickListener {

    public final DockPanel bar = new DockPanel();
    public final Button prevMonth = new Button("&lt;", this);
    public final Button prevYear = new Button("&lt;&lt;", this);
    public final Button nextYear = new Button("&gt;&gt;", this);
    public final Button nextMonth = new Button("&gt;", this);
    public final HTML title = new HTML();

    private final DatePicker calendar;

    public NavBar(DatePicker calendar) {
      this.calendar = calendar;

      initWidget(bar);
      bar.setStyleName("navbar");
      title.setStyleName("header");

      HorizontalPanel prevButtons = new HorizontalPanel();
      prevButtons.add(prevMonth);
      prevButtons.add(prevYear);

      HorizontalPanel nextButtons = new HorizontalPanel();
      nextButtons.add(nextYear);
      nextButtons.add(nextMonth);

      bar.add(prevButtons, DockPanel.WEST);
      bar.setCellHorizontalAlignment(prevButtons, DockPanel.ALIGN_LEFT);
      bar.add(nextButtons, DockPanel.EAST);
      bar.setCellHorizontalAlignment(nextButtons, DockPanel.ALIGN_RIGHT);
      bar.add(title, DockPanel.CENTER);
      bar.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
      bar.setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
      bar.setCellVerticalAlignment(title, HasAlignment.ALIGN_MIDDLE);
      bar.setCellWidth(title, "100%");
    }

    public void onClick(Widget sender) {
      if (sender == prevMonth) {
        calendar.prevMonth();
      } else if (sender == prevYear) {
        calendar.prevYear();
      } else if (sender == nextYear) {
        calendar.nextYear();
      } else if (sender == nextMonth) {
        calendar.nextMonth();
      }
    }
  }

  private static class CellHTML extends HTML {
    private int day;
       
    public CellHTML(String text, int day) {
      super(text);
      this.day = day;
    }
       
    public int getDay() {
      return day;
    }
  }
  
  private final NavBar navbar = new NavBar(this);
  private final DockPanel outer = new DockPanel();

  private final Grid grid = new Grid(6, 7) {
    public boolean clearCell(int row, int column) {
      boolean retValue = super.clearCell(row, column);
           
      Element td = getCellFormatter().getElement(row, column);
      DOM.setInnerHTML(td, "");
      return retValue;
    }
  };

  private Date date = new Date();

  private ChangeListenerCollection changeListeners;
   
  private String[] days = new String[] { "Sunday", "Monday", "Tuesday",
      "Wednesday", "Thursday", "Friday", "Saturday" };

  private String[] months = new String[] { "January", "February", "March",
      "April", "May", "June", "July", "August", "September", "October",
      "November", "December" };

  public DatePicker() {
    initWidget(outer);
    grid.setStyleName("table");
    grid.setCellSpacing(0);
    outer.add(navbar, DockPanel.NORTH);
    outer.add(grid, DockPanel.CENTER);
    drawCalendar();
    setStyleName("DatePicker");
  }

  private void drawCalendar() {
    int year = getYear();
    int month = getMonth();
    int day = getDay();
    setHeaderText(year, month);
    grid.getRowFormatter().setStyleName(0, "weekheader");
    for (int i = 0; i < days.length; i++) {
      grid.getCellFormatter().setStyleName(0, i, "days");
      grid.setText(0, i, days[i].substring(0, 3));
    }

    Date now = new Date();
    int sameDay = now.getDate();
    int today = (now.getMonth() == month && now.getYear()+1900 == year) ? sameDay : 0;
       
    int firstDay = new Date(year - 1900, month, 1).getDay();
    int numOfDays = getDaysInMonth(year, month);

    int j = 0;
    for (int i = 1; i < 6; i++) {
      for (int k = 0; k < 7; k++, j++) {
        int displayNum = (j - firstDay + 1);
        if (j < firstDay || displayNum > numOfDays) {
          grid.getCellFormatter().setStyleName(i, k, "empty");
          grid.setHTML(i, k, "&nbsp;");
        } else {
          HTML html = new CellHTML(
            "<span>" + String.valueOf(displayNum) + "</span>", 
            displayNum);
          html.addClickListener(this);
          grid.getCellFormatter().setStyleName(i, k, "cell");
          if (displayNum == today) {
            grid.getCellFormatter().addStyleName(i, k, "today");
          } else if (displayNum == sameDay) {
            grid.getCellFormatter().addStyleName(i, k, "day");
          }
          grid.setWidget(i, k, html);
        }
      }
    }
  }

  protected void setHeaderText(int year, int month) {
    navbar.title.setText(months[month] + ", " + year);
  }
   
  private int getDaysInMonth(int year, int month) {
    switch (month) {
      case 1:
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
          return 29; // leap year
        else
          return 28;
      case 3:
        return 30;
      case 5:
        return 30;
      case 8:
        return 30;
      case 10:
        return 30;
      default:
        return 31;
    }
  }

  public void prevMonth() {
    int month = getMonth() - 1;
    if (month < 0) {
      setDate(getYear() - 1, 11, getDay());
    } else {
      setMonth(month);
    }
    drawCalendar();
  }

  public void nextMonth() {
    int month = getMonth() + 1;
    if (month > 11) {
      setDate(getYear() + 1, 0, getDay());
    } else {
      setMonth(month);
    }
    drawCalendar();
  }
   
  public void prevYear() {
    setYear(getYear() - 1);
    drawCalendar();
  }

  public void nextYear() {
    setYear(getYear() + 1);
    drawCalendar();
  }

  private void setDate(int year, int month, int day) {
    date = new Date(year - 1900, month, day);
  }
   
  private void setYear(int year) {
    date.setYear(year - 1900);
  }

  private void setMonth(int month) {
    date.setMonth(month);
  }

  public int getYear() {
    return 1900 + date.getYear();
  }

  public int getMonth() {
    return date.getMonth();
  }

  public int getDay() {
    return date.getDate();
  }
   
  public Date getDate() {
    return date;
  }

  public void onClick(Widget sender) {
    CellHTML cell = (CellHTML)sender;
    setDate(getYear(), getMonth(), cell.getDay());
    drawCalendar();
    if (changeListeners != null) {
      changeListeners.fireChange(this);
    }
  }

  public void addChangeListener(ChangeListener listener) {
    if (changeListeners == null)
      changeListeners = new ChangeListenerCollection();
    changeListeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    if (changeListeners != null)
      changeListeners.remove(listener);
  }
}  