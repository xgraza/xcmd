package world.xgraza.xcmd.parser.argument;

import world.xgraza.xcmd.parser.argument.exception.ArgumentValidateFailureException;

public class ArgumentNumber<T extends Number> extends Argument<T>
{
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

    public static <T extends Number> ArgumentNumber<T> number(final Class<T> type,
                                                              final String name)
    {
        return new ArgumentNumber<>(type, name);
    }

    public static <T extends Number> ArgumentNumber<T> number(final Class<T> type,
                                                              final String name,
                                                              final T min,
                                                              final T max)
    {
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
