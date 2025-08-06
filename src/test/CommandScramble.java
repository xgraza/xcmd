package test;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.ArgumentNumber;
import world.xgraza.xcmd.parser.argument.ArgumentString;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class CommandScramble implements ICommandExecutor
{
    @Override
    public CommandResult dispatch(CommandContext context)
    {
        final String input = context.getArgument("input");
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
        arguments.add(ArgumentString.string("input", true));
        return arguments;
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "scramble" };
    }
}
