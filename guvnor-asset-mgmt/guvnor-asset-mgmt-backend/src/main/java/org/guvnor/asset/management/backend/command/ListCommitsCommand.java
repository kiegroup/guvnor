package org.guvnor.asset.management.backend.command;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.spi.BeanManager;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.guvnor.asset.management.backend.AssetManagementRuntimeException;
import org.guvnor.asset.management.backend.model.CommitInfo;
import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.NamedLiteral;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

public class ListCommitsCommand extends AbstractCommand {
	
	private static final Logger logger = LoggerFactory.getLogger(ListCommitsCommand.class);
	// remove dot files from sorted commits per file
	private static final String DEFAULT_FILER_REGEX = ".*\\/\\..*";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public ExecutionResults execute(CommandContext commandContext) throws Exception {
        try {
            ExecutionResults results = new ExecutionResults();
            String gitRepo = (String) getParameter(commandContext, "GitRepository");
            String branchName = (String) getParameter(commandContext, "BranchName");
            String compareToBranchName = (String) getParameter(commandContext, "CompareToBranchName");
            String fromDate = (String) getParameter(commandContext, "FromDate");

            Date startCommitDate = null;
            if (fromDate != null) {
                startCommitDate = dateFormat.parse(fromDate);
            }

            Set<String> existingCommits = new LinkedHashSet<String>();

            BeanManager beanManager = CDIUtils.lookUpBeanManager(commandContext);
            logger.debug("BeanManager " + beanManager);


            IOService ioService = CDIUtils.createBean(IOService.class, beanManager, new NamedLiteral("ioStrategy"));
            logger.debug("IoService " + ioService);

            Path branchPath = ioService.get(URI.create("git://" + branchName + "@" + gitRepo));

            if (compareToBranchName != null) {
                Path compareToBranchPath = ioService.get(URI.create("git://" + compareToBranchName + "@" + gitRepo));
                VersionAttributeView compareView = ioService.getFileAttributeView( compareToBranchPath, VersionAttributeView.class );
                List<VersionRecord> compareLogs = compareView.readAttributes().history().records();

                for (VersionRecord ccommit : compareLogs) {
                    if (startCommitDate != null && startCommitDate.after(ccommit.date())) {
                        break;
                    }
                    existingCommits.add(ccommit.id());
                }
            }

            VersionAttributeView vinit = ioService.getFileAttributeView( branchPath, VersionAttributeView.class );
            List<VersionRecord> logs = vinit.readAttributes().history().records();

            List<CommitInfo> commits = new ArrayList<CommitInfo>();
            JGitFileSystem fs = (JGitFileSystem)ioService.getFileSystem(branchPath.toUri());
            Collections.reverse(logs);

            for (VersionRecord commit : logs) {

                // check if there are already commits in compare to branch
                if (existingCommits.contains(commit.id())) {
                    continue;
                }
                String shortMessage = commit.comment();
                Date commitDate = commit.date();

                if (startCommitDate != null && startCommitDate.after(commitDate)) {
                    break;
                }

                List<String> files = getFilesInCommit(fs.gitRepo().getRepository(), JGitUtil.resolveObjectId(fs.gitRepo(), commit.id()));
                CommitInfo commitInfo = new CommitInfo(commit.id(), shortMessage, commit.author(), commitDate, files);
                commits.add(commitInfo);
                logger.debug("Found commit {}", commitInfo);

            }



            String commitsString = dumpToStringCommit(commits);
            Map<String, List<String>> commitsPerFileMap = sortByFileName(commits);

            results.setData("Commits", commits);
            results.setData("CommitsPerFile", commitsPerFileMap);
            results.setData("CommitsPerFileString", dumpToStringFiles(commitsPerFileMap.keySet()));
            results.setData("CommitsPerFileMap", commitsPerFileMap);
            results.setData("CommitsString", commitsString);

            return results;
        } catch (Throwable e) {
            throw new AssetManagementRuntimeException(e);
        }
    }
    
    protected String dumpToStringCommit(List<CommitInfo> commits) {
    	StringBuilder commitString = new StringBuilder();
    	
    	for (CommitInfo commit : commits) {
    		commitString.append(commit.getCommitId()).append(",");
    	}
    	
    	return commitString.toString();
    }
    
    protected String dumpToStringFiles(Set<String> files) {
    	StringBuilder filesString = new StringBuilder();
    	for (String file : files) {
    		filesString.append(file).append(",");
    	}
    	return filesString.toString();
    }
    
    protected Map<String, List<String>> sortByFileName(final List<CommitInfo> commits) {
    	Map<String, List<String>> sorted = new HashMap<String, List<String>>();
    	
    	if (commits == null) {
    		return sorted;
    	}

        for (CommitInfo commit : commits) {
            List<String> files = commit.getFiles();
            if (files == null) {
                continue;
            }
            for (String file : files) {
                if (!file.matches(DEFAULT_FILER_REGEX)) {
                    List<String> commitsPerFile = sorted.get(file);
                    if (commitsPerFile == null) {
                        commitsPerFile = new ArrayList<String>();
                        sorted.put(file, commitsPerFile);
                    }

                    commitsPerFile.add(commit.getCommitId());
                }
            }
        }
    	
    	return sorted;
    }


    protected List<String> getFilesInCommit(Repository repository, ObjectId commitId) {
        List<String> list = new ArrayList<String>();

        RevWalk rw = new RevWalk(repository);

        try {
            RevCommit commit = rw.parseCommit(commitId);
            if (commit == null) {
                return list;
            }

            if (commit.getParentCount() == 0) {
                TreeWalk tw = new TreeWalk(repository);
                tw.reset();
                tw.setRecursive(true);
                tw.addTree(commit.getTree());
                while (tw.next()) {
                    list.add(tw.getPathString());
                }
                tw.release();
            } else {
                RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(NullOutputStream.INSTANCE);
                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                for (DiffEntry diff : diffs) {

                    if (diff.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
                        list.add(diff.getOldPath());
                    } else if (diff.getChangeType().equals(DiffEntry.ChangeType.RENAME)) {
                        list.add(diff.getNewPath());
                    } else {
                        list.add(diff.getNewPath());
                    }

                }
            }
        } catch (Throwable t) {
            logger.error("Unable to determine files in commit due to {} in repository {}", t, repository);
        } finally {
            rw.dispose();
        }
        return list;
    }
}
