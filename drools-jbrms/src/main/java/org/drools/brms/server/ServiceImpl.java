package org.drools.brms.server;

import java.util.List;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.repository.CategoryItem;
import org.drools.repository.RulesRepository;

/**
 * This contains the "glue" between the servlet implementation
 * (which is GWT specific) and the repository api.
 * This is mainly to make things more testable.
 * 
 * It will be "injected" a RulesRepository implementation.
 * 
 * @author Michael Neale
 */
public class ServiceImpl implements RepositoryService {

    private RulesRepository repo;

    public ServiceImpl(RulesRepository repository) {
        this.repo = repository;
    }
    
    public String[] loadChildCategories(String categoryPath) {

        CategoryItem item = repo.loadCategory( categoryPath );
        List children = item.getChildTags();
        String[] list = new String[children.size()];
        for ( int i = 0; i < list.length; i++ ) {
            list[i] = ((CategoryItem) children.get( i )).getName();
        }
        return list;

    }

    public Boolean createCategory(String path,
                                  String name,
                                  String description) {
        
        if (path == null || "".equals(path)) {
            path = "/";
        }
        CategoryItem item = repo.loadCategory( path );
        item.addCategory( name, description );
        return Boolean.TRUE;
    }

    public String[][] loadRuleListForCategories(String categoryPath,
                                                String status) {
        // TODO Auto-generated method stub
        return null;
    }

    public TableConfig loadTableConfig(String listName) {
        // TODO Auto-generated method stub
        return null;
    }

}
