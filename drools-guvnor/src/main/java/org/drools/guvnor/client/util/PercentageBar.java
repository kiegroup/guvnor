package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;

public class PercentageBar extends HTML {

    interface PercentageBarTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class=\"smallish-progress-bar\" style=\"width: {0}px; background-color: {1};\"></div>")
        SafeHtml barHtml(int pixels,
                         String colour);

        @Template("<div class=\"smallish-progress-text\" style=\"width: {0}px\">{1} %</div>")
        SafeHtml textHtml(int width,
                          String percent);

        @Template("<div class=\"smallish-progress-wrapper\" style=\"width: {0}px\">{1}{2}</div>")
        SafeHtml mainHtml(int width,
                          SafeHtml bar,
                          SafeHtml text);
    }

    private static final PercentageBarTemplate TEMPLATE = GWT.create( PercentageBarTemplate.class );

    private String                             colour   = "GREEN";
    private int                                width    = 100;
    private float                              percent  = 100;

    public PercentageBar() {
    }

    public PercentageBar(String colour,
                         int width,
                         float percent) {
        this.colour = colour;
        this.width = width;
        this.percent = percent;

        render();
    }

    public PercentageBar(String colour,
                         int width,
                         int numerator,
                         int denominator) {
        this( colour,
              width,
              calculatePercent( numerator,
                                denominator ) );
    }

    public void render() {
        int pixels = (int) (width * (percent / 100));
        setHTML( TEMPLATE.mainHtml( width,
                                    TEMPLATE.barHtml( pixels,
                                                      colour ),
                                    TEMPLATE.textHtml( width,
                                                       Float.toString( percent ) ) ) );
    }

    private static int calculatePercent(int numerator,
                                        int denominator) {
        int percent = 0;

        if ( denominator != 0 ) {
            percent = (int) ((((float) denominator - (float) numerator) / (float) denominator) * 100);
        }

        return percent;
    }

    public void setColour(String colour) {
        this.colour = colour;
        render();
    }

    public void setWidth(int width) {
        this.width = width;
        render();
    }

    public void setPercent(float percent) {
        this.percent = percent;
        render();
    }

    public void setPercent(int numerator,
                           int denominator) {
        this.percent = calculatePercent( numerator,
                                         denominator );
        render();
    }
}
