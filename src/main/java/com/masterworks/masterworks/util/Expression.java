package com.masterworks.masterworks.util;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.List;

/**
 * Represents a mathematical expression that can contain variables prefixed with '$'.
 * <p>
 * It can be serialized/deserialized as a string and evaluated with provided variable values.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code $a * 3}</li>
 * <li>{@code $a * 0.5 + $b * 0.5}</li>
 * <li>{@code $a + $b + $c}</li>
 * </ul>
 */
public abstract class Expression {

    private Expression() {}

    public abstract Stream<String> parameters();

    public abstract Double evaluate(Map<String, Double> arguments) throws IllegalArgumentException;

    public abstract String format();

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

        if (VARIABLE_PATTERN.matcher(format).matches()) {
            return new Variable(format.substring(1));
        }

        if (CONSTANT_PATTERN.matcher(format).matches()) {
            return new Constant(Double.parseDouble(format));
        }

        throw new IllegalArgumentException("Invalid expression format: " + format);
    }


    private static class Constant extends Expression {
        private Double value;

        public Constant(Double value) {
            this.value = value;
        }

        @Override
        public Stream<String> parameters() {
            return Stream.empty();
        }

        @Override
        public Double evaluate(Map<String, Double> arguments) {
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
        public Stream<String> parameters() {
            return Stream.of(name);
        }

        @Override
        public Double evaluate(Map<String, Double> arguments) throws IllegalArgumentException {
            if (!arguments.containsKey(name)) {
                throw new IllegalArgumentException("Variable " + name + " not found in arguments");
            }
            return arguments.get(name);
        }

        @Override
        public String format() {
            return "$" + name;
        }
    }

    private static class Negation extends Expression {
        private Expression child;

        public Negation(Expression child) {
            this.child = child;
        }

        @Override
        public Stream<String> parameters() {
            return child.parameters();
        }

        @Override
        public Double evaluate(Map<String, Double> arguments) throws IllegalArgumentException {
            return -child.evaluate(arguments);
        }

        @Override
        public String format() {
            return "-" + child.format();
        }
    }

    private static abstract class Operator extends Expression {
        private List<Expression> children;

        protected Operator(List<Expression> children) {
            this.children = children;
        }


        @Override
        public Stream<String> parameters() {
            return children.stream().flatMap(Expression::parameters);
        }

        protected abstract Double evaluate(Stream<Double> values) throws IllegalArgumentException;

        @Override
        public Double evaluate(Map<String, Double> arguments) throws IllegalArgumentException {
            return evaluate(children.stream().map(child -> child.evaluate(arguments)));
        }

        protected abstract String symbol();

        @Override
        public String format() {
            return String.join(" " + symbol() + " ",
                    children.stream().map(Expression::format).toArray(String[]::new));
        }
    }

    private static class Sum extends Operator {
        public Sum(List<Expression> children) {
            super(children);
        }

        @Override
        protected Double evaluate(Stream<Double> values) throws IllegalArgumentException {
            return values.reduce(0.0, Double::sum);
        }

        @Override
        protected String symbol() {
            return "+";
        }
    }

    private static class Product extends Operator {
        public Product(List<Expression> children) {
            super(children);
        }

        @Override
        protected Double evaluate(Stream<Double> values) throws IllegalArgumentException {
            return values.reduce(1.0, (a, b) -> a * b);
        }

        @Override
        protected String symbol() {
            return "*";
        }
    }

    public static final Codec<Expression> CODEC =
            Codec.STRING.xmap(Expression::parse, Expression::format);

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\$[a-z_][a-z0-9_]*$");
    private static final Pattern CONSTANT_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
}
