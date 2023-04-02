package hub.lol.flargs;

import java.util.function.Predicate;

public class Flag extends Element {
    final String name;
    final String value;
    final Predicate<String> validator;

    public Flag(String name, String value, Predicate<String> validator) {
        this.name = name;
        this.value = value;
        this.validator = validator;
    }

    public Flag(String name, String value) {
        this(name, value, s -> true);
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public boolean validate() {
        return validator.test(value);
    }
}
