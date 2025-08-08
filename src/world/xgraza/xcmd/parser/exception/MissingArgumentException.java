package world.xgraza.xcmd.parser.exception;

import world.xgraza.xcmd.parser.argument.Argument;

import java.util.List;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public class MissingArgumentException extends Exception
{
    private final List<Argument<?>> missingArguments;

    public MissingArgumentException(final List<Argument<?>> missingArguments)
    {
        this.missingArguments = missingArguments;
    }

    public List<Argument<?>> getMissingArguments()
    {
        return missingArguments;
    }
}
