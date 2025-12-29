/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.parser;

import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.exception.ArgumentParseException;
import us.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;
import us.xgraza.xcmd.parser.argument.internal.ArgumentString;
import us.xgraza.xcmd.parser.exception.MissingArgumentException;
import us.xgraza.xcmd.parser.flag.Flag;
import us.xgraza.xcmd.parser.lexer.Lexer;
import us.xgraza.xcmd.parser.lexer.Token;
import us.xgraza.xcmd.parser.lexer.exception.LexerException;
import us.xgraza.xcmd.registry.ICommandRegistry;
import us.xgraza.xcmd.registry.exception.InvalidCommandException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
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
    private static final Pattern FLAG_REGEX = Pattern.compile("-(\\S+)(?:\\s(?:[\"'](.*?)[\"']|(\\S+)))?");

    private final ICommandRegistry commandRegistry;

    public CommandParser(final ICommandRegistry commandRegistry)
    {
        this.commandRegistry = commandRegistry;
    }

    /**
     * Parses the input
     *
     * @param input the input string input
     * @return null or {@link CommandContext}
     */
    public CommandContext parse(final String input) throws LexerException,
            InvalidCommandException,
            ArgumentParseException,
            MissingArgumentException,
            ArgumentValidateFailureException
    {
        final List<String> rawArguments = splitIntoArguments(input);
        if (rawArguments.isEmpty())
        {
            return null;
        }

        final String commandName = rawArguments.remove(0);
        final ICommandExecutor executor = commandRegistry.getExecutor(commandName);
        if (executor == null)
        {
            throw new InvalidCommandException(commandName);
        }

        final CommandContext context = new CommandContext(executor, rawArguments);

        // we should early on parse flags so theyre not mixed with arguments
        final List<Flag<?>> flags = executor.getFlags();
        if (!flags.isEmpty())
        {
            parseFlags(input, rawArguments, flags, context.getResolvedFlagMap());
        }

        final List<Argument<?>> arguments = executor.getArguments();
        if (arguments.isEmpty())
        {
            return context;
        }
        final List<Token> tokenized = Lexer.tokenize(rawArguments);

        // validate arguments with their internal validation, ensure strict types
        validateArguments(tokenized, arguments);
        // finally supply our command context with our argument resolutions
        resolveArguments(tokenized, arguments, context.getResolvedArgumentMap());

        // finally give context over
        return context;
    }

    @SuppressWarnings("unchecked")
    private void parseFlags(final String raw,
                            final List<String> rawArguments,
                            final List<Flag<?>> flags,
                            final Map<String, Object> resolvedFlagMap)
            throws LexerException, ArgumentParseException, ArgumentValidateFailureException
    {
        final Matcher matcher = FLAG_REGEX.matcher(raw);
        while (matcher.find())
        {
            final String name = matcher.group(1);
            Flag<?> flag = null;
            {
                for (final Flag<?> next : flags)
                {
                    if (next.getName().equalsIgnoreCase(name))
                    {
                        flag = next;
                        break;
                    }
                }
            }
            if (flag == null)
            {
                continue;
            }
            rawArguments.remove("-" + name);

            if (flag.isConditional())
            {
                resolvedFlagMap.put(flag.getName(), true);
                continue;
            }

            final String input = findFirstGroup(matcher, 2);
            if (input == null)
            {
                throw new ArgumentParseException("Flag '"
                        + flag.getName()
                        + "' must be followed with a value");
            }

            rawArguments.remove(input);

            final Argument<?> argument = flag.getArgument();
            final Token token = Lexer.tokenizeSingle(input);
            compareTypes(token, argument);

            final Object value = argument.parse(token.getRaw(), token.getType());
            // hacky, but my pretty generics :(
            ((Argument<Object>) argument).validate(value);
            resolvedFlagMap.put(flag.getName(), value);
        }
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

            if (argument instanceof ArgumentString && ((ArgumentString) argument).isGreedy())
            {
                final List<Token> remainingTokens = tokenized.subList(i, tokenized.size());
                final StringJoiner joiner = new StringJoiner(" ");
                // this is the type of greed foretold in the bible
                for (final Token remainingToken : remainingTokens)
                {
                    joiner.add(remainingToken.getRaw());
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
                final List<Argument<?>> missingArguments = new LinkedList<>();
                for (int j = i; j < arguments.size(); ++j)
                {
                    final Argument<?> arg = arguments.get(j);
                    if (!arg.isRequired())
                    {
                        missingArguments.add(arg);
                    }
                }
                throw new MissingArgumentException(missingArguments);
            }
            if (argument.isRequired())
            {
                compareTypes(tokenized.get(i), argument);
            }
        }
    }

    private void compareTypes(final Token token, final Argument<?> argument)
            throws ArgumentParseException
    {
        // if the argument is a string, you could supply "20" and even though its token is an "integer" it'll still accept it
        // this should be a configurable behavior incase of an incorrect behavior for a specific use case
        if (argument.getTokenType().equals("string")
                || token.getType().equals(argument.getTokenType()))
        {
            return;
        }
        throw new ArgumentParseException("Incorrect type supplied for '"
                + argument.getName() + "'! Type "
                + token.getType() + "' does not match the expected type '"
                + argument.getTokenType() + "'");
    }

    /**
     * Splits raw input into a list of arguments (supports "quoting of arguments")
     *
     * @param raw the raw input
     * @return a list of strings split to represent an argument
     */
    private List<String> splitIntoArguments(final String raw)
    {
        final List<String> rawArguments = new LinkedList<>();
        final Matcher matcher = PHRASE_REGEX.matcher(raw
                .substring(commandRegistry.getCommandPrefix().length())
                .trim());
        while (matcher.find())
        {
            final String value = findFirstGroup(matcher, 1);
            if (value != null)
            {
                rawArguments.add(value);
            }
        }
        return rawArguments;
    }

    /**
     * Finds the first group from a matcher that is not null nor empty (my regular expressions suck...)
     *
     * @param matcher the {@link Matcher} object for the char sequence
     * @param start   the first group to search from
     * @return the first non-null & non-empty string, or null if none found
     */
    private String findFirstGroup(final Matcher matcher, final int start)
    {
        int index = start;
        while (index <= matcher.groupCount())
        {
            final String value = matcher.group(index);
            if (value != null && !value.isEmpty())
            {
                return value;
            }
            ++index;
        }
        return null;
    }
}
