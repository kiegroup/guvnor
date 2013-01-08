package org.kie.guvnor.jcr2vfsmigration.migrater.util;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.Module;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

/**
 * Generates a Path for every object that needs to be migrated.
 * Guarantees uniqueness. Supports look ups.
 */
@ApplicationScoped
public class MigrationPathManager {

    private static final String VFS_PATH_NAMESPACE = "default://guvnor-jcr2vfs-migration/";

    private Map<String, Path> uuidToPathMap = new HashMap<String, Path>();
    private Map<Path, String> pathToUuidMap = new HashMap<Path, String>();

    // Generate methods

    public Path generatePathForModule(Module jcrModule) {
        Path path = PathFactory.newPath(escapePathEntry(jcrModule.getName()),
                VFS_PATH_NAMESPACE
                        + escapePathEntry(jcrModule.getName()) + "/");
        register(jcrModule.getUuid(), path);
        return path;
    }

    public Path generatePathForAsset(Module jcrModule, Asset jcrAsset) {
        Path path = PathFactory.newPath(escapePathEntry(jcrAsset.getName()) + "." + jcrAsset.getFormat(),
                VFS_PATH_NAMESPACE
                        + escapePathEntry(jcrModule.getName()) + "/"
                        + escapePathEntry(jcrAsset.getName()) + "/");
        register(jcrAsset.getUuid(), path);
        return path;
    }

    // Helper methods

    public String escapePathEntry(String pathEntry) {
        // VFS doesn't support /'s in the path entries
        pathEntry = pathEntry.replaceAll("/", " slash ");
        // TODO Once porcelli has a list of all illegal and escaped characters in PathEntry, deal with them here
        return pathEntry;
    }

    protected void register(String uuid, Path path) {
        if (uuidToPathMap.containsKey(uuid)) {
            throw new IllegalArgumentException("The uuid (" + uuid + ") cannot be registered for path ("
                    + path + ") because it has already been registered once. Last time it was for path ("
                    + uuidToPathMap.get(uuid) + "), but even if it's equal, it should never be registered twice.");
        }
        if (pathToUuidMap.containsKey(path)) {
            throw new IllegalArgumentException("The path (" + path + ") cannot be registered from uuid ("
                    + uuid + ") because it has already been registered once. Last time it was for uuid ("
                    + pathToUuidMap.get(path) + "), but even if it's equal, it should never be registered twice.");
        }
        uuidToPathMap.put(uuid, path);
        pathToUuidMap.put(path, uuid);
    }

    public Path getPath(String uuid) {
        return uuidToPathMap.get(uuid);
    }

    public String getUuid(Path path) {
        return pathToUuidMap.get(path);
    }

}
