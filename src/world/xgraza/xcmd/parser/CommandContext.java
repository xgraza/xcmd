package world.xgraza.xcmd.parser;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.DispatchCode;
import world.xgraza.xcmd.executor.ICommandExecutor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 08/06/2025
 *
 * Provides a "context" to the dispatched command
 */
public final class CommandContext
{
    private final ICommandExecutor executor;
    private final List<String> rawArguments;

    private final Map<String, Object> resolvedArgumentMap = new LinkedHashMap<>();

    public CommandContext(final ICommandExecutor executor, final List<String> rawArguments)
    {
        this.executor = executor;
        this.rawArguments = rawArguments;
    }

    /**
     * Resolves the dispatched command successfully
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult ok(final String message)
    {
        return resolve(DispatchCode.OK, message);
    }

    /**
     * Resolves the dispatched command with a fail
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult fail(final String message)
    {
        return resolve(DispatchCode.FAIL, message);
    }

    /**
     * Resolves the dispatched command
     * @param code the code
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult resolve(final int code, final String message)
    {
        return new CommandResult(executor, code, message);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(final String name)
    {
        return (T) resolvedArgumentMap.get(name);
    }

    /**
     * Checks if an argument has been resolved
     * @param name the argument name
     * @return if the argument has been resolved
     */
    public boolean isArgumentResolved(final String name)
    {
        return resolvedArgumentMap.containsKey(name);
    }

    public ICommandExecutor getExecutor()
    {
        return executor;
    }

    public List<String> getRawInput()
    {
        return rawArguments;
    }

    public Map<String, Object> getResolvedArguments()
    {
        return resolvedArgumentMap;
    }
}
