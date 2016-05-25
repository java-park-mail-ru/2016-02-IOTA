package su.iota.backend.models.game;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Field {

    private final FieldCell[][] field = new FieldCell[34][34];

    @SuppressWarnings("OverlyComplexMethod")
    public boolean isPlacementCorrect(@NotNull Coordinate placementCoordinate, @NotNull FieldCell placement) {
        final int xCoord = placementCoordinate.getX();
        final int yCoord = placementCoordinate.getY();
        final FieldCell currentCell = field[xCoord][yCoord];
        if (currentCell != null) {
            if (currentCell.isConcrete()) {
                return false;
            }
            final Collection<FieldCell> substitutes = currentCell.getSubstitutes();
            return substitutes != null && substitutes.contains(placement);
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
                    placement
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
                    placement
            );
            if (!isValidForY) {
                return false;
            }
        }

        return true;
    }

    private boolean isPlacementValidForLine(@NotNull Coordinate start, @NotNull Coordinate increment,
                                            @NotNull Predicate<Coordinate> end, @NotNull Predicate<Coordinate> ignore,
                                            @NotNull FieldCell placement) {
        final Set<Integer> colors = new HashSet<>(3);
        final Set<Integer> shapes = new HashSet<>(3);
        final Set<Integer> numbers = new HashSet<>(3);

        for (Coordinate coord = start; end.test(coord); coord = coord.plus(increment)) {
            if (ignore.test(coord)) {
                continue;
            }
            final FieldCell cell = field[coord.getX()][coord.getY()];
            colors.add(cell.getColor());
            shapes.add(cell.getShape());
            numbers.add(cell.getNumber());
        }

        return isLineConditionSatisfied(colors, placement.getColor())
                && isLineConditionSatisfied(shapes, placement.getShape())
                && isLineConditionSatisfied(numbers, placement.getNumber());
    }

    private int getLineLength(@NotNull Coordinate initialCoordinate, @NotNull Coordinate incrementCoordinate) {
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

    private <T> boolean isLineConditionSatisfied(@NotNull Set<T> items, @NotNull T item) {
        return ((items.size() != 1 || !items.contains(item)) && items.contains(item));
    }
    
}
