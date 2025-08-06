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
    public CommandContext parse(final String raw) throws LexerException,
            InvalidCommandException,
            ArgumentParseException,
            MissingArgumentException,
            ArgumentValidateFailureException
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

        // create the command context - this holds arguments, flags, and the executor
        final CommandContext context = new CommandContext(executor, rawArguments);

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
            return context;
        }

        // "tokenize" each raw argument (ex: "balls" -> "string")
        final List<Token> tokenized = Lexer.tokenize(rawArguments);

        // ensure we have the required arguments and the types match
        validateArguments(tokenized, arguments);

        // resolve the arguments
        // "resolve" is two parts: parse & validate
        // parsing will actually give the object needed (such as turning a string literal "true" to true)
        // validating is an additional step determined by the argument (ex: min/max bounds on a integer)
        resolveArguments(tokenized, arguments, context.getResolvedArguments());

        return context;
    }

    @SuppressWarnings("unchecked")
    private void resolveArguments(final List<Token> tokenized,
                                  final List<Argument<?>> arguments,
                                  final Map<String, Object> resolvedArgumentMap)
            throws ArgumentValidateFailureException, ArgumentParseException
    {
        if (tokenized.isEmpty())
        {
            return;
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
                // this is the type of greed foretold in the bible
                for (final Token t : restOfTokens)
                {
                    joiner.add(t.getRaw());
                }
                resolvedArgumentMap.put(argument.getName(), joiner.toString());
                return;
            }

            final Object value = argument.parse(token.getRaw(), token.getType());
            // hacky, but my pretty generics :(
            ((Argument<Object>) argument).validate(value);
            resolvedArgumentMap.put(argument.getName(), value);
        }
    }

    private void validateArguments(final List<Token> tokenized, final List<Argument<?>> arguments)
            throws ArgumentParseException, MissingArgumentException
    {
        for (int i = 0; i < arguments.size(); ++i)
        {
            final Argument<?> argument = arguments.get(i);
            if (i > tokenized.size() - 1)
            {
                // non-required arguments are last, therefore we can stop here
                if (!argument.isRequired())
                {
                    break;
                }
                final List<String> missingArguments = new LinkedList<>();
                for (int j = i; j < arguments.size(); ++j)
                {
                    final Argument<?> arg = arguments.get(j);
                    if (arg.isRequired())
                    {
                        missingArguments.add(arg.getName() + "(" + arg.getTokenType() + ")");
                    }
                }
                throw new MissingArgumentException("Missing " + missingArguments.size()
                        + " argument(s): " + String.join(", ", missingArguments));
            }

            final Token token = tokenized.get(i);
            if (!token.getType().equals(argument.getTokenType()) && argument.isRequired())
            {
                throw new ArgumentParseException("Incorrect type supplied for '"
                        + argument.getName() + "'! Type "
                        + token.getType() + "' does not match the expected type '"
                        + argument.getTokenType() + "'");
            }
        }
    }

    /**
     * Splits raw input into a list of arguments (supports "quoting of arguments")
     * @param raw the raw input
     * @return a list of strings split to represent an argument
     */
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
