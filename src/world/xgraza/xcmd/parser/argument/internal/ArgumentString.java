package world.xgraza.xcmd.parser.argument.internal;

import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public class ArgumentString extends Argument<String>
{
    private boolean greedy = false;

    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public ArgumentString()
    {
        this(null);
    }

    public ArgumentString(final String name)
    {
        super(String.class, name);
    }

    @Override
    public String parse(final String raw, final String type)
    {
        return raw;
    }

    /**
     * If this {@link ArgumentString} is "greedy"
     * @apiNote "greedy" refers to that this argument will take on the following arguments when parsing to give one big input
     * @return if this {@link ArgumentString} is greedy
     */
    public boolean isGreedy()
    {
        return greedy;
    }

    public ArgumentString setGreedy(boolean greedy)
    {
        if (isFlag())
        {
            throw new RuntimeException("Arguments associated with a flag cannot be greedy");
        }
        this.greedy = greedy;
        return this;
    }

    public static ArgumentString string()
    {
        return new ArgumentString();
    }

    public static ArgumentString string(final String name)
    {
        return string(name, false);
    }

    public static ArgumentString string(final String name, final boolean greedy)
    {
        return (ArgumentString) new ArgumentString(name)
                .setGreedy(greedy);
    }

    public static ArgumentString string(final String name, final String pattern)
    {
        return new ArgumentString(name)
        {
            @Override
            public void validate(final String value) throws ArgumentValidateFailureException
            {
                if (!name.matches(pattern))
                {
                    throw new ArgumentValidateFailureException("Argument does not match " + pattern);
                }
            }
        };
    }

    public static ArgumentString string(final String name,
                                        final int minLength,
                                        final int maxLength,
                                        final boolean greedy)
    {
        return (ArgumentString) new ArgumentString(name)
        {
            @Override
            public void validate(final String value) throws ArgumentValidateFailureException
            {
                final int length = value.length();
                if (length > maxLength)
                {
                    throw new ArgumentValidateFailureException(String.format(
                            "string length(%s) > max(%s)", length, maxLength));
                }
                if (length < minLength)
                {
                    throw new ArgumentValidateFailureException(String.format(
                            "string length(%s) < min(%s)", length, minLength));
                }
            }
        }.setGreedy(greedy);
    }
}
