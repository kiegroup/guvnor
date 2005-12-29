package org.drools.repository.db;

import org.drools.repository.Repository;
import org.drools.repository.RuleDef;
import org.hibernate.Session;



public class RepositoryImpl implements Repository {

    public RuleDef addNewRule(String ruleName, String content, String comment) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.beginTransaction();

        RuleDef ruleDef = new RuleDef();
        ruleDef.setContent(content);
        ruleDef.setName(ruleName);

        session.save(ruleDef);

        session.getTransaction().commit();
        return ruleDef;
    }
    
}
