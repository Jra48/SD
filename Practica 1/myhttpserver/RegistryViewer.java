import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;




public class RegistryViewer {
  public static void main(String args[]){
  	try {
	    Registry registry = LocateRegistry.getRegistry("localhost", 1099);

	    if(registry.list().length == 0)
	    	System.out.println("No hay ningún objeto registrado");
	    else {
	    	System.out.println("Registry: ");
	    	for (String name : registry.list())
	        System.out.println(name);
	    }
	    
	  }
	  catch(Exception e) { }
  }    
}