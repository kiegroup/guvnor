package org.kie.guvnor.commons.service.source;

import org.kie.commons.java.nio.file.Path;

/**
 * Base implementation of all SourceServices
 */
public abstract class BaseSourceService<T>
        implements SourceService<T> {

    private final String prefix;

    protected BaseSourceService(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean accepts(final Path path) {
        final String pattern = getPattern();
        final String uri = path.toUri().toString();
        return uri.substring(uri.length() - pattern.length()).equals(pattern);
    }

    protected String stripProjectPrefix(final Path path) {
        final String uri = path.toUri().toString();
        final int prefixIndex = uri.indexOf(prefix);
        return uri.substring(prefixIndex);
    }

    protected String returnPackageDeclaration(final Path path) {
        String projectPrefix = stripProjectPrefix(path);
        int fileNameStartsFrom = projectPrefix.lastIndexOf('/');
        if (isThisFileInTheDefaultPackage(fileNameStartsFrom)) {
            return "";
        } else {
            return "package " + projectPrefix.substring(prefix.length() + 1, fileNameStartsFrom).replace('/', '.');
        }
    }

    private boolean isThisFileInTheDefaultPackage(int fileNameStartsFrom) {
        return prefix.length() + 1 >= fileNameStartsFrom;
    }

    protected String correctFileName(final String path,
                                     String requiredFileExtension) {
        if (!requiredFileExtension.startsWith(".")) {
            requiredFileExtension = "." + requiredFileExtension;
        }
        return path.replace(getPattern(),
                requiredFileExtension);
    }

}
