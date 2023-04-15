package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;
import hub.lol.flargs.exceptions.FormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
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

    private Flag(Set<String> labels, Function<String, T> converter, Predicate<T> validator) {
        this.labels = Collections.unmodifiableSet(labels);
        this.converter = converter;
        this.validator = validator;
    }

    public static Flag<Boolean> newBoolFlag(Set<String> labels) {
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

    public static Flag<Integer> newIntFlag(Set<String> labels) {
        return new Flag<>(labels, s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<Float> newFloatFlag(Set<String> labels) {
        return new Flag<>(labels, s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<String> newStrFlag(Set<String> labels) {
        return new Flag<>(labels, s -> Function.<String>identity().apply(s), s -> !s.isEmpty());
    }

    public Set<String> labels() {
        return labels;
    }

    public @Nullable T dfault() {
        return dfault;
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
    public void dfault(@NotNull T value) {
        this.dfault = value;
        validate(this.dfault);
    }

    /**
     * @throws FormatException
     */
    private void value(@NotNull String value) {
        this.value = converter.apply(value);
        validate(this.value);
    }

    /**
     * @throws FormatException
     */
    private void validate(T value) {
        if (value == null) {
            throw new FormatException("Flag " + this.labels + " has null value.");
        }
        if (!validator.test(value)) {
            throw new FormatException("Flag " + this.labels + " has invalid value.");
        }
    }

    public static final class Builder<T> {
        private final Set<String> labels = new HashSet<>();
        private final Set<Element> exclusives = new HashSet<>();
        private Function<String, T> converter;
        private Predicate<T> validator;
        private T dfault;
        private T value;
        private boolean optional;
        private boolean required;
        private boolean repeating;

        public Builder<T> withLabel(String label) {
            this.labels.add(label);
            return this;
        }

        public Builder<T> withConverter(Function<String, T> converter) {
            this.converter = converter;
            return this;
        }

        public Builder<T> withValidator(Predicate<T> validator) {
            this.validator = validator;
            return this;
        }

        public Builder<T> withDefault(T dfault) {
            this.dfault = dfault;
            return this;
        }

        public Builder<T> withValue(T value) {
            this.value = value;
            return this;
        }

        public Builder<T> withExclusive(Element other) {
            this.exclusives.add(other);
            return this;
        }

        public Builder<T> optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder<T> required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder<T> repeating(boolean repeating) {
            this.repeating = repeating;
            return this;
        }

        /**
         * @throws BuildException
         * @throws FormatException
         */
        public Flag<T> build() {
            if (labels.isEmpty()) throw new BuildException("Flag without labels.");
            if (converter == null) throw new BuildException("Flag without converter.");
            if (validator == null) throw new BuildException("Flag without validator.");
            Flag<T> flag = new Flag<>(labels, converter, validator);
            flag.dfault = this.dfault;
            flag.value = this.value;
            flag.exclusives = this.exclusives;
            flag.optional = this.optional;
            flag.required = this.required;
            flag.repeating = this.repeating;
            flag.validate(flag.value);
            return flag;
        }
    }
}
