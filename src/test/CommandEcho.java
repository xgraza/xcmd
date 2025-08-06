package test;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.ArgumentString;

import java.util.LinkedList;
import java.util.List;

final class CommandEcho implements ICommandExecutor
{
    @Override
    public CommandResult dispatch(final CommandContext context)
    {
        return context.ok(context.getArgument("input"));
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        final List<Argument<?>> arguments = new LinkedList<>();
        arguments.add(ArgumentString.string("input", true));
        return arguments;
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "echo", "e" };
    }
}
