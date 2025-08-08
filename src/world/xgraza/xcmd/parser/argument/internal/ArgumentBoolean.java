package world.xgraza.xcmd.parser.argument.internal;

import world.xgraza.xcmd.parser.argument.Argument;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public final class ArgumentBoolean extends Argument<Boolean>
{
    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public ArgumentBoolean()
    {
        this(null);
    }

    public ArgumentBoolean(final String name)
    {
        super(Boolean.class, name);
    }

    @Override
    public Boolean parse(String raw, String type)
    {
        if (!type.equals("boolean"))
        {
            throw new RuntimeException("Invalid type");
        }

        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("t"))
        {
            return true;
        }
        if (raw.equalsIgnoreCase("false") || raw.equalsIgnoreCase("f"))
        {
            return false;
        }

        throw new RuntimeException("unrecognized option '" + raw + "'");
    }

    /**
     * Helper method to create an {@link ArgumentBoolean}
     *
     * @return an {@link ArgumentBoolean} with no name (flag only)
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public static ArgumentBoolean bool()
    {
        return new ArgumentBoolean();
    }

    /**
     * Helper method to create an {@link ArgumentBoolean}
     *
     * @param name the name for this {@link ArgumentBoolean}
     * @return an {@link ArgumentBoolean} with the supplied name
     */
    public static ArgumentBoolean bool(final String name)
    {
        return new ArgumentBoolean(name);
    }
}
