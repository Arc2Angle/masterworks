package com.masterworks.masterworks.data.property.base;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.PropertyTypeMap;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;

public interface ExpressionProperty extends Property {
    Expression expression();

    @Override
    Type<?> type();

    interface Type<P extends ExpressionProperty> extends Property.Type<P> {
        P create(Expression expression);

        @Override
        default Decoder<P> decoder(
                Map<Construct.Component.Key, RoleReferenceResourceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<P>() {
                @Override
                public <T> DataResult<P> decode(Dynamic<T> input) {
                    return Expression.CODEC.parse(input).flatMap(expression -> {
                        // TODO: validate expression against roles' keys here
                        return DataResult.success(create(expression));
                    });
                }
            });
        }
    }


    default Double evaluate(Construct construct) {
        Composition composition = construct.composition().registered().value();

        Map<String, Double> arguments =
                construct.components().entrySet().stream().flatMap(entry -> {
                    Construct.Component.Key key = entry.getKey();
                    RoleReferenceResourceLocation role =
                            Optional.ofNullable(composition.components().get(key)).orElseThrow();

                    String argumentKey = "$" + key.value();
                    Optional<Double> argumentValue = entry.getValue().value().map(
                            material -> evaluateComponentMaterial(material),
                            componentConstruct -> evaluateComponentConstruct(componentConstruct,
                                    role));

                    return argumentValue.map(value -> Map.entry(argumentKey, value)).stream();
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return expression().evaluate(arguments);
    }

    private Optional<Double> evaluateComponentMaterial(MaterialReferenceResourceLocation material) {
        PropertyTypeMap map = material.registered().value().properties();

        return map.get(type()).map(property -> property.expression().evaluate(Map.of()));
    }

    private Optional<Double> evaluateComponentConstruct(Construct construct,
            RoleReferenceResourceLocation role) {
        Composition composition = construct.composition().registered().value();

        PropertyTypeMap properties = Optional.ofNullable(composition.properties().get(role))
                .orElseThrow(() -> new RuntimeException(
                        "Missing role \"" + role + "\" in composition " + construct.composition()));

        return properties.get(type()).map(property -> property.evaluate(construct));
    }
}
