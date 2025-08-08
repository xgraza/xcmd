package test;

import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.executor.ICommandExecutor;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.internal.ArgumentNumber;
import world.xgraza.xcmd.parser.argument.internal.ArgumentString;
import world.xgraza.xcmd.parser.flag.Flag;

import java.util.LinkedList;
import java.util.List;

public class CommandFlagTest implements ICommandExecutor
{
    @Override
    public CommandResult dispatch(CommandContext context)
    {
        if (context.hasFlag("test"))
        {
            return context.ok(context.getFlag("test"));
        }
        if (context.hasArgument("number"))
        {
            return context.ok(context.<Integer>getArgument("number").toString());
        }
        return context.ok("ok");
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        final List<Argument<?>> arguments = new LinkedList<>();
        arguments.add(ArgumentNumber.number(Integer.class, "number").setRequired(false));
        return arguments;
    }

    @Override
    public List<Flag<?>> getFlags()
    {
        final List<Flag<?>> flags = new LinkedList<>();
        flags.add(new Flag<>("test", ArgumentString.string()));
        return flags;
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "cft" };
    }
}
