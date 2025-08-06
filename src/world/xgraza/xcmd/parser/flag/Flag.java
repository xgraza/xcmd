package world.xgraza.xcmd.parser.flag;

/**
 * @author xgraza
 * @since 08/06/2025
 *
 * Represents a flag for additional functionality
 */
public final class Flag
{
    private final String name;

    public Flag(final String name)
    {
        if (name.contains(" "))
        {
            throw new RuntimeException("flag names cannot contain whitespaces");
        }
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
