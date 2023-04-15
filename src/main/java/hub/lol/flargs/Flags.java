package hub.lol.flargs;

public class Flags {

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

    public static Command parse(String[] args, Command root) {

        // TODO: parse flag values and command args

        // TODO: validate flag values
        // TODO: verify required flags are set

        // TODO: verify required num args are present

        for (String arg : args) {

        }

        return root;
    }
}
