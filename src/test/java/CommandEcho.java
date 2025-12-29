import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.internal.ArgumentString;

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
        arguments.add(ArgumentString.greedy("input"));
        return arguments;
    }

    @Override
    public String[] getAliases()
    {
        return new String[]{ "echo", "e" };
    }
}
