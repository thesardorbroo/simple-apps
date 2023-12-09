package uz.sardorbroo.apartments;

import uz.sardorbroo.apartments.service.ApartmentManagerService;
import uz.sardorbroo.apartments.service.ApartmentPrinterService;
import uz.sardorbroo.apartments.service.ApartmentStorageManagerService;
import uz.sardorbroo.apartments.service.ApartmentStorageService;
import uz.sardorbroo.apartments.service.enumeration.Sort;
import uz.sardorbroo.apartments.service.impl.ApartmentFileStorageServiceImpl;
import uz.sardorbroo.apartments.service.impl.ApartmentManagerServiceImpl;
import uz.sardorbroo.apartments.service.impl.ApartmentPrinterServiceImpl;
import uz.sardorbroo.apartments.service.impl.ApartmentStorageManagerServiceImpl;
import uz.sardorbroo.apartments.service.utils.ScannerUtils;

import java.util.Scanner;
import java.util.Set;

public class ApartmentApplication {
    private final static Scanner SCANNER = new Scanner(System.in);
    private final static String FILENAME = "apartment.txt";
    private final static Set<String> SUPPORTED_EXTENSIONS = Set.of(".txt", ".json", ".pdf");

    public static void main(String[] args) {

        final ApartmentStorageService storageService = new ApartmentFileStorageServiceImpl(FILENAME, SUPPORTED_EXTENSIONS);
        final ApartmentStorageManagerService storageManager = new ApartmentStorageManagerServiceImpl(storageService);
        final ApartmentPrinterService printer = new ApartmentPrinterServiceImpl();
        final ApartmentManagerService manager = new ApartmentManagerServiceImpl(storageManager, printer);

        String saveApartment = "Save new apartment data";
        String updateApartment = "Update data apartment";
        String getApartment = "Get data of apartment";
        String deleteApartment = "Delete apartment data";
        String exit = "Exit!";

        String menu = String.format("1. %s\n2. %s\n3. %s\n4. %s\n5. %s", saveApartment, getApartment, updateApartment, deleteApartment, exit);

        printer.print(" ++++++++++++++ Welcome to Apartment manager application ++++++++++++++ ", System.out::println);
        while (true) {
            printer.print("\n|       Main menu       |\n");
            printer.print("\n" + menu + "\n");

            int action = ScannerUtils.scan(SCANNER::nextInt, 0);

            if (action == 1) {
                manager.create();
            } else if (action == 2) {
                manager.get();
            } else if (action == 3) {
                manager.update();
            } else if (action == 4) {
                manager.delete();
            } else if (action == 5) {
                storageManager.sortBy(Sort.ASC);
                break;
            } else {
                printer.print("Wrong action!");
                break;
            }
        }

        printer.print(" ++++++++++++++ Exited ++++++++++++++ ", System.out::println);
        System.exit(0);
    }
}
