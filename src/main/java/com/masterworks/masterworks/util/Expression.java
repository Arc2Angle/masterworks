package com.masterworks.masterworks.util;

import com.mojang.serialization.Codec;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a mathematical expression that can contain positional "variables" prefixed with '$'.
 * <p>
 * It can be serialized/deserialized as a string and evaluated with provided variable values.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code $0 * 3}</li>
 * <li>{@code $0 * 0.5 + $1 * 0.5}</li>
 * <li>{@code $2 + $0 + $1}</li>
 * </ul>
 */
public abstract class Expression {

    /**
     * Evaluates the expression with the provided arguments
     * 
     * @param arguments to be used as variable values. Unused variables will be ignored, and can be
     *        left null
     * @return The result of the expression evaluation
     * @throws IllegalArgumentException if the expression is missing required variables
     */
    public abstract Double evaluate(@Nonnull Map<String, Double> arguments)
            throws IllegalArgumentException;

    public abstract String format();

    private static class Constant extends Expression {
        private Double value;

        public Constant(Double value) {
            this.value = value;
        }

        @Override
        public Double evaluate(@Nonnull Map<String, Double> arguments) {
            return value;
        }

        @Override
        public String format() {
            return value.toString();
        }
    }

    private static class Variable extends Expression {
        private String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public Double evaluate(@Nonnull Map<String, Double> arguments)
                throws IllegalArgumentException {
            try {
                return Objects.requireNonNull(arguments.get(name));
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Variable \"" + name + "\" not found in arguments", e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Variable \"" + name + "\" is null", e);
            }
        }

        @Override
        public String format() {
            return name;
        }
    }

    private static class Negation extends Expression {
        private Expression child;

        public Negation(Expression child) {
            this.child = child;
        }

        @Override
        public Double evaluate(@Nonnull Map<String, Double> arguments)
                throws IllegalArgumentException {
            return -child.evaluate(arguments);
        }

        @Override
        public String format() {
            return "-" + child.format();
        }
    }

    private static abstract class NaryOperator extends Expression {
        private List<Expression> children;

        protected NaryOperator(List<Expression> children) {
            this.children = children;
        }

        protected abstract Double calculate(Stream<Double> values) throws IllegalArgumentException;

        @Override
        public Double evaluate(@Nonnull Map<String, Double> arguments)
                throws IllegalArgumentException {
            return calculate(children.stream().map(child -> child.evaluate(arguments)));
        }

        protected abstract String symbol();

        @Override
        public String format() {
            return String.join(" " + symbol() + " ",
                    children.stream().map(Expression::format).toArray(String[]::new));
        }
    }

    private static class Sum extends NaryOperator {
        public Sum(List<Expression> children) {
            super(children);
        }

        @Override
        protected Double calculate(Stream<Double> values) throws IllegalArgumentException {
            return values.reduce(0.0, Double::sum);
        }

        @Override
        protected String symbol() {
            return "+";
        }
    }

    private static class Product extends NaryOperator {
        public Product(List<Expression> children) {
            super(children);
        }

        @Override
        protected Double calculate(Stream<Double> values) throws IllegalArgumentException {
            return values.reduce(1.0, (a, b) -> a * b);
        }

        @Override
        protected String symbol() {
            return "*";
        }
    }

    public static Expression parse(String format) {
        format = format.trim();

        if (format.contains("+")) {
            return new Sum(Stream.of(format.split("\\+")).map(Expression::parse).toList());
        }

        if (format.contains("*")) {
            return new Product(Stream.of(format.split("\\*")).map(Expression::parse).toList());
        }

        if (format.startsWith("-")) {
            return new Negation(Expression.parse(format.substring(1)));
        }

        if (IDENTIFIER_PATTERN.matcher(format).matches()) {
            return new Variable(format);
        }

        if (DECIMAL_PATTERN.matcher(format).matches()) {
            return new Constant(Double.parseDouble(format));
        }

        throw new IllegalArgumentException("Invalid expression format: " + format);
    }

    public static final Codec<Expression> CODEC =
            Codec.STRING.xmap(Expression::parse, Expression::format);

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[$a-z_][a-z0-9_]*$");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
}
