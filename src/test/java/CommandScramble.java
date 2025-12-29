import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.internal.ArgumentNumber;
import us.xgraza.xcmd.parser.argument.internal.ArgumentString;
import us.xgraza.xcmd.parser.flag.Flag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class CommandScramble implements ICommandExecutor
{
    @Override
    public CommandResult dispatch(CommandContext context)
    {
        final String input = context.getArgument("input");
        if (context.hasFlag("ignore"))
        {
            return context.ok(input);
        }
        final int times = context.getArgument("times");

        final List<String> sentence = new LinkedList<>();
        final String[] split = input.split(" ");
        for (final String word : split)
        {
            final List<String> characters = new LinkedList<>();
            Collections.addAll(characters, word.split(""));
            for (int i = 0; i < times; ++i)
            {
                Collections.shuffle(characters);
            }
            sentence.add(String.join("", characters));
        }

        return context.ok(String.join(" ", sentence));
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        final List<Argument<?>> arguments = new LinkedList<>();
        arguments.add(ArgumentNumber.number(Integer.class, "times", 1, 20));
        arguments.add(ArgumentString.greedy("input"));
        return arguments;
    }

    @Override
    public List<Flag<?>> getFlags()
    {
        final List<Flag<?>> flags = new LinkedList<>();
        flags.add(new Flag<>("ignore"));
        return flags;
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{ "scramble" };
    }
}
