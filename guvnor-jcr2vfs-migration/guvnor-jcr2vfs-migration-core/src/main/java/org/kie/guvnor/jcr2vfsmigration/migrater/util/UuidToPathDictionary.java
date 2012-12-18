package org.kie.guvnor.jcr2vfsmigration.migrater.util;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class UuidToPathDictionary {

    private Map<String, Path> uuidToPathMap = new HashMap<String, Path>();
    private Map<Path, String> pathToUuidMap = new HashMap<Path, String>();

    public void register(String uuid, Path path) {
        if (uuidToPathMap.containsKey(uuid)) {
            throw new IllegalArgumentException("The uuid (" + uuid + ") cannot be registered for path ("
                    + path + ") because it has already been registered once. Last time it was for path ("
                    + uuidToPathMap.get(uuid) + "), but even if it's equal, it should never be registered twice.");
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
