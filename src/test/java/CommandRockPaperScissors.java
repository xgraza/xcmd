import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.internal.ArgumentEnum;
import world.xgraza.xcmd.parser.flag.Flag;

import java.util.LinkedList;
import java.util.List;

final class CommandRockPaperScissors implements ICommandExecutor
{
    @Override
    public CommandResult dispatch(final CommandContext context)
    {
        final Move playerMove = context.getArgument("move");
        final Move botMove = context.getFlag("cheat", pickRandomMove());
        if (botMove.equals(playerMove))
        {
            return context.ok("We both picked " + botMove.name() + "! We tied");
        }
        return context.ok("I picked " +
                botMove +
                ", " +
                (botMove.getOutcomes()[playerMove.ordinal()] ? "I win!" : "you win!"));
    }

    private Move pickRandomMove()
    {
        return Move.values()[(int) Math.floor(Math.random() * 3)];
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        final List<Argument<?>> arguments = new LinkedList<>();
        arguments.add(new ArgumentEnum<>(Move.class, "move"));
        return arguments;
    }

    @Override
    public List<Flag<?>> getFlags()
    {
        final List<Flag<?>> flags = new LinkedList<>();
        flags.add(new Flag<>("cheat", new ArgumentEnum<>(Move.class)));
        return flags;
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "rps", "rockpaperscissors" };
    }

    private enum Move
    {
        ROCK(false, false, true), // 0
        PAPER(true, false, false), // 1
        SCISSORS(false, true, false); // 2

        private final boolean[] outcomes;

        Move(final boolean... outcomes)
        {
            this.outcomes = outcomes;
        }

        public boolean[] getOutcomes()
        {
            return outcomes;
        }
    }
}
