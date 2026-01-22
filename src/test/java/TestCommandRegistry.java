/*
 * Copyright (c) xgraza 2026
 */

import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.DispatchCode;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.registry.CommandRegistry;

final class TestCommandRegistry extends CommandRegistry
{
    public TestCommandRegistry()
    {
        super(".");
    }

    @Override
    public void handleDispatchResult(final CommandResult commandResult,
                                     final CommandContext context)
    {
        switch (commandResult.getCode())
        {
            case DispatchCode.ERROR:
            {
                break;
            }
            case DispatchCode.OK:
            {
                System.out.println(commandResult.getMessage());
                break;
            }
            case DispatchCode.FAIL:
            {
                break;
            }
            case DispatchCode.INVALID_ARGUMENT:
            {

                break;
            }
        }
    }

    @Override
    public void handleDispatchException(final Exception exception)
    {
        System.out.println("Exception: " + exception.getMessage());
    }
}
