package com.masterworks.masterworks.data.property.base;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ExpressionProperty extends Property {
    static final String ARGUMENT_PREFIX = "$";

    Expression expression();

    Map<Construct.Component.Key, RoleReferenceLocation> roles();

    default Double evaluate(Map<Construct.Component.Key, Construct.Component> components) {
        Map<String, Double> arguments = components.entrySet().stream()
                .flatMap(entry -> {
                    Construct.Component.Key key = entry.getKey();
                    Construct.Component component = entry.getValue();

                    RoleReferenceLocation role = Optional.ofNullable(roles().get(key))
                            .orElseThrow(() -> new IllegalStateException(
                                    "No role mapping for component key " + key + " in property " + this));

                    String argumentKey = "$" + key.value();
                    Optional<Double> argumentValue = component
                            .properties(role)
                            .get(type())
                            .map(property -> property.evaluate(component.components()));

                    return argumentValue.map(value -> Map.entry(argumentKey, value)).stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return expression().evaluate(arguments);
    }

    @Override
    Type<?> type();

    public abstract static class Type<P extends ExpressionProperty> implements Property.Type<P> {
        protected Decoder<P> decoder(
                BiFunction<Expression, Map<Construct.Component.Key, RoleReferenceLocation>, P> constructor,
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<P>() {
                @Override
                public <T> DataResult<P> decode(Dynamic<T> input) {
                    return decodeExpression(input, components)
                            .map(expression -> constructor.apply(expression, components));
                }
            });
        }

        protected <T> DataResult<Expression> decodeExpression(
                Dynamic<T> input, Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Expression.CODEC.parse(input).flatMap(expression -> expression
                    .arguments()
                    .flatMap(argument -> {
                        if (!argument.startsWith(ARGUMENT_PREFIX)) {
                            return Stream.of(DataResult.<Expression>error(() -> "Invalid statement \""
                                    + argument + "\" in expression \"" + expression
                                    + "\": component references must start with \""
                                    + ARGUMENT_PREFIX + "\""));
                        }

                        Construct.Component.Key key =
                                new Construct.Component.Key(argument.substring(ARGUMENT_PREFIX.length()));

                        if (!components.containsKey(key)) {
                            return Stream.of(DataResult.<Expression>error(() -> "Expression \""
                                    + expression + "\" references non-existent component \"" + key
                                    + "\""));
                        }

                        return Stream.empty();
                    })
                    .findFirst()
                    .orElse(DataResult.success(expression)));
        }
    }
}
