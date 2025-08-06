package world.xgraza.xcmd.parser.argument;

import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import world.xgraza.xcmd.registry.ICommandRegistry;

public final class ArgumentCommand extends Argument<ICommandExecutor>
{
    private final ICommandRegistry commandRegistry;

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
}
