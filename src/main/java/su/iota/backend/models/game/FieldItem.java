package su.iota.backend.models.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class FieldItem {

    public static final UUID EPHEMERAL_UUID = new UUID(0L, 0L);

    protected boolean concrete = true;

    @Nullable
    protected Color color;

    @Nullable
    protected Shape shape;

    @Nullable
    protected Number number;

    @NotNull
    protected UUID uuid = EPHEMERAL_UUID;

    @Nullable
    protected Collection<FieldItem> substitutes;

    public boolean isEphemeral() {
        return uuid.equals(EPHEMERAL_UUID);
    }

    public void materialize() {
        if (!isEphemeral()) {
            throw new IllegalStateException();
        }
        uuid = UUID.randomUUID();
    }

    public boolean isConcrete() {
        return concrete;
    }

    @Nullable
    public Collection<FieldItem> getSubstitutes() {
        if (concrete) {
            throw new IllegalStateException();
        }
        return substitutes;
    }

    public void setSubstitutes(@NotNull Collection<FieldItem> substitutes) {
        if (concrete) {
            throw new IllegalStateException();
        }
        this.substitutes = substitutes;
    }

    public void morphToConcrete(@NotNull FieldItem concreteFieldItem) {
        if (isEphemeral()) {
            throw new IllegalStateException();
        }
        if (!concreteFieldItem.concrete || substitutes == null || !substitutes.contains(concreteFieldItem)) {
            throw new IllegalArgumentException();
        }
        this.substitutes = null;
        color = concreteFieldItem.color;
        shape = concreteFieldItem.shape;
        number = concreteFieldItem.number;
        uuid = concreteFieldItem.uuid;
    }

    @NotNull
    public Color getColor() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        if (color == null) {
            throw new AssertionError();
        }
        return color;
    }

    public void setColor(@NotNull Color color) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.color = color;
    }

    @NotNull
    public Shape getShape() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        if (shape == null) {
            throw new AssertionError();
        }
        return shape;
    }

    public void setShape(@NotNull Shape shape) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.shape = shape;
    }

    @NotNull
    public Number getNumber() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        if (number == null) {
            throw new AssertionError();
        }
        return number;
    }

    public void setNumber(@NotNull Number number) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.number = number;
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final FieldItem fieldItem = (FieldItem) obj;

        if (concrete != fieldItem.concrete) return false;
        if (color != null ? color != fieldItem.color : fieldItem.color != null) return false;
        if (shape != null ? shape != fieldItem.shape : fieldItem.shape != null) return false;
        //noinspection SimplifiableIfStatement
        if (number != null ? number != fieldItem.number : fieldItem.number != null) return false;
        return uuid.equals(fieldItem.uuid);
    }

    @Override
    public int hashCode() {
        int result = (concrete ? 1 : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (shape != null ? shape.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + uuid.hashCode();
        return result;
    }

    @SuppressWarnings("EnumeratedConstantNamingConvention")
    public enum Color {
        RED, GREEN, BLUE, YELLOW
    }

    public enum Shape {SQUARE, XCROSS, TRIANGLE, CIRCLE}

    @SuppressWarnings("EnumeratedConstantNamingConvention")
    public enum Number {
        ONE, TWO, THREE, FOUR
    }

}
