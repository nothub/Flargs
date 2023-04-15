package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class CommandTest {

    @Test
    void noCmdFunc() {
        Assertions.assertThrows(BuildException.class, () -> new Command.Builder()
            .withCmd("sub", new Command.Builder().build()));
    }

    @Test
    void duplicateFlagLabels() {
        var builder = new Command.Builder()
            .withCmd("sub", new Command.Builder().withFunc(cmd -> {}).build())
            .withFlag(Flag.newIntFlag(Set.of("-x", "--yz")));
        // this should throw because the label "--yz" is already registered.
        Assertions.assertThrows(BuildException.class, () -> builder
            .withFlag(Flag.newStrFlag(Set.of("-a", "--yz"))));
    }

}
