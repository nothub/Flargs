package hub.lol.flargs;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Command extends Element implements Runnable {
    public final String name;
    private final Set<Command> subCmds = new HashSet<>();
    private final Consumer<Command> func;

    public Command(String name, Consumer<Command> func) {
        this.name = name;
        this.func = func;
    }

    @Override
    public void run() {
        func.accept(this);
    }
}
