package ru.ramil.xunit;

import ru.ramil.tests.CalculatorTest;

public class MainApp {
    public static void main(String[] args) {
        Testing.start(CalculatorTest.class);
        //Testing.start("ru.ramil.tests.CalculatorTest");
    }
}
