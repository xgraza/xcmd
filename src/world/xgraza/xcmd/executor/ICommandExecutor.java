package world.xgraza.xcmd.executor;

import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.flag.Flag;

import java.util.Collections;
import java.util.List;

public interface ICommandExecutor
{
    CommandResult dispatch(final CommandContext context);

    String[] getAliases();

    default List<Argument<?>> getArguments()
    {
        return Collections.emptyList();
    }

    default List<Flag> getFlags()
    {
        return Collections.emptyList();
    }

    default List<String> suggest(final Argument<?> argument, final String input)
    {
        return Collections.emptyList();
    }
}
