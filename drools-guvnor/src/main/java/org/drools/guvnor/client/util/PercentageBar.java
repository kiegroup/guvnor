package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PercentageBar extends Composite {

    interface PercentageBarBinder
        extends
        UiBinder<Widget, PercentageBar> {
    }

    private static PercentageBarBinder uiBinder = GWT.create( PercentageBarBinder.class );

    private String                     colour   = "GREEN";
    private int                        width    = 100;
    private float                      percent  = 100;

    public PercentageBar() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public PercentageBar(String colour,
                         int width,
                         float percent) {
        initWidget( uiBinder.createAndBindUi( this ) );
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
        //        int pixels = (int) (width * (percent / 100));
        //        String p = Float.toString( percent );
        //
        //        setHTML( TEMPLATE.mainHtml( width,
        //                                    TEMPLATE.barHtml( pixels,
        //                                                      colour,
        //                                                      p )
        //                                    TEMPLATE.textHtml( width,
        //                                                       p ) ) 
        //        ) );
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

    public void setWidth(String width) {
        setWidth( Integer.parseInt( width ) );
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
