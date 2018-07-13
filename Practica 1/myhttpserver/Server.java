import java.net.*;

public class Server extends Thread {


	public static void main(String [] args) {
		System.out.println("-- Servidor en funcionamiento --");

		try {
			if (args.length < 3) {
				System.out.println("Indica el puerto de escucha del servidor y/o la dirección del controller y su puerto");
				System.out.println("$./Servidor servidor_puerto controller_ip controller_puerto");
				System.exit (1);
			}

			ServerSocket skServer = new ServerSocket(Integer.parseInt(args[0]));
			
			System.out.println("-- Esperando clientes --");			
			
			while(true) {
				if(ServerThread.getCounter() < 5) {
					Socket skClient = skServer.accept();

					System.out.println("-- Cliente aceptado --");
					
					new ServerThread(skClient, args[1], Integer.parseInt(args[2])).start();

					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
				}
				else
					System.out.println("ESTÁ LLENO");
			}
		}
		catch (Exception e) {
			System.out.println("Arranque del servidor fallido: " + e.toString());
		}		
	}

}
