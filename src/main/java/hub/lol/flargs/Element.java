package hub.lol.flargs;

import java.util.HashSet;
import java.util.Set;

public abstract class Element {
    Set<Element> exclusives = new HashSet<>();
    boolean optional = false;
    boolean required = false;
    boolean repeating = false;
}
