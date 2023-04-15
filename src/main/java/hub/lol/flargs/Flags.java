package hub.lol.flargs;

import java.util.Arrays;

public class Flags {

    public static Command parse(String[] args, Command root) {
        int cursor = 0;
        Command cmd = root;

        while (cursor < args.length) {
            System.err.println("Parsing argument: " + args[cursor]);

            /*
            how expressive should this be / which of the following docopt features must be possible to express?

            my_program ship new <name>...
            my_program ship <name> move <x> <y> [--speed=<kn>]
            my_program ship shoot <x> <y>
            my_program mine (set|remove) <x> <y> [--moored|--drifting]
            my_program -h | --help
            my_program --version
            my_program command --option <argument>
            my_program [<optional-argument>]
            my_program --another-option=<with-argument>
            my_program (--either-that-option | <or-this-argument>)
            my_program <repeating-argument> <repeating-argument>...
            my_program [command --option <argument>]
            my_program [command] [--option] [<argument>]
            my_program (--either-this <and-that> | <or-this>)
            my_program [(<one-argument> <another-argument>)]
            my_program go (--up | --down | --left | --right)
            my_program go [--up | --down | --left | --right]
            my_program run [--fast]
            my_program jump [--high]
            my_program (run [--fast] | jump [--high])
            my_program open <file>...
            my_program move (<from> <to>)...
            my_program [<file>...]
            my_program [<file>]...
            my_program [<file> [<file> ...]]
            my_program <file>...
            my_program <file> <file>...
            my_program [options] [--] <file>...
            */

            if (args[cursor].startsWith("-")) {

                if (args[cursor].equals("--")) {
                    if (cursor + 1 < args.length) {
                        cmd.addArgs(Arrays.copyOfRange(args, cursor + 1, args.length));
                    }
                    break;
                }

                Flag<?> flag = cmd.flag(args[cursor]);
                if (flag == null) {
                    // TODO: no such flag -> print help and exit
                }

                flag.value();

            }

            // TODO: parse flag values and command args

            // TODO: validate flag values
            // TODO: verify required flags are set

            // TODO: verify required num args are present

            cursor++;
        }

        return root;
    }
}
