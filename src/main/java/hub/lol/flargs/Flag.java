package hub.lol.flargs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Flag<T> extends Element {
    private final Set<String> labels;
    private final Function<String, T> converter;
    private final Predicate<T> validator;

    private T dfault = null;
    private T value = null;

    Flag(Set<String> labels, Function<String, T> converter, Predicate<T> validator) {
        this.labels = Collections.unmodifiableSet(labels);
        this.converter = converter;
        this.validator = validator;
    }

    public static Flag<Boolean> boolFlag(Set<String> labels) {
        Map<String, Boolean> lookup = Map.of(
            "true", true,
            "yes", true,
            "y", true,

            "false", false,
            "no", false,
            "n", false
        );

        Function<String, Boolean> converter = s -> {
            if (s.isBlank()) {
                // A bool-flag without value ( e.g. -f ) indicates true.
                return true;
            }

            if (!lookup.containsKey(s.toLowerCase())) {
                throw new FormatException(String.format(
                    "Invalid value for bool-flag. Valid values (case ignored): %s",
                    lookup.keySet().stream().sorted().collect(Collectors.joining(","))
                ));
            }

            return lookup.get(s.toLowerCase());
        };

        return new Flag<>(labels, converter, v -> true);
    }

    public static Flag<Integer> intFlag(Set<String> labels) {
        return new Flag<>(labels, s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<Float> floatFlag(Set<String> labels) {
        return new Flag<>(labels, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<String> strFlag(Set<String> labels) {
        return new Flag<>(labels, s -> Function.<String>identity().apply(s), s -> !s.isEmpty());
    }

    public @Nullable T value() {
        if (value == null) {
            return dfault;
        }
        return value;
    }

    /**
     * @throws FormatException
     */
    public void setDefault(@NotNull T value) {
        this.dfault = value;
        validate(this.dfault);
    }

    /**
     * @throws FormatException
     */
    private void setValue(@NotNull String value) {
        this.value = converter.apply(value);
        validate(this.value);
    }

    private void validate(T value) {
        if (value == null) {
            throw new FormatException("Flag " + this.labels + " has null value.");
        }
        if (!validator.test(value)) {
            throw new FormatException("Flag " + this.labels + " has invalid value.");
        }
    }

    public static class FormatException extends RuntimeException {
        public FormatException(String message) {
            super(message);
        }

        public FormatException(Throwable cause) {
            super(cause);
        }
    }
}
