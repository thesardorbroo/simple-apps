package uz.sardorbroo.apartments.service.impl;

import uz.sardorbroo.apartments.domain.Apartment;
import uz.sardorbroo.apartments.service.ApartmentPrinterService;

import java.util.List;
import java.util.function.Consumer;

public class ApartmentPrinterServiceImpl implements ApartmentPrinterService {

    private static final String HEADER_FOOTER =
            "=======================================================================";
    private static final String HEADER_FOOTER_4_SINGLE_APARTMENT =
            "==============";

    public void print(List<Apartment> apartments) {
        System.out.println(HEADER_FOOTER);
        System.out.printf("| %-10s |%n", "All apartments");
        System.out.println(HEADER_FOOTER);
        System.out.printf("| %-1s | %-15s | %-15s | %-4s | %-6s | %-6s | |%n", "ID", "CITY", "REGION", "TYPE", "ROOMS", "PRICE");
        System.out.println(HEADER_FOOTER);

        for (Apartment apartment : apartments) {
            System.out.printf("| %d | %-15s | %-15s | %-4s | %6d | %6d | |%n",
                    apartment.getId(), apartment.getCity(), apartment.getRegion(), apartment.getType(), apartment.getRoomsCount(), apartment.getPrice());
        }

        System.out.println(HEADER_FOOTER);
    }

    public void print(Apartment apartment) {

        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-10s |%n", "Apartment");
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %3d |%n", "ID", apartment.getId());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %-3s |%n", "CITY", apartment.getCity());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %-3s |%n", "REGION", apartment.getRegion());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %-3s |%n", "TYPE", apartment.getType());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %3d |%n", "ROOMS", apartment.getRoomsCount());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
        System.out.printf("| %-2s | %3d |%n", "PRICE", apartment.getPrice());
        System.out.println(HEADER_FOOTER_4_SINGLE_APARTMENT);
    }

    @Override
    public void print(String message) {
        print(message, System.out::print);
    }

    @Override
    public void print(String message, Consumer<String> consumer) {
        consumer.accept(message);
    }
}
