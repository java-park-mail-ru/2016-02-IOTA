package su.iota.backend.models.game;

import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class Field {

    public static final Coordinate CENTER_COORDINATE = new Coordinate(0, 0);

    private final FieldItem[][] field = new FieldItem[36][36];

    @SuppressWarnings("OverlyComplexMethod")
    public boolean isPlacementCorrect(@NotNull Coordinate placementCoordinate, @NotNull FieldItem placement) throws SuspendExecution {
        if (placement.isEphemeral()) {
            return false;
        }
        final int xCoord = placementCoordinate.getX();
        final int yCoord = placementCoordinate.getY();
        final FieldItem existingItem = field[xCoord][yCoord];
        if (existingItem != null) {
            if (existingItem.isConcrete()) {
                return false;
            }
        }
        final int leftLineLength = getLineLength(placementCoordinate, new Coordinate(-1, 0));
        final int rightLineLength = getLineLength(placementCoordinate, new Coordinate(1, 0));
        final int topLineLength = getLineLength(placementCoordinate, new Coordinate(0, -1));
        final int bottomLineLength = getLineLength(placementCoordinate, new Coordinate(0, 1));

        final int newLineLengthX = leftLineLength + rightLineLength + 1;
        final int newLineLengthY = topLineLength + bottomLineLength + 1;

        boolean isLengthFit = true;
        //noinspection ConstantConditions
        isLengthFit = isLengthFit && (newLineLengthX <= 4);
        isLengthFit = isLengthFit && (newLineLengthY <= 4);
        if (!isLengthFit) {
            return false;
        }

        if (newLineLengthX >= 3) {
            final boolean isValidForX = isPlacementValidForLine(
                    placementCoordinate.minus(new Coordinate(leftLineLength, 0)),
                    new Coordinate(1, 0),
                    coordinate -> coordinate.getX() < xCoord + rightLineLength + 1,
                    coordinate -> coordinate.equals(placementCoordinate),
                    placement,
                    existingItem
            );
            if (!isValidForX) {
                return false;
            }
        }

        if (newLineLengthY >= 3) {
            final boolean isValidForY = isPlacementValidForLine(
                    placementCoordinate.minus(new Coordinate(0, topLineLength)),
                    new Coordinate(0, 1),
                    coordinate -> coordinate.getY() < yCoord + bottomLineLength + 1,
                    coordinate -> coordinate.equals(placementCoordinate),
                    placement,
                    existingItem
            );
            if (!isValidForY) {
                return false;
            }
        }

        return true;
    }

    private boolean isPlacementValidForLine(@NotNull Coordinate start, @NotNull Coordinate increment,
                                            @NotNull Predicate<Coordinate> end, @NotNull Predicate<Coordinate> ignore,
                                            @NotNull FieldItem placement, @Nullable FieldItem existingItem) throws SuspendExecution {
        final Set<FieldItem.Color> colors = EnumSet.noneOf(FieldItem.Color.class);
        final Set<FieldItem.Shape> shapes = EnumSet.noneOf(FieldItem.Shape.class);
        final Set<FieldItem.Number> numbers = EnumSet.noneOf(FieldItem.Number.class);

        for (Coordinate coord = start; end.test(coord); coord = coord.plus(increment)) {
            if (ignore.test(coord)) {
                continue;
            }
            final FieldItem cell = field[coord.getX()][coord.getY()];
            colors.add(cell.getColor());
            shapes.add(cell.getShape());
            numbers.add(cell.getNumber());
        }

        if (existingItem != null) {
            if (existingItem.isConcrete()) {
                throw new AssertionError();
            }
            Collection<FieldItem> substitutes = existingItem.getSubstitutes();
            if (substitutes == null) {
                substitutes = calculatePossibleSubstitutes(existingItem);
                existingItem.setSubstitutes(substitutes);
            }
            if (!substitutes.contains(placement)) {
                return false;
            }
        }

        return isLineConditionSatisfied(colors, placement.getColor())
                && isLineConditionSatisfied(shapes, placement.getShape())
                && isLineConditionSatisfied(numbers, placement.getNumber());
    }

    private Collection<FieldItem> calculatePossibleSubstitutes(FieldItem cell) throws SuspendExecution {
        return new LinkedList<>(); // todo
    }

    private int getLineLength(@NotNull Coordinate initialCoordinate, @NotNull Coordinate incrementCoordinate) throws SuspendExecution {
        int lineLength = 0;
        Coordinate currentCoordinate = initialCoordinate;
        while (true) {
            final Coordinate nextCoordinate = currentCoordinate.plus(incrementCoordinate);
            if (field[nextCoordinate.getX()][nextCoordinate.getY()] == null) {
                return lineLength;
            }
            lineLength++;
            currentCoordinate = nextCoordinate;
        }
    }

    private <T> boolean isLineConditionSatisfied(@NotNull Set<T> items, @NotNull T item) throws SuspendExecution {
        return ((items.size() != 1 || !items.contains(item)) && items.contains(item));
    }

    public void placeCard(@NotNull Coordinate coordinate, @NotNull FieldItem card) throws SuspendExecution {
        field[coordinate.getX()][coordinate.getY()] = card; // todo
    }

    public FieldItem[][] getRawField() throws SuspendExecution {
        return field;
    }

}
