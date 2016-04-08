package ru.cdecl.pub.iota.models.game;

public class Card {

    private Color color;
    private Shape shape;
    private int value;

    public Card(Color color, Shape shape, int value) {
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

    public void setColor(Color color) {
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @SuppressWarnings("EnumeratedConstantNamingConvention")
    public enum Color {
        RED, GREEN, BLUE, YELLOW
    }

    public enum Shape {TRIANGLE, SQUARE, CIRCLE, CROSS}

}
