package world.xgraza.xcmd.parser.argument.internal;

import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;

/**
 * @author xgraza
 * @since 08/06/2025
 */
public class ArgumentNumber<T extends Number> extends Argument<T>
{
    /**
     * @apiNote only used for flags, will throw an exception if used otherwise
     */
    public ArgumentNumber(final Class<T> type)
    {
        this(type, null);
    }

    public ArgumentNumber(final Class<T> type, final String name)
    {
        super(type, name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse(final String raw, final String type)
    {
        switch (type)
        {
            case "integer":
            {
                return (T) (Object) Integer.parseInt(raw);
            }
            case "long":
            {
                return (T) (Object) Long.parseLong(raw);
            }
            case "double":
            {
                return (T) (Object) Double.parseDouble(raw);
            }
            case "float":
            {
                return (T) (Object) Float.parseFloat(raw);
            }
            default:
            {
                throw new RuntimeException("invalid token: " + type);
            }
        }
    }

    @Override
    public String getTokenType()
    {
        return getType().getSimpleName().toLowerCase();
    }

    /**
     * Helper method to create a new {@link ArgumentNumber<T>}
     * @param type the type (extending {@link Number}
     * @param name the name of the argument
     * @return the {@link ArgumentNumber<T>} object with the supplied name
     * @param <T> a class extending {@link Number}
     */
    public static <T extends Number> ArgumentNumber<T> number(final Class<T> type,
                                                              final String name)
    {
        return new ArgumentNumber<>(type, name);
    }

    /**
     * Helper method to create a new {@link ArgumentNumber<T>} with bounds
     * @param type the type (extending {@link Number}
     * @param name the name of the argument
     * @param min the minimum value
     * @param max the maximum value
     * @return the {@link ArgumentNumber<T>} object with the supplied name
     * @param <T> a class extending {@link Number}
     */
    public static <T extends Number> ArgumentNumber<T> number(final Class<T> type,
                                                              final String name,
                                                              final T min,
                                                              final T max)
    {
        if (min.doubleValue() > max.doubleValue())
        {
            throw new RuntimeException("min cannot be greater than max");
        }
        return new ArgumentNumber<T>(type, name)
        {
            @Override
            public void validate(final T value) throws ArgumentValidateFailureException
            {
                final double v = value.doubleValue();
                if (v > max.doubleValue())
                {
                    throw new ArgumentValidateFailureException(String.format("input(%s) > max(%s)", value, max));
                }
                if (v < min.doubleValue())
                {
                    throw new ArgumentValidateFailureException(String.format("input(%s) < min(%s)", value, min));
                }
            }
        };
    }
}
