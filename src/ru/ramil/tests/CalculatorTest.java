package ru.ramil.tests;

import ru.ramil.Calculator.Calculator;
import ru.ramil.xunit.Annotation.*;

public class CalculatorTest {
    private Calculator calculator;

    @BeforeSuite
    //@AfterSuite            если раскомментировать не сработает
    private void start() {
        calculator = new Calculator();
        System.out.println("Test begin");
    }

    @AfterSuite
    //@BeforeSuite           если раскомментировать не сработает
    private void end() {
        System.out.println("Test end");
    }

    @Test
    //@AfterSuite            если раскомментировать не сработает
    public void addTest(){
        System.out.println("addTest - priority = " + Priority.PRIORITY_5);
        System.out.println("5 + 5 = " + calculator.add(5, 5));
    }

    @Test
    //@BeforeSuite           если раскомментировать не сработает
    private void difTest() {
        System.out.println("difTest - priority = " + Priority.PRIORITY_5);
        System.out.println("20 - 5 = " + calculator.dif(20, 5));
    }

    @Test(priority = Priority.PRIORITY_6)
    //@BeforeSuite           если раскомментировать не сработает
    //@AfterSuite
    public void mulTest() {
        System.out.println("mulTest - priority = " + Priority.PRIORITY_6);
        System.out.println("5 * 5 = " + calculator.mul(5, 5));
    }

    @Test(priority = Priority.PRIORITY_10)
    private void divTest() {
        System.out.println("divTest - priority = " + Priority.PRIORITY_10);
        System.out.println("15 / 7 = " + calculator.div(15, 7));
    }

    @Test(priority = Priority.PRIORITY_7)
    public void m1() {
        System.out.println("m1 - priority = " + Priority.PRIORITY_7);
    }

    @Test(priority = Priority.PRIORITY_3)
    private void m2() {
        System.out.println("m2 - priority = " + Priority.PRIORITY_3);
    }


    //@AfterSuite             если раскомментировать не сработает
    public void m3() {
        System.out.println("m3 - after suite");
    }

    //@BeforeSuite             если раскомментировать не сработает
    public void m4() {
        System.out.println("m4 - after suite");
    }
}
