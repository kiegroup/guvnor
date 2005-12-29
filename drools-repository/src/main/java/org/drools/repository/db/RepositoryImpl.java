package org.drools.repository.db;

import java.util.List;

import org.drools.repository.Repository;
import org.drools.repository.RuleDef;
import org.hibernate.Query;
import org.hibernate.Session;



public class RepositoryImpl implements Repository {

    public RuleDef addNewRule(String ruleName, String content, String comment) {
        Session session = getSession();
        session.beginTransaction();

        RuleDef ruleDef = new RuleDef(ruleName, content);
        session.save(ruleDef);

        session.getTransaction().commit();
        return ruleDef;
    }

    
    public List listRules(boolean head) {
        Session session = getSession();
        session.beginTransaction();
        List results = session
                    .createQuery("from RuleDef where head = :head")
                    .setBoolean("head", head).setMaxResults(1000)
                    .list();        
        session.getTransaction().commit();
        return results;        
    }
    
    private Session getSession(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session;
    }
    
    
}
