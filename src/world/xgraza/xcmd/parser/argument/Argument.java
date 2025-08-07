package world.xgraza.xcmd.parser.argument;

import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;

public abstract class Argument<T>
{
    private final String name;
    private final Class<T> type;
    private final T defaultValue;
    private boolean required = true, greedy;

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
    }

    public abstract T parse(final String raw, final String type) throws ArgumentParseException;

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

    public boolean isGreedy()
    {
        return greedy;
    }

    public Argument<T> setGreedy(boolean greedy)
    {
        if (!getTokenType().equals("string"))
        {
            throw new RuntimeException("Only type 'string' can be greedy");
        }
        if (isFlag())
        {
            throw new RuntimeException("Arguments associated with a flag cannot be greedy");
        }
        this.greedy = greedy;
        return this;
    }
}
