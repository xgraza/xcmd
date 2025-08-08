package test;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.DispatchCode;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.registry.CommandRegistry;

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

    @Override
    public void handleExecutorDebug(CommandContext context, double parseTimeMS, double dispatchTimeMS)
    {
        System.out.println("\033[3;8mExecuted in " + dispatchTimeMS + "ms (parsed in " + parseTimeMS + "ms)");
    }
}
