package apm.db;

import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public enum SessionFactory {
	Factory;
	private org.hibernate.SessionFactory sessionFactory;
	
	SessionFactory(){
		/*cfg = new Configuration().configure();
		srb = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
		StandardServiceRegistry sr = srb.build();
		sessionFactory = cfg.buildSessionFactory(sr); */
		
		
		
		StandardServiceRegistry  serviceRegistry=new StandardServiceRegistryBuilder().configure().build(); 
        sessionFactory=new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
	}
	public static Session getSession(){
		Session session = Factory.sessionFactory.openSession();
		return session;
	}
}
