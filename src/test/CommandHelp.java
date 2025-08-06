package test;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.ArgumentCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

final class CommandHelp implements ICommandExecutor
{
    private final TestCommandRegistry commandRegistry;

    public CommandHelp(final TestCommandRegistry commandRegistry)
    {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public CommandResult dispatch(CommandContext context)
    {
        if (context.isArgumentResolved("command-name"))
        {
            final ICommandExecutor executor = context.getArgument("command-name");
            return context.ok("Aliases: " + String.join("|", executor.getAliases()));
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("Commands: ");
        builder.append(commandRegistry.getExecutors().size());
        builder.append("\n");

        final StringJoiner joiner = new StringJoiner(", ");
        for (final ICommandExecutor executor : commandRegistry.getExecutors())
        {
            joiner.add(executor.getAliases()[0]);
        }
        builder.append(joiner);
        return context.ok(builder.toString());
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        final List<Argument<?>> arguments = new LinkedList<>();
        arguments.add(new ArgumentCommand(commandRegistry, "command-name")
                .setRequired(false));
        return arguments;
    }

    @Override
    public List<String> suggest(final Argument<?> argument, final String input)
    {
        final List<String> suggestions = new LinkedList<>();
        for (final String alias : commandRegistry.getCommandExecutorAliasMap().keySet())
        {
            if (input.equalsIgnoreCase(alias))
            {
                continue;
            }
            if (alias.startsWith(input) || alias.contains(input) || alias.endsWith(input))
            {
                suggestions.add(alias);
            }
        }
        return suggestions;
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "help", "cmds", "commands" };
    }
}
