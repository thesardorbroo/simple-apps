package uz.sardorbroo.apartments.service.impl;

import uz.sardorbroo.apartments.domain.Apartment;
import uz.sardorbroo.apartments.service.ApartmentStorageService;
import uz.sardorbroo.apartments.service.utils.ApartmentConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ApartmentFileStorageServiceImpl implements ApartmentStorageService {
    private final static Logger LOGGER = Logger.getGlobal();
    private final Set<String> SUPPORTED_EXTENSIONS;
    private final File storage;

    public ApartmentFileStorageServiceImpl(String filename, Set<String> supportedExtensions) {
        SUPPORTED_EXTENSIONS = supportedExtensions;
        validateFileName(filename);
        this.storage = new File(filename);
        initializeStorage();
    }

    @Override
    public void write(List<Apartment> apartments) {

        if (Objects.isNull(apartments) || apartments.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument is passed! Apartments must not be empty!");
        }

        BufferedWriter writer = getWriter();
        try {

            for (Apartment a : apartments) {
                writer.write(ApartmentConverter.convert(a));
                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> readAll() {
        List<String> allLines = new ArrayList<>();

        try {
            BufferedReader reader = getReader();

            while (reader.ready()) {
                allLines.add(reader.readLine());
            }

            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while reading file! Exception: " + e.getMessage());
        }

        return allLines;
    }

    private void initializeStorage() {
        LOGGER.info("Apartment storage is creating...");

        if (!storage.exists()) {
            try {
                storage.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Exception while creating new file! Exception: " + e.getMessage());
            }
        }

        LOGGER.info("Apartment storage is created as file. File name is " + storage.getName());
    }

    private BufferedReader getReader() {
        try {
            return new BufferedReader(new FileReader(storage));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedWriter getWriter() {
        try {
            return new BufferedWriter(new FileWriter(storage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateFileName(String filename) {
        List<Constraint> constraints = filenameConstraints();

        for (Constraint constraint : constraints) {
            if (constraint.constraint.test(filename)) {
                throw new IllegalArgumentException(constraint.errorMessage);
            }
        }
    }

    private List<Constraint> filenameConstraints() {
        return List.of(
                new Constraint((Objects::isNull), "File name must not be null!"),
                new Constraint((String::isBlank), "Invalid file name!"),
                new Constraint(
                        (filename -> SUPPORTED_EXTENSIONS.stream()
                                .filter(filename::endsWith)
                                .findFirst()
                                .isEmpty()),
                        "File name should finish with supported extensions! Extensions: " + SUPPORTED_EXTENSIONS)
        );
    }

    private record Constraint(Predicate<String> constraint, String errorMessage) {
    }
}
