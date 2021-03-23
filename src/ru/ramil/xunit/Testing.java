package ru.ramil.xunit;

import ru.ramil.xunit.Annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class Testing {

    /**
     * Запуск тестовых методов из класса className
     * @param className строка содержащая полностью квалифицированное имя класса, в котором написаны тестовые методы
     */
    public static void start(String className) {
        try {
            Class testClass = Class.forName(className);
            start(testClass);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Запуск тестовых методов из класса testClass
     * @param testClass объект типа Class класса, в котором написаны тестовые методы
     */
    public static void start(Class testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        checkMethods(methods);
        Method methodBeforeSuite = null;
        Method methodAfterSuite = null;
        List<Method> list = new ArrayList<>();
        for(Method m : methods) {
            if(m.getAnnotation(BeforeSuite.class) != null) {
                methodBeforeSuite = m;
            } else if(m.getAnnotation(AfterSuite.class) != null) {
                methodAfterSuite = m;
            } else if (m.getAnnotation(Test.class) != null) {
                list.add(m);
            }
        }
        Comparator<Method> comparator = Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority().ordinal());
        Collections.sort(list, comparator.reversed());
        startMethods(methodBeforeSuite, list, methodAfterSuite, testClass);
    }

    /**
     * Проверка методов отмеченных аннотациями из пакета ru.ramil.xunit.Annotation
     * Ограничения для класса, содержащего тестовые методы:
     * 1. В классе может быть только один метод, отмеченный аннотацией @BeforeSuite
     * 2. В классе может быть только один метод, отмеченный аннотацией @AfterSuite
     * 3. Один и тот же метод не может содержать одновременно какую-либо комбинацию
     *    из аннотаций @BeforeSuite, @AfterSuite и @Test
     * @param methods
     * @throws RuntimeException если:
     *         1. В классе присутствует больше одного метода, отмеченного аннотацией @BeforeSuite
     *         2. В классе присутствует больше одного метода, отмеченного аннотацией @AfterSuite
     *         3. В классе есть метод, содержащий одновременно какую-либо комбинацию
     *            из аннотаций @BeforeSuite, @AfterSuite и @Test
     */
    private static void checkMethods(Method[] methods) throws RuntimeException {
        boolean isAlreadyExistBeforeMethod = false;
        boolean isAlreadyExistAfterMethod = false;
        for(Method m : methods) {
            if(intersectsAnnotations(m, BeforeSuite.class, Test.class, AfterSuite.class)) {
                throw new RuntimeException("Метод не должен содержать одновременно какую-либо " +
                        "комбинацию из аннотаций @BeforeSuite, @Test и @AfterSuite");
            }
            if(m.getAnnotation(BeforeSuite.class) != null) {
                if(!isAlreadyExistBeforeMethod) {
                    isAlreadyExistBeforeMethod = true;
                } else {
                    throw new RuntimeException("Аннотацией @BeforeSuite может быть отмечен только один метод в классе");
                }
            } else if(m.getAnnotation(AfterSuite.class) != null) {
                if(!isAlreadyExistAfterMethod) {
                    isAlreadyExistAfterMethod = true;
                } else {
                    throw new RuntimeException("Аннотацией @AfterSuite может быть отмечен только один метод в классе");
                }
            }
        }
    }

    /**
     * Проверка содержит ли метод какую-либо комбинацию из аннотаций передаваемых в параметре annotations
     * @param method проверяемый метод
     * @param annotations список аннотаций, которыми метод должны быть отмечен только один раз
     * @return true - если аннотации из списка annotations появляются в методе больше одного раза,
     *         false - в противном случае
     */
    private static boolean intersectsAnnotations(Method method, Class... annotations) {
        int count = 0;
        for(Class annoClass : annotations) {
            if(method.getAnnotation(annoClass) != null) {
                count++;
                if(count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Запускает методы на выполнение. Порядок выполнения:
     * 1. Выполняется метод, помеченный аннотацией @BeforeSuite, если он присутствует в классе.
     * 2. Выполняются все методы, помеченные аннотацией @Test в порядке их приоритета. Наивысший приоритет имеют
     *    методы с приоритетом Priority.PRIORITY_10.
     * 3. Выполняется метод, помеченный аннотацией @AfterSuite, если он присутствует в классе.
     * @param before метод, помеченный аннотацией @BeforeSuite, который должен выполняться раньше всех
     * @param tests отсортированный по приоритету список методов, помеченных аннотацией @Test
     * @param after метод, помеченный аннотацией @AfterSuite, который должен выполняться раньше всех
     * @param testClass объект типа Class, представляющий класс, в котором написаны тестовые методы
     */
    private static void startMethods(Method before, List<Method> tests, Method after, Class testClass) {
        try {
            Object testing = testClass.newInstance();
            if(before != null) {
                invokeMethod(before, testing);
            }
            for (Method m : tests) {
                invokeMethod(m, testing);
            }
            if(after != null) {
                invokeMethod(after, testing);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Запускает на выполнение отдельный метод
     * @param method метод, запускаемый на выполнение
     * @param object объект, для которого вызывается метод
     * @throws IllegalAccessException может возникнуть при вызове method.invoke()
     * @throws InvocationTargetException может возникнуть при вызове method.invoke()
     */
    private static void invokeMethod(Method method, Object object)
            throws IllegalAccessException, InvocationTargetException {
        method.setAccessible(true);
        method.invoke(object);
    }
}
