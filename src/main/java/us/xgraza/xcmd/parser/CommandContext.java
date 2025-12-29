/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.parser;

import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.DispatchCode;
import us.xgraza.xcmd.executor.ICommandExecutor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 08/06/2025
 * <p>
 * Provides a "context" to the dispatched command
 */
public final class CommandContext
{
    private final ICommandExecutor executor;
    private final List<String> rawArguments;

    private final Map<String, Object> resolvedArgumentMap = new LinkedHashMap<>();
    private final Map<String, Object> resolvedFlagMap = new LinkedHashMap<>();

    CommandContext(final ICommandExecutor executor, final List<String> rawArguments)
    {
        this.executor = executor;
        this.rawArguments = rawArguments;
    }

    /**
     * Resolves the dispatched command successfully
     *
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult ok(final String message)
    {
        return resolve(DispatchCode.OK, message);
    }

    /**
     * Resolves the dispatched command with a fail
     *
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult fail(final String message)
    {
        return resolve(DispatchCode.FAIL, message);
    }

    /**
     * Resolves the dispatched command
     *
     * @param code    the code
     * @param message the result (nullable)
     * @return the {@link CommandResult}
     */
    public CommandResult resolve(final int code, final String message)
    {
        return new CommandResult(executor, code, message);
    }

    public <T> T getArgument(final String name)
    {
        return getArgument(name, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(final String name, final T defaultValue)
    {
        return (T) resolvedArgumentMap.getOrDefault(name, defaultValue);
    }

    public <T> T getFlag(final String name)
    {
        return getFlag(name, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFlag(final String name, final T defaultValue)
    {
        return (T) resolvedFlagMap.getOrDefault(name, defaultValue);
    }

    /**
     * Checks if an argument has been resolved
     *
     * @param name the argument name
     * @return if the argument has been resolved
     */
    public boolean hasArgument(final String name)
    {
        return resolvedArgumentMap.containsKey(name);
    }

    /**
     * Checks if a flag has been resolved
     *
     * @param name the flag name
     * @return if the flag has been resolved
     */
    public boolean hasFlag(final String name)
    {
        return resolvedFlagMap.containsKey(name);
    }

    public ICommandExecutor getExecutor()
    {
        return executor;
    }

    public List<String> getRawInput()
    {
        return rawArguments;
    }

    public Map<String, Object> getResolvedArgumentMap()
    {
        return resolvedArgumentMap;
    }

    public Map<String, Object> getResolvedFlagMap()
    {
        return resolvedFlagMap;
    }
}
