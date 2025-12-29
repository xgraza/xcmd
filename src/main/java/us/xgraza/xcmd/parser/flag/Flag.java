/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.parser.flag;

import us.xgraza.xcmd.parser.argument.Argument;

/**
 * @author xgraza
 * @since 08/06/2025
 * <p>
 * Represents a flag for additional functionality
 */
public final class Flag<T>
{
    private final String name;
    private final Argument<T> argument;

    public Flag(final String name)
    {
        this(name, null);
    }

    public Flag(final String name, final Argument<T> argument)
    {
        if (name.contains(" "))
        {
            throw new RuntimeException("flag names cannot contain whitespaces");
        }
        this.name = name;
        this.argument = argument;
    }

    public String getName()
    {
        return name;
    }

    public Argument<T> getArgument()
    {
        return argument;
    }

    /**
     * If the flag is "conditional" (aka if present = true)
     *
     * @return if the argument is conditional
     */
    public boolean isConditional()
    {
        return argument == null;
    }
}
