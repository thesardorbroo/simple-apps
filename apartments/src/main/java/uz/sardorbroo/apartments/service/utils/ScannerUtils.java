package uz.sardorbroo.apartments.service.utils;

import java.util.InputMismatchException;
import java.util.function.Supplier;

public class ScannerUtils {

    public static <T> T scan(Supplier<T> scanner, T defaultValue) {

        try {
            return scanner.get();
        } catch (InputMismatchException e) {
            System.err.println("Mismatched value! Please try again!");
            return defaultValue;
        }
    }
}
