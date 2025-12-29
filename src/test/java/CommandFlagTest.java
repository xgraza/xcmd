import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.internal.ArgumentNumber;
import us.xgraza.xcmd.parser.argument.internal.ArgumentString;
import us.xgraza.xcmd.parser.flag.Flag;

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
        return new String[]{ "cft" };
    }
}
