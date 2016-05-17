package su.iota.backend.misc;

import co.paralleluniverse.fibers.SuspendExecution;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("InterfaceNeverImplemented")
public final class SuspendableUtils {

    private SuspendableUtils() {
    }

    @FunctionalInterface
    public interface SuspendableFunction<T, R> {

        R apply(T t) throws SuspendExecution;

    }

    @FunctionalInterface
    public interface SuspendableConsumer<T> {

        void accept(T t) throws SuspendExecution;

    }

    public static <T, R> Function<T, R> rethrowFunction(SuspendableFunction<T, R> function) {
        return param -> {
            try {
                return function.apply(param);
            } catch (SuspendExecution ex) {
                rethrow(ex);
                return null;
            }
        };
    }

    public static <T> Consumer<T> rethrowConsumer(SuspendableConsumer<T> consumer) {
        return param -> {
            try {
                consumer.accept(param);
            } catch (SuspendExecution ex) {
                rethrow(ex);
            }
        };
    }

    private static <E extends Exception> void rethrow(Exception exception) throws E {
        //noinspection unchecked
        throw (E) exception;
    }

}
