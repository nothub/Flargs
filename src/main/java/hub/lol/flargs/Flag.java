package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;
import hub.lol.flargs.exceptions.FormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Flag<T> {
    private final Set<String> labels;
    private final Function<String, T> converter;
    private final Predicate<T> validator;

    private T dfault = null;
    private T value = null;
    private boolean optional = false;
    private boolean required = false;
    private boolean repeating = false;
    private Set<String> exclusives = new HashSet<>();

    private Flag(Set<String> labels, Function<String, T> converter, Predicate<T> validator) {
        this.labels = Collections.unmodifiableSet(labels);
        this.converter = converter;
        this.validator = validator;
    }

    public static Flag<Boolean> newBoolFlag(String... labels) {
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

        return new Flag<>(Arrays.stream(labels).collect(Collectors.toUnmodifiableSet()), converter, v -> true);
    }

    public static Flag<Integer> newIntFlag(String... labels) {
        return new Flag<>(Arrays.stream(labels).collect(Collectors.toUnmodifiableSet()), s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<Float> newFloatFlag(String... labels) {
        return new Flag<>(Arrays.stream(labels).collect(Collectors.toUnmodifiableSet()), s -> {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException ex) {
                throw new FormatException(ex);
            }
        }, v -> true);
    }

    public static Flag<String> newStrFlag(String... labels) {
        return new Flag<>(Arrays.stream(labels).collect(Collectors.toUnmodifiableSet()), s -> Function.<String>identity().apply(s), s -> !s.isEmpty());
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
    void value(@NotNull String value) {
        this.value = converter.apply(value);
        validate(this.value);
    }

    /**
     * @throws FormatException
     */
    private void validate(@Nullable T value) {
        if (value == null) {
            throw new FormatException("Flag " + this.labels + " has null value.");
        }
        if (!validator.test(value)) {
            throw new FormatException("Flag " + this.labels + " has invalid value.");
        }
    }

    public boolean exclusive(String label) {
        return this.exclusives.contains(label);
    }

    public static final class Builder<T> {
        private final Set<String> labels = new HashSet<>();
        private Function<String, T> converter;
        private Predicate<T> validator;
        private T dfault;
        private boolean optional;
        private boolean required;
        private boolean repeating;
        private final Set<String> exclusives = new HashSet<>();

        /**
         * @throws BuildException
         */
        public Builder<T> withLabel(@NotNull String label) {
            if (label.isEmpty()) throw new BuildException("Flag label is empty.");
            if (!label.startsWith("-")) throw new BuildException("Flag label must begin with dash.");
            if (label.contains(" ")) throw new BuildException("Flag label can not contain space.");
            if (label.contains("=")) throw new BuildException("Flag label can not contain equal sign.");
            this.labels.add(label);
            return this;
        }

        public Builder<T> withConverter(@NotNull Function<String, T> converter) {
            this.converter = converter;
            return this;
        }

        public Builder<T> withValidator(@NotNull Predicate<T> validator) {
            this.validator = validator;
            return this;
        }

        public Builder<T> withDefault(@Nullable T dfault) {
            this.dfault = dfault;
            return this;
        }

        public Builder<T> withExclusive(String... labels) {
            for (String label : labels) {
                this.exclusives.add(label);
            }
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
            flag.exclusives = this.exclusives;
            flag.optional = this.optional;
            flag.required = this.required;
            flag.repeating = this.repeating;
            flag.validate(flag.value);
            return flag;
        }
    }
}
