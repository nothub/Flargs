package hub.lol.args;

import java.util.HashSet;
import java.util.Set;

public abstract class Element {
    boolean optional = false;
    boolean required = false;
    boolean repeating = false;
    Set<Element> mutuallyExclusives = new HashSet<>();
}
