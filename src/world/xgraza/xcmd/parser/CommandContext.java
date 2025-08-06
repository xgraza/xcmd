package world.xgraza.xcmd.parser;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.DispatchCode;
import world.xgraza.xcmd.executor.ICommandExecutor;

import java.util.List;
import java.util.Map;

public final class CommandContext
{
    private ICommandExecutor executor;
    private Map<String, Object> resolvedArgumentMap;
    private List<String> rawArguments;

    public CommandResult resolve(final String message)
    {
        return resolve(DispatchCode.OK, message);
    }

    public CommandResult resolve(final int code)
    {
        return resolve(code, null);
    }

    public CommandResult resolve(final int code, final String message)
    {
        return new CommandResult(executor, code, message);
    }

    void setExecutor(final ICommandExecutor executor)
    {
        this.executor = executor;
    }

    public ICommandExecutor getExecutor()
    {
        return executor;
    }

    void setResolvedArgumentMap(final Map<String, Object> resolvedArgumentMap)
    {
        this.resolvedArgumentMap = resolvedArgumentMap;
    }

    public Map<String, Object> getResolvedArgumentMap()
    {
        return resolvedArgumentMap;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(final String name)
    {
        return (T) resolvedArgumentMap.get(name);
    }

    public boolean isArgumentResolved(final String name)
    {
        return resolvedArgumentMap.containsKey(name);
    }

    void setRawInput(final List<String> rawInput)
    {
        this.rawArguments = rawInput;
    }

    public List<String> getRawInput()
    {
        return rawArguments;
    }
}
