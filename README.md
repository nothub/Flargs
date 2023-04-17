# Flargs

## Non-Features

The following features of the [docopt](https://docopt.org/) syntax can currently not be expressed by Flargs:

#### Exclusivity

e.g.

```
my_program (--either-option | <or-argument>)
my_program go [--up | --down | --left | --right]
```

#### Grouping

e.g.

```
my_program [(<one-argument> <another-argument>)]
my_program (run [--fast] | jump [--high])
```
