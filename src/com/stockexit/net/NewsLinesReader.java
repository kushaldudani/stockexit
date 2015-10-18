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


public class NewsLinesReader {
	
	private Session session;
	
	public void openSession(){
		try{
			Logger log = Logger.getLogger("org.hibernate");
			log.setLevel(Level.WARNING);
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
			session = sessionFactory.openSession();
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "NewsLinesReader opensession failed", e);
		}
	}
	
	public List<Newsline> getNewsLines(String stock, String date) {
		List<Newsline> newslines = new ArrayList<Newsline>();
		try{
		session.beginTransaction();
		
		String hql = "Select * from Newsline where Symbol like '"+stock+"-"+date+"-%'";
	    SQLQuery query = session.createSQLQuery(hql);
	    query.addEntity(Newsline.class);
	    List<Object> objects = query.list();
	    for(Object o : objects){
	    	Newsline ne = (Newsline) o;
	    	newslines.add(ne);
	    }
	    
		session.getTransaction().commit();
		}catch(Exception e){
			session.getTransaction().rollback();
			LoggerUtil.getLogger().log(Level.SEVERE, "NewsLinesReader getNewsLines failed", e);
			return null;
		}
		return newslines;
	}
	
	public void closeSession(){
		try{
			session.close();
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "NewsLinesReader closeSession failed", e);
		}
	}

}
