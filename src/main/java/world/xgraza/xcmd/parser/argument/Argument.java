package world.xgraza.xcmd.parser.argument;

import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public abstract class Argument<T>
{
    private final String name;
    private final Class<T> type;
    private final T defaultValue;
    private boolean required;

    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public Argument(final Class<T> type)
    {
        this(type, null, null);
    }

    public Argument(final Class<T> type, final String name)
    {
        this(type, name, null);
    }

    public Argument(final Class<T> type, final String name, final T defaultValue)
    {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.required = true;
    }

    /**
     * Parses raw input into an actual object
     *
     * @param raw  the raw argument
     * @param type the type the lexer inferred from the raw argument
     * @return {@link T} or null
     * @throws ArgumentParseException if an invalid raw argument is passed
     */
    public abstract T parse(final String raw, final String type) throws ArgumentParseException;

    /**
     * Validates a value based on a condition
     *
     * @param value the parsed value
     * @throws ArgumentValidateFailureException if validation fails
     * @apiNote Defaults to an empty method, override to add functionality
     */
    public void validate(final T value) throws ArgumentValidateFailureException
    {
        // no-op
    }

    public String getName()
    {
        return name;
    }

    public boolean isFlag()
    {
        return name == null;
    }

    public Class<T> getType()
    {
        return type;
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * The token type
     *
     * @return the token type of this argument
     * @apiNote should only be the primitive type (ex: integer, double, boolean, string)
     */
    public String getTokenType()
    {
        return "string";
    }

    public boolean isRequired()
    {
        return required;
    }

    public Argument<T> setRequired(boolean required)
    {
        this.required = required;
        return this;
    }
}
