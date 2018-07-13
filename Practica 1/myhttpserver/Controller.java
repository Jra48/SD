import java.net.*;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Controller extends Thread {

	public static void main(String [] args) {
		System.out.println("-- Controller en funcionamiento --");

		try {
			if (args.length < 1) {
				System.out.println("Indica la dirección y puerto de acceso a la máquina de las sondas");
				System.out.println("$./Controller controller_puerto server_ip");
				System.exit (1);
			}

			ServerSocket skServer = new ServerSocket(Integer.parseInt(args[0]));


			// PARTE RMI
			if(System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());

			//Registry reistrgistry = LocateRegistry.createRegy(1099);
			Registry registry = LocateRegistry.getRegistry(1099);
			RemoteRegisterInterface register = new RemoteRegister(registry);

			Naming.rebind("rmi://localhost:1099/Registrador", register);

			System.out.println("-- Esperando clientes --");			
			
			while(true) {
				if(ServerThread.getCounter() < 5) {
					Socket skClient = skServer.accept();

					System.out.println("-- Cliente aceptado por el Controller --");
					
					new ControllerThread(skClient, args[1]).start();

					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
				}
				else
					System.out.println("ESTÁ LLENO");
			}
		}
		catch (Exception e) {
			System.out.println("Arranque del controller fallido: " + e.toString());
		}		
	}
}
