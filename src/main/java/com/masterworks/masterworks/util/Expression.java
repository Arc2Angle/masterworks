package com.masterworks.masterworks.util;

import com.mojang.serialization.Codec;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Represents a mathematical expression that can contain named "variables".
 * <p>
 * It can be serialized/deserialized as a string and evaluated with provided variable values.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code 6.7}</li>
 * <li>{@code $value * 3}</li>
 * <li>{@code $a * 0.5 + $b * 0.5}</li>
 * <li>{@code $2 + $0 + $1}</li>
 * </ul>
 */
public interface Expression {
    static final Codec<Expression> CODEC =
            Codec.withAlternative(Codec.STRING.xmap(Expression.Impl::parse, Expression::format),
                    Codec.DOUBLE, Impl.Constant::new);

    /**
     * Evaluates the expression with the provided arguments
     * 
     * @param arguments to be used as variable values. Unused variables will be ignored, and can be
     *        left null
     * @return The result of the expression evaluation
     * @throws IllegalArgumentException if the expression is missing required variables
     */
    @Nonnull
    Double evaluate(@Nonnull Map<String, Double> arguments) throws IllegalArgumentException;

    /**
     * Gets the set of variable names used in this expression
     * 
     * @return Stream of variable names
     */
    @Nonnull
    Stream<String> arguments();

    @Nonnull
    String format();

    class Impl {
        static Expression parse(String format) {
            format = format.trim();

            if (format.contains("+")) {
                return new Sum(Stream.of(format.split("\\+")).map(Impl::parse).toList());
            }

            if (format.contains("*")) {
                return new Product(Stream.of(format.split("\\*")).map(Impl::parse).toList());
            }

            if (format.startsWith("-")) {
                return new Negation(Impl.parse(format.substring(1)));
            }

            if (IDENTIFIER_PATTERN.matcher(format).matches()) {
                return new Variable(format);
            }

            if (DECIMAL_PATTERN.matcher(format).matches()) {
                return new Constant(Double.parseDouble(format));
            }

            throw new IllegalArgumentException("Invalid expression format: " + format);
        }

        static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[$a-z_][a-z0-9_]*$");
        static final Pattern DECIMAL_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

        record Constant(Double value) implements Expression {
            @Override
            public Double evaluate(@Nonnull Map<String, Double> arguments) {
                return value;
            }

            @Override
            public Stream<String> arguments() {
                return Stream.empty();
            }

            @Override
            public String format() {
                return value.toString();
            }
        }

        record Variable(String name) implements Expression {
            @Override
            public Double evaluate(@Nonnull Map<String, Double> arguments)
                    throws IllegalArgumentException {
                Double value = arguments.get(name);

                if (value == null) {
                    throw new IllegalArgumentException(
                            "Variable \"" + name + "\" not found in arguments");
                }

                return value;
            }

            @Override
            public Stream<String> arguments() {
                return Stream.of(name);
            }

            @Override
            public String format() {
                return name;
            }
        }

        record Negation(Expression child) implements Expression {
            @Override
            public Double evaluate(@Nonnull Map<String, Double> arguments)
                    throws IllegalArgumentException {
                return -child.evaluate(arguments);
            }

            @Override
            public Stream<String> arguments() {
                return child.arguments();
            }

            @Override
            public String format() {
                return "-" + child.format();
            }
        }

        interface NaryOperator extends Expression {
            List<Expression> children();

            Double calculate(Stream<Double> values) throws IllegalArgumentException;

            String symbol();

            @Override
            default Double evaluate(@Nonnull Map<String, Double> arguments)
                    throws IllegalArgumentException {
                return calculate(children().stream().map(child -> child.evaluate(arguments)));
            }

            @Override
            default Stream<String> arguments() {
                return children().stream().flatMap(Expression::arguments);
            }

            @Override
            default String format() {
                return String.join(" " + symbol() + " ",
                        children().stream().map(Expression::format).toArray(String[]::new));
            }
        }

        record Sum(List<Expression> children) implements NaryOperator {
            @Override
            public Double calculate(Stream<Double> values) throws IllegalArgumentException {
                return values.reduce(0.0, Double::sum);
            }

            @Override
            public String symbol() {
                return "+";
            }
        }

        record Product(List<Expression> children) implements NaryOperator {
            @Override
            public Double calculate(Stream<Double> values) throws IllegalArgumentException {
                return values.reduce(1.0, (a, b) -> a * b);
            }

            @Override
            public String symbol() {
                return "*";
            }
        }

    }
}
