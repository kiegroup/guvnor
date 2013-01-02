package org.kie.guvnor.datamodel.backend.server;

import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * Define operations of a DataModelOracle cache
 */
public interface DataModelOracleCache {

    /**
     * Retrieve the DataModelOracle for the specified Project path. The Path should resolve to a Project.
     * @param project The Path to the Project
     * @return DataModelOracle for the Project
     */
    DataModelOracle getDataModelOracle( Path project );

    /**
     * Set the DataModelOracle for the specified Project path. The Path should resolve to a Project.
     * @param project The Path to the Project
     * @param oracle The DataModelOracle for the Project
     */
    void setDataModelOracle( Path project,
                             DataModelOracle oracle );

    /**
     * Invalidate the entire cache
     */
    void invalidateCache();

    /**
     * Invalidate the cache for a specific Project path. The path should resolve to a Project.
     * @param project The Path to the Project
     */
    void invalidateCache( Path project );
}
