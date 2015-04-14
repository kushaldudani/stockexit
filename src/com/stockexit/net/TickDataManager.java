package com.stockexit.net;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.stockexit.util.HibernateUtil;
import com.stockexit.util.LoggerUtil;



public class TickDataManager {
	
	
private Session session;
	
	public void openSession(){
		try{
			Logger log = Logger.getLogger("org.hibernate");
			log.setLevel(Level.WARNING);
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
			session = sessionFactory.openSession();
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "TickDataManager opensession failed", e);
		}
	}
	
	
	
	public void closeSession(){
		try{
			session.close();
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "TickDataManager closeSession failed", e);
		}
	}
	
	
	
	public List<TickData> getTickDatas(String stock) {
		List<TickData> tickdatas = new ArrayList<TickData>();
		try{
		session.beginTransaction();
		
		String hql = "Select * from TickData where Symbol like '"+stock+"-%' order by Symbol asc";
	    SQLQuery query = session.createSQLQuery(hql);
	    query.addEntity(TickData.class);
	    List<Object> objects = query.list();
	    for(Object o : objects){
	    	TickData td = (TickData) o;
	    	tickdatas.add(td);
	    }
	    
		session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			LoggerUtil.getLogger().log(Level.SEVERE, "TickDataManager getTickDatas failed", e);
			System.exit(1);
			return null;
		}
		return tickdatas;
	}


}
