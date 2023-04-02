package hub.lol.flargs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Command extends Element implements Runnable {
    final String name;
    Set<Flag> flags = new HashSet<>();
    List<String> args = new ArrayList<>();
    Consumer<Command> func;

    public Command(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Set<Flag> flags() {
        return flags;
    }

    public List<String> args() {
        return args;
    }

    public void addFlag(Flag flag) {
        this.flags.add(flag);
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }

    @Override
    public void run() {
        func.accept(this);
    }
}
