package world.xgraza.xcmd.parser.lexer;

/**
 * @author xgraza
 * @since 08/06/2025
 * <p>
 * A token for the lexer. Provides the raw argument value and its inferred type
 */
public final class Token
{
    private final String raw, type;

    Token(String raw, String type)
    {
        this.raw = raw;
        this.type = type;
    }

    public String getRaw()
    {
        return raw;
    }

    public String getType()
    {
        return type;
    }
}
