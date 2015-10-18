package com.stockexit.net;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Newsline")
public class Newsline implements Serializable {
	private static final long serialVersionUID = -746912531273686521L;

	@Id
	@Column(name = "Symbol")
    private String symbol;
    
	@Column(name = "Line")
    private String line;
	
	@Column(name = "Subject")
    private String subject;
    

	public Newsline() {
	}


	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}

	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
