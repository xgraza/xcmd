/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.executor;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public final class CommandResult
{
    private final ICommandExecutor executor;
    private final int code;
    private final String message;

    public CommandResult(final ICommandExecutor executor, final int code, final String message)
    {
        this.executor = executor;
        this.code = code;
        this.message = message;
    }

    public ICommandExecutor getExecutor()
    {
        return executor;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }
}
