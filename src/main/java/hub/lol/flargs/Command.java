package hub.lol.flargs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Command extends Element implements Runnable {
    Set<Flag> flags = new HashSet<>();
    List<String> args = new ArrayList<>();
    Consumer<Command> func;

    @Override
    public void run() {
        func.accept(this);
    }
}
