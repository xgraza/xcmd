package world.xgraza.xcmd.parser.argument;

public final class ArgumentBoolean extends Argument<Boolean>
{
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
}
