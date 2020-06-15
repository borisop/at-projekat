package model;

import java.io.Serializable;

public interface Agent extends Serializable{
	String PROJEKAT_MODULE = "projekat-jar";
	String PROJEKAT_EAR = "projekat-ear";
	String PROJEKAT_WAR = "projekat-war";
	
	void init(AID aid);
	
	void handleMessage(ACLMessage aclMessage);
	
	void stop();
	
	AID getAID();
	
}
