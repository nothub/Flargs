package hub.lol.flargs;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Command extends Element implements Runnable {
    Set<Flag> flags = new HashSet<>();
    int minArgs;
    int maxArgs;
    Consumer<Command> func;

    @Override
    public void run() {
        func.accept(this);
    }
}
