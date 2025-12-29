/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.parser.argument.internal;

import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.exception.ArgumentParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public final class ArgumentEnum<T> extends Argument<T>
{
    private final Map<String, T> referenceMap = new HashMap<>();
    private final T[] constants;

    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public ArgumentEnum(final Class<T> type)
    {
        this(type, null);
    }

    public ArgumentEnum(final Class<T> type, final String name)
    {
        super(type, name);
        if (!type.isEnum())
        {
            throw new RuntimeException("must be an enum type for ArgumentEnum");
        }
        constants = type.getEnumConstants();
        for (final T e : constants)
        {
            referenceMap.put(e.toString().toLowerCase(), e);
        }
    }

    @Override
    public T parse(final String raw, final String type) throws ArgumentParseException
    {
        final T value = referenceMap.getOrDefault(raw
                .replaceAll(" ", "_")
                .replaceAll("-", "_")
                .toLowerCase(), null);
        if (value == null)
        {
            throw new ArgumentParseException("Unrecognized option '" + raw + "', Options are: "
                    + Arrays.stream(constants).map(Object::toString)
                    .collect(Collectors.joining(", ")));
        }
        return value;
    }
}
