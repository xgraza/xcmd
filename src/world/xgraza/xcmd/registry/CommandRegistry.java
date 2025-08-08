package world.xgraza.xcmd.registry;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.CommandParser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 08/08/2025
 * An abstract internal implementation of {@link ICommandRegistry}
 */
public abstract class CommandRegistry implements ICommandRegistry
{
    private final Map<String, ICommandExecutor> commandExecutorAliasMap = new LinkedHashMap<>();
    private final List<ICommandExecutor> commandExecutors = new LinkedList<>();
    private final CommandParser commandParser = new CommandParser(this);
    private final String commandPrefix;

    public CommandRegistry(final String commandPrefix)
    {
        this.commandPrefix = commandPrefix;
    }

    /**
     * Register an {@link ICommandExecutor}
     *
     * @param executor the executor
     */
    public void register(final ICommandExecutor executor)
    {
        commandExecutors.add(executor);
        for (final String alias : executor.getAliases())
        {
            if (commandExecutorAliasMap.containsKey(alias))
            {
                throw new RuntimeException("Conflicting alias keys!\n'"
                        + alias + "' is already assigned to "
                        + commandExecutorAliasMap.get(alias));
            }
            commandExecutorAliasMap.put(alias, executor);
        }
    }

    /**
     * Process command input
     *
     * @param input raw string containing the command name, prefix, and arguments
     */
    public void process(final String input)
    {
        long start, end;

        CommandContext context;
        try
        {
            start = System.nanoTime();
            context = commandParser.parse(input);
            end = System.nanoTime();
        }
        catch (Exception e)
        {
            handleDispatchException(e);
            return;
        }
        final double parseTimeMS = (end - start) / 1E+6;
        if (context == null || context.getExecutor() == null)
        {
            return;
        }
        try
        {
            start = System.nanoTime();
            final CommandResult result = context.getExecutor().dispatch(context);
            end = System.nanoTime();
            handleDispatchResult(result, context);
            handleExecutorDebug(context, parseTimeMS, (end - start) / 1E+6);
        }
        catch (final Exception e)
        {
            handleDispatchException(e);
        }
    }

    public abstract void handleDispatchResult(final CommandResult commandResult,
                                              final CommandContext context);

    public abstract void handleDispatchException(final Exception exception);

    public void handleExecutorDebug(final CommandContext context,
                                    final double parseTimeMS,
                                    final double dispatchTimeMS)
    {
        // no-op
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ICommandExecutor> T getExecutor(final String alias)
    {
        return (T) commandExecutorAliasMap.get(alias);
    }

    public Map<String, ICommandExecutor> getCommandExecutorAliasMap()
    {
        return commandExecutorAliasMap;
    }

    public List<ICommandExecutor> getExecutors()
    {
        return commandExecutors;
    }

    @Override
    public String getCommandPrefix()
    {
        return commandPrefix;
    }
}
