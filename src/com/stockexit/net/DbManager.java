package com.stockexit.net;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.stockexit.util.HibernateUtil;


public class DbManager {
	
private Session session;
	
	public void openSession(){
		try{
			Logger log = Logger.getLogger("org.hibernate");
			log.setLevel(Level.WARNING);
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
			session = sessionFactory.openSession();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void insertOrUpdate(BuySell buysell){
		try{
			session.beginTransaction();  
        
			
			session.saveOrUpdate(buysell);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			session.getTransaction().rollback();
		}
	}
	
	public List<BuySell> getBusSells() {
		List<BuySell> records = new ArrayList<BuySell>();
		try{
		session.beginTransaction();
		
		String hql = "Select * from BuySell where Exited = 0 and Hasbudget = 1";
	    SQLQuery query = session.createSQLQuery(hql);
	    query.addEntity(BuySell.class);
	    List<Object> objects = query.list();
	    for(Object o : objects){
	    	BuySell pr = (BuySell) o;
	    	records.add(pr);
	    }
	    
		session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			session.getTransaction().rollback();
			return null;
		}
		return records;
	}
	
	
	public void closeSession(){
		try{
			session.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
