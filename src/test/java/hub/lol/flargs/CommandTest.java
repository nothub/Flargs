package hub.lol.flargs;

import hub.lol.flargs.exceptions.BuildException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void example() {
        Command command = new Command.Builder()
            .withFlag(Flag.newBoolFlag("-x", "--bla"))
            .withFlag(Flag.newFloatFlag("-f", "--fl", "--floater"))
            .withFunc(cmd -> System.out.println("bla"))
            .withCmd("foo", new Command.Builder()
                .withFlag(Flag.newBoolFlag("-i"))
                .withFlag(new Flag.Builder<Integer>()
                    .withLabel("-y")
                    .withConverter(s -> Integer.parseInt(s) * 2)
                    .withValidator(i -> i % 2 == 0)
                    .build())
                .withFunc(cmd -> System.out.println("innerbla"))
                .build())
            .build();
    }

    @Test
    void noCmdFunc() {
        Assertions.assertThrows(BuildException.class, () -> new Command.Builder()
            .withCmd("sub", new Command.Builder().build()));
    }

    @Test
    void duplicateFlagLabels() {
        var builder = new Command.Builder()
            .withCmd("sub", new Command.Builder().withFunc(cmd -> {}).build())
            .withFlag(Flag.newIntFlag("-x", "--yz"));
        // this should throw because the label "--yz" is already registered.
        Assertions.assertThrows(BuildException.class, () -> builder
            .withFlag(Flag.newStrFlag("-a", "--yz")));
    }

}
