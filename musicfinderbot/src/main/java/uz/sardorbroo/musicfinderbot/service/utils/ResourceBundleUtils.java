package uz.sardorbroo.musicfinderbot.service.utils;

import jakarta.ws.rs.NotFoundException;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ResourceBundleUtils {

    private static final String BUNDLE_BASE_NAME = "messages";

    public static Optional<ResourceBundle> getBundle(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);

        return Objects.isNull(bundle) ? Optional.empty() : Optional.of(bundle);
    }

    public static ResourceBundle getBundle(String languageCode) {
        return getBundleOrError(new Locale(languageCode));
    }

    /**
     * @param locale
     * @return ResourceBundle
     * @throws NotFoundException
     */
    public static ResourceBundle getBundleOrError(Locale locale) {
        Optional<ResourceBundle> bundleOptional = getBundle(locale);

        return bundleOptional.orElseThrow(() ->
                new NotFoundException("ResourceBundle is not found! Locale: " + locale));
    }
}
