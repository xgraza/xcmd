package world.xgraza.xcmd.executor;

import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.flag.Flag;

import java.util.Collections;
import java.util.List;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public interface ICommandExecutor
{
    /**
     * Dispatches this {@link ICommandExecutor}
     * @param context the {@link CommandContext} provided from the parser
     * @return the result of this {@link ICommandExecutor} dispatch
     */
    CommandResult dispatch(final CommandContext context);

    /**
     * @return an array of Strings of every alias/name this command can be called by
     */
    String[] getAliases();

    /**
     * @return a list of {@link Argument<?>}
     */
    default List<Argument<?>> getArguments()
    {
        return Collections.emptyList();
    }

    /**
     * @return a list of {@link Flag<?>}
     */
    default List<Flag<?>> getFlags()
    {
        return Collections.emptyList();
    }

    /**
     * Suggests possible responses to an {@link Argument<?>} based on the raw input
     * @param argument the {@link Argument<?>}
     * @param input the raw string input
     * @return a list of Strings of possible responses
     */
    default List<String> suggest(final Argument<?> argument, final String input)
    {
        return Collections.emptyList();
    }
}
