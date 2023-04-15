package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;

import java.util.*;
import java.util.function.Consumer;

public class Command extends Element implements Runnable {
    private final Map<String, Command> cmds;
    private final Set<Flag<?>> flags;
    private final Consumer<Command> func;

    private Command(Map<String, Command> cmds, Set<Flag<?>> flags, Consumer<Command> func) {
        this.cmds = cmds;
        this.flags = flags;
        this.func = func;
    }

    public Map<String, Command> getCmds() {
        return cmds;
    }

    public Optional<Flag<?>> flag(String label) {
        return flags.stream().filter(f -> f.labels().contains(label)).findFirst();
    }

    public Set<Flag<?>> flags() {
        return Collections.unmodifiableSet(flags);
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
        private boolean optional;
        private boolean required;
        private boolean repeating;

        public Builder withCmd(String name, Command cmd) {
            this.cmds.put(name, cmd);
            return this;
        }

        /**
         * @throws BuildException
         */
        public Builder withFlag(Flag<?> flag) {
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

        public Builder withFunc(Consumer<Command> func) {
            this.func = func;
            return this;
        }

        public Builder withExclusive(Element other) {
            this.exclusives.add(other);
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
            Command command = new Command(cmds, flags, func);
            command.exclusives = this.exclusives;
            command.optional = this.optional;
            command.required = this.required;
            command.repeating = this.repeating;
            return command;
        }
    }
}
