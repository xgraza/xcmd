package world.xgraza.xcmd.parser;

import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;
import world.xgraza.xcmd.parser.exception.MissingArgumentException;
import world.xgraza.xcmd.parser.flag.Flag;
import world.xgraza.xcmd.parser.lexer.Lexer;
import world.xgraza.xcmd.parser.lexer.Token;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.lexer.exception.LexerException;
import world.xgraza.xcmd.registry.ICommandRegistry;
import world.xgraza.xcmd.registry.exception.InvalidCommandException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 08/06/2025
 * Interprets raw command input into objects
 */
public final class CommandParser
{
    private static final Pattern PHRASE_REGEX = Pattern.compile("[\"'](.*?)[\"']|(\\S+)");
    private static final Pattern FLAG_CONDITIONAL_REGEX = Pattern.compile("-([a-zA-Z0-9-_]+)");
    private static final Pattern FLAG_VALUE_REGEX = Pattern.compile("-([a-zA-Z0-9-_]+)=([\"'](.*?)[\"']|(\\S+))");

    private final ICommandRegistry commandRegistry;

    public CommandParser(final ICommandRegistry commandRegistry)
    {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Parses the input
     *
     * @param raw the raw string input
     * @return null or {@link CommandContext}
     */
    public CommandContext parse(final String raw)
            throws LexerException, InvalidCommandException, ArgumentParseException, MissingArgumentException
    {
        // first, put the raw input into "bite" size pieces of information
        final List<String> rawArguments = splitRawArguments(raw);
        // if there are none parsed, no command was provided
        if (rawArguments.isEmpty())
        {
            return null;
        }
        // pop the first element to grab the command name
        final String commandName = rawArguments.remove(0);

        // get executor from the provided command registry
        final ICommandExecutor executor = commandRegistry.getExecutor(commandName);
        if (executor == null)
        {
            throw new InvalidCommandException(commandName);
        }

        final CommandContext parseResult = new CommandContext();
        parseResult.setExecutor(executor);
        parseResult.setRawInput(rawArguments);

        // we should early on parse flags so theyre not considered normal arguments
        final List<Flag> flags = executor.getFlags();
        if (!flags.isEmpty())
        {
            // TODO
        }

        // get list of arguments per the executor
        final List<Argument<?>> arguments = executor.getArguments();
        // if no arguments are present, resolve early
        if (arguments.isEmpty())
        {
            return parseResult;
        }

        // next, we must run each argument through a lexer (aka tokenizing)
        // this will map each argument to a type
        // raw argument -> type (ex: boolean, integer, string)
        final List<Token> tokenized = Lexer.tokenize(rawArguments);

        // now that we have the types, we can use this to check against our argument types
        // this will make the command parser "strict"
        // TODO: option to ignore unknown args/flags?
        validateArguments(tokenized, arguments);

        // next, we need to parse these tokens into objects
        // this is the actually useful part to a command
        try
        {
            parseResult.setResolvedArgumentMap(resolveArguments(tokenized, arguments));
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }

        return parseResult;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveArguments(final List<Token> tokenized,
                                                 final List<Argument<?>> arguments)
            throws ArgumentValidateFailureException, ArgumentParseException
    {
        final Map<String, Object> resolvedArgumentMap = new LinkedHashMap<>();
        if (tokenized.isEmpty())
        {
            return resolvedArgumentMap;
        }
        for (int i = 0; i < arguments.size(); ++i)
        {
            final Argument<?> argument = arguments.get(i);

            final Token token = tokenized.get(i);
            if (!argument.isRequired() && !token.getType().equals(argument.getTokenType()))
            {
                continue;
            }

            if (argument.isGreedy())
            {
                final List<Token> restOfTokens = tokenized.subList(i, tokenized.size());
                final StringJoiner joiner = new StringJoiner(" ");
                for (final Token t : restOfTokens)
                {
                    // only be greedy to those of the same type
                    // ex: this is a greedy argument 20 hello
                    // the argument which is greedy will take input until the "20"
                    // because the "20" is considered a different argument (an integer)
                    // of course if it's surrounded in quotes, this wont matter.
                    if (!t.getType().equals("string"))
                    {
                        break;
                    }
                    joiner.add(t.getRaw());
                }
                resolvedArgumentMap.put(argument.getName(), joiner.toString());
                return resolvedArgumentMap;
            }

            final Object value = argument.parse(token.getRaw(), token.getType());
            ((Argument<Object>) argument).validate(value);
            resolvedArgumentMap.put(argument.getName(), value);
        }
        return resolvedArgumentMap;
    }

    private void validateArguments(final List<Token> tokenized,
                                     final List<Argument<?>> arguments)
            throws ArgumentParseException, MissingArgumentException
    {
        final int required = arguments.stream()
                .reduce(0, (p, arg) -> p + (arg.isRequired() ? 1 : 0), Integer::sum);
        if (tokenized.size() < required)
        {
            throw new ArgumentParseException("Too little argument(s) (expected "
                    + (arguments.size() > required ? "at minimum " : "")
                    + required
                    + " argument(s), got "
                    + tokenized.size()
                    + ")");
        }

        for (int i = 0; i < arguments.size(); ++i)
        {
            final Argument<?> argument = arguments.get(i);
            // finished...
            if (i > tokenized.size() - 1)
            {
                if (argument.isRequired())
                {
                    throw new MissingArgumentException(argument.getName());
                }
                break;
            }
            final Token token = tokenized.get(i);
            if (!token.getType().equals(argument.getTokenType()))
            {
                throw new ArgumentParseException("Expected type "
                        + argument.getTokenType()
                        + " for argument '"
                        + argument.getName()
                        + "' but instead got type "
                        + token.getType()
                        + " from '"
                        + token.getRaw()
                        + "'");
            }
        }
    }

    private List<String> splitRawArguments(final String raw)
    {
        final List<String> rawArguments = new LinkedList<>();
        final Matcher matcher = PHRASE_REGEX.matcher(raw
                .substring(commandRegistry.getCommandPrefix().length())
                .trim());
        while (matcher.find())
        {
            String value = matcher.group(1);
            if (value == null)
            {
                value = matcher.group(0);
            }
            if (!value.isEmpty())
            {
                rawArguments.add(value);
            }
        }
        return rawArguments;
    }
}
