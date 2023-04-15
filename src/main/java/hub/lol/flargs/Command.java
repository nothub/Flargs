package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Command extends Element implements Runnable {
    private final Map<String, Command> cmds;
    private final Map<String, Flag<?>> flags = new HashMap<>();
    private final Consumer<Command> func;
    private final List<String> args = new ArrayList<>();
    final int minArgs;
    final int maxArgs;

    private Command(Map<String, Command> cmds, Set<Flag<?>> flags, Consumer<Command> func, int minArgs, int maxArgs) {
        this.cmds = cmds;
        for (Flag<?> flag : flags) {
            for (String label : flag.labels()) {
                this.flags.put(label, flag);
            }
        }
        this.func = func;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    public @NotNull Map<String, Command> cmds() {
        return cmds;
    }

    public @Nullable Flag<?> flag(@NotNull String label) {
        return flags.get(label);
    }

    public @NotNull Set<Flag<?>> flags() {
        return flags.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public @NotNull List<String> args() {
        return Collections.unmodifiableList(args);
    }

    void addArgs(String... args) {
        this.args.addAll(Arrays.asList(args));
    }

    @Override
    public void run() {
        func.accept(this);
    }

    public static final class Builder {
        private final Map<String, Command> cmds = new HashMap<>();
        private final Set<Flag<?>> flags = new HashSet<>();
        private final Set<Element> exclusives = new HashSet<>();
        private Consumer<Command> func;
        private int minArgs;
        private int maxArgs = Integer.MAX_VALUE;
        private boolean optional;
        private boolean required;
        private boolean repeating;

        public Builder withCmd(@NotNull String name, @NotNull Command cmd) {
            this.cmds.put(name, cmd);
            return this;
        }

        /**
         * @throws BuildException
         */
        public Builder withFlag(@NotNull Flag<?> flag) {
            for (Flag<?> f : this.flags) {
                for (String l : f.labels()) {
                    if (flag.labels().contains(l)) {
                        throw new BuildException("Duplicate flag label \"" + l + "\".");
                    }
                }
            }
            this.flags.add(flag);
            return this;
        }

        public Builder withFunc(@NotNull Consumer<Command> func) {
            this.func = func;
            return this;
        }

        public Builder withExclusive(@NotNull Element other) {
            this.exclusives.add(other);
            return this;
        }

        public Builder minArgs(int n) {
            this.minArgs = n;
            return this;
        }

        public Builder maxArgs(int n) {
            this.maxArgs = n;
            return this;
        }

        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder repeating(boolean repeating) {
            this.repeating = repeating;
            return this;
        }

        /**
         * @throws BuildException
         */
        public Command build() {
            if (func == null) throw new BuildException("Command without function.");
            Command command = new Command(cmds, flags, func, minArgs, maxArgs);
            command.exclusives = this.exclusives;
            if (minArgs > maxArgs) throw new BuildException("Command min args > max args.");
            command.optional = this.optional;
            command.required = this.required;
            command.repeating = this.repeating;
            return command;
        }
    }
}
