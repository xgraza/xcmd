package world.xgraza.xcmd.parser.argument.internal;

import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import world.xgraza.xcmd.registry.ICommandRegistry;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public final class ArgumentCommand extends Argument<ICommandExecutor>
{
    private final ICommandRegistry commandRegistry;

    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public ArgumentCommand(final ICommandRegistry commandRegistry)
    {
        this(commandRegistry, null);
    }

    public ArgumentCommand(final ICommandRegistry commandRegistry, final String name)
    {
        super(ICommandExecutor.class, name);
        this.commandRegistry = commandRegistry;
    }

    @Override
    public ICommandExecutor parse(final String raw, final String type) throws ArgumentParseException
    {
        final ICommandExecutor executor = commandRegistry.getExecutor(raw.toLowerCase());
        if (executor == null)
        {
            throw new ArgumentParseException("Unrecognized option '" + raw + "' for a command executor");
        }
        return executor;
    }

    /**
     * Helper method to create an {@link ArgumentCommand}
     *
     * @return an {@link ArgumentCommand} with no name (flag only)
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public static ArgumentCommand command(final ICommandRegistry registry)
    {
        return new ArgumentCommand(registry);
    }

    /**
     * Helper method to create an {@link ArgumentCommand}
     *
     * @param name the name for this {@link ArgumentCommand}
     * @return an {@link ArgumentCommand} with the supplied name
     */
    public static ArgumentCommand command(final ICommandRegistry registry, final String name)
    {
        return new ArgumentCommand(registry, name);
    }
}
