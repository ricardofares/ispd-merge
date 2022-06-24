package ispd.gui.iconico;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;

public final class Ruler extends JComponent {

    /**
     * It represents the font used to mark the labels on the ruler.
     */
    private static final Font RULER_FONT =
            new Font("SansSerif", Font.PLAIN, 10);

    /**
     * It represents the background color.
     */
    private static final Color RULER_BACKGROUND_COLOR =
            new Color(240, 240, 240);

    /**
     * It represents the tick length. The term <em>tick</em> is coined
     * to represent integral unit numbers in the rule.
     */
    private static final int RULER_TICK_LENGTH = 10;

    /**
     * It represents the pre-tick length. The term <em>pre-tick</em>
     * is coined to represent the fractional numbers in the rule.
     */
    private static final int RULER_PRE_TICK_LENGTH = 7;

    /**
     * It represents the ruler's size.
     */
    private static final int SIZE = 35;

    /**
     * It stores the ruler's orientation, this is essential because
     * the orientation in which the ruler is drawn on the grid depends
     * on the value specified by this variable.
     */
    private final RulerOrientation orientation;

    /**
     * It stores the ruler's unit, this is essential because the unit
     * is used to draw the <em>ticks</em> and <em>labels</em> relative
     * to the unit specified in this variable.
     */
    private RulerUnit unit;

    /**
     * Constructor of {@link Ruler} which specifies the ruler
     * orientation and the ruler unit.
     *
     * @param orientation the orientation
     * @param unit        the unit
     */
    public Ruler(final RulerOrientation orientation,
                 final RulerUnit unit) {
        this.orientation = orientation;
        this.unit = unit;
    }

    /**
     * Constructor of {@link Ruler} which specifies the ruler
     * orientation. Further, the ruler is set to centimeters as default.
     *
     * @param orientation the orientation
     * @see #Ruler(RulerOrientation, RulerUnit) for specify the ruler
     * unit
     */
    public Ruler(final RulerOrientation orientation) {
        this(orientation, RulerUnit.CENTIMETERS);
    }

    protected void paintComponent(final Graphics g) {
        final var units = this.unit.getUnit();
        final var increment = this.unit.getIncrement();

        final Rectangle drawHere = g.getClipBounds();

        // Fill clipping area with dirty brown/orange.
        g.setColor(RULER_BACKGROUND_COLOR);
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Set the color and font for marking the ruler's labels.
        g.setFont(RULER_FONT);
        g.setColor(Color.BLACK);

        // Some vars we need.
        int end = 0;
        int start = 0;

        final var isHorizontalOrientated
                = this.orientation == RulerOrientation.HORIZONTAL;

        // Use clipping bounds to calculate first and last tick locations.
        if (isHorizontalOrientated) {
            start = (drawHere.x / increment) * increment;
            end = (((drawHere.x + drawHere.width) / increment) + 1)
                    * increment;
        } else {
            start = (drawHere.y / increment) * increment;
            end = (((drawHere.y + drawHere.height) / increment) + 1)
                    * increment;
        }

        // Make a special case of 0 to display the number
        // within the rule and draw a units label.
        if (start == 0) {
            final var text = "0 " + this.unit.getSymbol();

            if (isHorizontalOrientated) {
                g.drawLine(0, SIZE - 1,
                        0, SIZE - RULER_TICK_LENGTH - 1);
                g.drawString(text, 2, 21);
            } else {
                g.drawLine(SIZE - 1, 0,
                        SIZE - RULER_TICK_LENGTH - 1, 0);
                g.drawString(text, 9, 10);
            }
            start = increment;
        }

        // ticks and labels
        for (int i = start; i < end; i += increment) {
            final String text;
            final int tickLength;

            if (i % units == 0) {
                tickLength = RULER_TICK_LENGTH;
                text = Integer.toString(i / units);
            } else {
                tickLength = RULER_PRE_TICK_LENGTH;
                text = null;
            }

            if (isHorizontalOrientated) {
                g.drawLine(i, SIZE - 1, i, SIZE - tickLength - 1);
                if (text != null)
                    g.drawString(text, i - 3, 21);
            } else {
                g.drawLine(SIZE - 1, i, SIZE - tickLength - 1, i);
                if (text != null)
                    g.drawString(text, 9, i + 3);
            }
        }
    }

    /**
     * It updates the ruler unit to the specified unit.
     * <p>
     * Further, the specified unit is supposed to be <em>non-null</em>.
     * Therefore, unexpected behavior may arise if this precondition
     * is not followed.
     *
     * @param unit the unit to be updated to
     */
    public void updateUnitTo(final RulerUnit unit) {
        this.unit = unit;
        this.repaint();
    }

    /**
     * It sets the ruler's preferred height.
     *
     * @param preferredHeight the preferred height
     */
    public void setPreferredHeight(final int preferredHeight) {
        setPreferredSize(new Dimension(SIZE, preferredHeight));
    }

    /**
     * It sets the ruler's preferred width.
     *
     * @param preferredWidth the preferred width
     */
    public void setPreferredWidth(int preferredWidth) {
        setPreferredSize(new Dimension(preferredWidth, SIZE));
    }

    /**
     * {@link RulerOrientation} is an enumeration that stores the
     * available ruler orientation.
     */
    public enum RulerOrientation {
        HORIZONTAL,
        VERTICAL
    }

    /**
     * {@link RulerUnit} is an enumeration that stores the available
     * ruler units.
     */
    public enum RulerUnit {
        CENTIMETERS("cm") {
            /**
             * Returns the unit in centimeters unit.
             * @return the unit in centimeters unit
             */
            @Override
            public int getUnit() {
                /* 1 in = 2.54 cm */
                return (int) ((double) INCH / 2.54D);
            }

            /**
             * Returns the increment in centimeters unit.
             * @return the increment in centimeters unit
             */
            @Override
            public int getIncrement() {
                return this.getUnit();
            }
        },
        INCHES("in") {
            /**
             * Returns the unit in inches unit.
             * @return the unit in inches unit
             */
            @Override
            public int getUnit() {
                return INCH;
            }

            /**
             * Returns the increment in inches unit.
             * @return the increment in inches unit
             */
            @Override
            public int getIncrement() {
                return this.getUnit() >> 1;
            }
        };

        /**
         * It represents the screen resolution in dots-per-inch.
         */
        private static final int INCH = Toolkit.getDefaultToolkit()
                .getScreenResolution();

        /**
         * It stores the unit symbol. Every symbol must be
         * in English and singular form.
         */
        private final String symbol;

        /**
         * Constructor of {@link RulerUnit} which specifies the unit
         * symbol.
         *
         * @param symbol the symbol
         */
        /* package-private */ RulerUnit(final String symbol) {
            this.symbol = symbol;
        }

        /**
         * Returns the ruler unit.
         *
         * @return the ruler unit
         */
        public abstract int getUnit();

        /**
         * Returns the ruler increment.
         *
         * @return the ruler increment
         */
        public abstract int getIncrement();

        /**
         * It returns the next unit described after this one.
         * Further, if it does not have any unit described
         * after this one, then the <em>topmost (or the first)</em>
         * unit is returned, instead.
         * <p>
         * An example of such method operation is given below,
         * first suppose the units is described as
         * <ul>
         *     <li>CENTIMETERS</li>
         *     <li>INCHES</li>
         * </ul>
         * and suppose that this unit is <em>centimeters</em>.
         * Therefore, the next described unit after this one
         * is <em>inches</em>.
         *
         * @return the next unit described after this one
         */
        public RulerUnit nextUnit() {
            final var values = RulerUnit.values();
            return values[(this.ordinal() + 1) % values.length];
        }

        /**
         * Returns the unit symbol.
         *
         * @return the unit symbol
         */
        public String getSymbol() {
            return this.symbol;
        }
    }
}
