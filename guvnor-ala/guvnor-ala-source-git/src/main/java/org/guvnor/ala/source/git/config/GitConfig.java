package org.guvnor.ala.source.git.config;

import org.guvnor.ala.config.SourceConfig;

/*
 * Interface that represent the specific of the Git Configuration data
*/
public interface GitConfig extends SourceConfig {

    /*
     * Get the Repository Name
     * @return String with the repository name if provided, if not it will default to 
     *  resolve the expresion ${input.repo-name} from the Pipeline Input map
    */
    default String getRepoName() {
        return "${input.repo-name}";
    }

    /*
    * Get String to find out if we need to create the repo or not
    * @return String true/false
    *  resolve the expresion ${input.create-repo} from the Pipeline Input map
   */
    default String getCreateRepo() {
        return "${input.create-repo}";
    }

    /*
     * Get the Origin address
     * @return String with the Origin name if provided, if not it will default to 
     *  resolve the expresion ${input.origin} from the Pipeline Input map
    */
    default String getOrigin() {
        return "${input.origin}";
    }

    /*
     * Get the Branch Name of the repository that will be used
     * @return String with the Branch name if provided, if not it will default to 
     *  resolve the expresion ${input.branch} from the Pipeline Input map
    */
    default String getBranch() {
        return "${input.branch}";
    }

    /*
    * Get the OutPath where the repo is going to be stored
    * @return String with the OutPath if provided, if not it will default to
    *  resolve the expresion ${input.out-dir} from the Pipeline Input map
   */
    default String getOutPath() {
        return "${input.out-dir}";
    }
}
