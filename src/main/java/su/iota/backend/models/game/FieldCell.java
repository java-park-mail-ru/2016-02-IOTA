package su.iota.backend.models.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FieldCell {

    private boolean concrete = true;
    private Collection<FieldCell> substitutes = new HashSet<>();
    private Integer color;
    private Integer shape;
    private Integer number;

    public FieldCell(int color, int shape, int number) {
        this.color = color;
        this.shape = shape;
        this.number = number;
    }

    public boolean isConcrete() {
        return concrete;
    }

    @Nullable
    public Collection<FieldCell> getSubstitutes() {
        if (concrete) {
            throw new IllegalStateException();
        }
        return substitutes;
    }

    public void setSubstitutes(@NotNull Collection<FieldCell> substitutes) {
        if (concrete) {
            throw new IllegalStateException();
        }
        this.substitutes = substitutes;
    }

    public void morphToConcrete(@NotNull FieldCell concreteFieldCell) {
        if (!concreteFieldCell.concrete || substitutes == null || !substitutes.contains(concreteFieldCell)) {
            throw new IllegalArgumentException();
        }
        this.substitutes = null;
        color = concreteFieldCell.color;
        shape = concreteFieldCell.shape;
        number = concreteFieldCell.number;
    }

    public int getColor() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        return color;
    }

    public void setColor(int color) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.color = color;
    }

    public int getShape() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        return shape;
    }

    public void setShape(int shape) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.shape = shape;
    }

    public int getNumber() {
        if (!concrete) {
            throw new IllegalStateException();
        }
        return number;
    }

    public void setNumber(int number) {
        if (!concrete) {
            throw new IllegalStateException();
        }
        this.number = number;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final FieldCell fieldCell = (FieldCell) obj;

        if (concrete != fieldCell.concrete) {
            return false;
        }
        if (substitutes != null ? !substitutes.equals(fieldCell.substitutes) : fieldCell.substitutes != null) {
            return false;
        }
        if (color != null ? !color.equals(fieldCell.color) : fieldCell.color != null) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (shape != null ? !shape.equals(fieldCell.shape) : fieldCell.shape != null) {
            return false;
        }
        return number != null ? number.equals(fieldCell.number) : fieldCell.number == null;

    }

    @Override
    public int hashCode() {
        int result = (concrete ? 1 : 0);
        result = 31 * result + (substitutes != null ? substitutes.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (shape != null ? shape.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }
    
}
