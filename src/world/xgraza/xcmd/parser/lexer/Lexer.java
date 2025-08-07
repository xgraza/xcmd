package world.xgraza.xcmd.parser.lexer;

import world.xgraza.xcmd.parser.lexer.exception.LexerException;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 08/06/2025
 *
 * Turns raw arguments into tokens for further processing
 */
public final class Lexer
{
    private static final Pattern NUMBER_REGEX = Pattern.compile("^-?[0-9,]+(\\.\\d+)?([LFDfd])?");
    private static final Pattern BOOLEAN_REGEX = Pattern.compile("true|false|t|f");

    /**
     * Tokenizes each argument
     * @param arguments a {@link List<String>} of raw arguments
     * @return a {@link List<Token>}
     * @throws LexerException if a type could not be inferred
     */
    public static List<Token> tokenize(final List<String> arguments) throws LexerException
    {
        final List<Token> tokenList = new LinkedList<>();
        for (final String argument : arguments)
        {
            final Token token = tokenize(argument);
            if (token != null)
            {
                tokenList.add(token);
            }
        }
        return tokenList;
    }

    public static Token tokenize(final String argument) throws LexerException
    {
        if (argument.matches(NUMBER_REGEX.pattern()))
        {
            return inferNumberType(argument);
        }
        else if (argument.matches(BOOLEAN_REGEX.pattern()))
        {
            return new Token(argument, "boolean");
        }
        else
        {
            // "string" is super general, and should be the default type
            // for custom argument types
            return new Token(argument, "string");
        }
    }

    // TODO: possibly improve?
    private static Token inferNumberType(final String argument) throws LexerException
    {
        final Matcher matcher = NUMBER_REGEX.matcher(argument);
        if (!matcher.find())
        {
            throw new LexerException("Could not find match for a number");
        }
        final String digit = matcher.group();
        final boolean isDecimal = matcher.group(1) != null;
        String type = matcher.group(2);
        if (type == null)
        {
            type = isDecimal ? "double" : "integer";
        }

        switch (type)
        {
            case "D":
            case "d":
            {
                type = "double";
                break;
            }
            case "F":
            case "f":
            {
                type = "float";
                break;
            }
            case "L":
            {
                if (isDecimal)
                {
                    throw new LexerException("Number type Long cannot contain a decimal");
                }
                type = "long";
            }
        }

        return new Token(digit, type);
    }
}
