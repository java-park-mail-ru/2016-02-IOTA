package ru.cdecl.pub.iota.models.game;

import org.jetbrains.annotations.NotNull;

public class Card implements CardDeckItem {

    private final Color color;
    private final Shape shape;
    private final int value;

    public Card(@NotNull Color color, @NotNull Shape shape, int value) {
        this.color = color;
        this.shape = shape;
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final Card card = (Card) other;
        return value == card.value
                && color == card.color
                && shape == card.shape;
    }

    @Override
    public int hashCode() {
        int result = color.hashCode();
        result = 31 * result + shape.hashCode();
        result = 31 * result + value;
        return result;
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

    public int getValue() {
        return value;
    }

    public enum Color {
        RED("r"), GREEN("g"), BLUE("b"), YELLOW("y");

        @NotNull
        String stringValue;

        Color(@NotNull String stringValue) {
            this.stringValue = stringValue;
        }

        public static Color fromString(@NotNull String str) {
            for (Color color : Color.values()) {
                if (color.stringValue.equals(str)) {
                    return color;
                }
            }
            throw new IllegalArgumentException("str");
        }
    }

    public enum Shape {
        TRIANGLE("t"), SQUARE("s"), CIRCLE("c"), CROSS("x");

        @NotNull
        String stringValue;

        Shape(@NotNull String stringValue) {
            this.stringValue = stringValue;
        }

        public static Shape fromString(@NotNull String str) { // todo: can I use Shape.valueOf(str) ??
            for (Shape shape : Shape.values()) {
                if (shape.stringValue.equals(str)) {
                    return shape;
                }
            }
            throw new IllegalArgumentException("str");
        }
    }

}
