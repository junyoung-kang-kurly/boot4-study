package com.example.springboot4study.java25;

import org.springframework.stereotype.Component;

/**
 * Java 25 Feature: Primitive Types in Pattern Matching (JEP 507)
 *
 * Java 25 extends pattern matching to work with all primitive types (int, long, double, etc.).
 * Previously, pattern matching was limited to reference types only.
 * This enables cleaner code when working with instanceof and switch expressions.
 */
@Component
public class PatternMatchingExample {

    /**
     * Demonstrates pattern matching with primitive types in switch expressions.
     * Java 25 allows direct matching on primitive values with type patterns.
     */
    public String classifyNumber(Object value) {
        return switch (value) {
            case Integer i when i < 0 -> "Negative integer: " + i;
            case Integer i when i == 0 -> "Zero";
            case Integer i when i > 0 && i <= 100 -> "Small positive integer: " + i;
            case Integer i -> "Large positive integer: " + i;
            case Long l -> "Long value: " + l;
            case Double d when Double.isNaN(d) -> "Not a Number";
            case Double d when Double.isInfinite(d) -> "Infinite value";
            case Double d -> "Double value: " + d;
            case Float f -> "Float value: " + f;
            case null -> "Null value";
            default -> "Unknown type: " + value.getClass().getSimpleName();
        };
    }

    /**
     * Demonstrates primitive pattern matching for HTTP status code classification.
     * Clean and readable way to handle different status code ranges.
     */
    public String classifyHttpStatus(int statusCode) {
        return switch (statusCode) {
            case int s when s >= 100 && s < 200 -> "Informational";
            case int s when s >= 200 && s < 300 -> "Success";
            case int s when s >= 300 && s < 400 -> "Redirection";
            case int s when s >= 400 && s < 500 -> "Client Error";
            case int s when s >= 500 && s < 600 -> "Server Error";
            default -> "Unknown Status";
        };
    }

    /**
     * Demonstrates exhaustive pattern matching with sealed types and primitives.
     */
    public record Temperature(double value, TemperatureUnit unit) {}

    public enum TemperatureUnit { CELSIUS, FAHRENHEIT, KELVIN }

    public String describeTemperature(Temperature temp) {
        return switch (temp) {
            case Temperature(double v, TemperatureUnit.CELSIUS) when v < 0 -> "Freezing cold (Celsius)";
            case Temperature(double v, TemperatureUnit.CELSIUS) when v < 20 -> "Cold (Celsius)";
            case Temperature(double v, TemperatureUnit.CELSIUS) when v < 30 -> "Comfortable (Celsius)";
            case Temperature(double v, TemperatureUnit.CELSIUS) -> "Hot (Celsius): " + v + "°C";
            case Temperature(double v, TemperatureUnit.FAHRENHEIT) when v < 32 -> "Freezing (Fahrenheit)";
            case Temperature(double v, TemperatureUnit.FAHRENHEIT) -> "Temperature: " + v + "°F";
            case Temperature(double v, TemperatureUnit.KELVIN) when v < 273.15 -> "Below freezing (Kelvin)";
            case Temperature(double v, TemperatureUnit.KELVIN) -> "Temperature: " + v + "K";
        };
    }

    /**
     * Demonstrates pattern matching with record deconstruction and primitive guards.
     */
    public record Rectangle(int width, int height) {}

    public String classifyRectangle(Rectangle rect) {
        return switch (rect) {
            case Rectangle(int w, int h) when w == h -> "Square with side " + w;
            case Rectangle(int w, int h) when w > h -> "Landscape rectangle " + w + "x" + h;
            case Rectangle(int w, int h) -> "Portrait rectangle " + w + "x" + h;
        };
    }
}
