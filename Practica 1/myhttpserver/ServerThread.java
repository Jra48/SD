import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
	private static int PORT_CONTROLLER, counter = 0;
	private static String IP_CONTROLLER;
	private Socket skClient;
	private PrintWriter out;

	ServerThread(Socket client, String ip, int port) {
		skClient = client;
		this.IP_CONTROLLER = ip;
		this.PORT_CONTROLLER = port;
	}

	void debug(String mensaje) {
		System.out.println("Mensaje: " + mensaje);
	}

	@Override
	public void run() {
		debug("Sirviendo cliente");

		try{
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(skClient.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(skClient.getOutputStream(),"utf-8"), true);

			String cadena = "";	// cadena donde almacenamos las lineas que leemos
			int i = 0;	// lo usaremos para que cierto codigo solo se ejecute una vez
	 		
			do {
				cadena = fromClient.readLine();

				if(cadena != null) {
					debug("--" + cadena + "-");
	 
		     	if(i == 0) { // la primera linea nos dice que fichero hay que descargar
		        i++;

		        StringTokenizer st = new StringTokenizer(cadena);

						if((st.countTokens() >= 2) && st.nextToken().equals("GET")) {
							String url = st.nextToken(); // se guarda la URL entera
							
							// Se comprueba si se devolverá una página estática o habrá que llamar al controlador
							StringTokenizer sfichero = new StringTokenizer(url, "/");

							if(sfichero.hasMoreTokens()) {
								String first = sfichero.nextToken();

								if(first.equals("controladorSD")) 
									if(sfichero.hasMoreTokens())
										callController(sfichero.nextToken());
									else
										callController("");
								else if(first.equals("index.html") || first.equals("style.css"))
									returnFile(first, 200, "OK");
								else
									returnFile("404.html", 404, "NOT FOUND");
							}
							else
								returnFile("index.html", 200, "OK");
						}
						else
							out.println("HTTP/1.1 405 Method Not Allowed");
					}
		    }
			} while (cadena != null && cadena.length() != 0);

			fromClient.close();
		}
		catch(Exception e){
			debug("Error run: " + e.toString());
    		out.println("HTTP/1.1 400 Bad Request");
    		out.close();
		}

		debug("Hemos terminado");
	}

	public static int getCounter() { return counter; }

	void returnFile(String filename, int code, String description) {
		try {
			out.println("HTTP/1.1" + code + " " + description);
			out.println("Server: Jra48 Server/1.0");
			out.println("Content-Type: text/html");
			out.println("\n");

			BufferedReader buffer = new BufferedReader(new FileReader(new File(filename)));
			String linea = "";

			do {
				linea = buffer.readLine();

				if (linea != null)
					out.println(linea);
			} while (linea != null);

			debug("Fin envio fichero.");

			buffer.close();
		}
		catch(Exception e) {
			debug("Error returnFile: " + e.toString());
		}
	}


	public void callController(String order) {
		String finalOrder = "";
		PrintWriter toController = null;

		try {
			if(!order.equals(""))
				finalOrder = checkOrder(order);
				
			if(!finalOrder.equals("") || order.equals("")) {
				// Conexión del servidor con el controller
				Socket skController = new Socket(IP_CONTROLLER, PORT_CONTROLLER);

				toController = new PrintWriter(new OutputStreamWriter(skController.getOutputStream(),"utf-8"), true);
				BufferedReader fromController = new BufferedReader(new InputStreamReader(skController.getInputStream()));

				// Se comunica con el controller y le pasa la instrucción solicitada
				toController.println(finalOrder);
				String lineRead = "";

				while((lineRead = fromController.readLine()) != null)
					out.println(lineRead);

				fromController.close();
			}
		}
		catch(Exception e) {
			debug("Error callController: " + e.toString());
			returnFile("409.html", 409, "Conflict");
		}
	}

	public String checkOrder(String order) {
		String instructions = "";
		//String auxtemp="valor";

		try {
			Boolean set = false;
			StringTokenizer parts1 = new StringTokenizer(order, "?");

			if(parts1.countTokens() == 2) {
				String data1 = parts1.nextToken(), dataSet = "";
				data1 = data1.toLowerCase();

				StringTokenizer ifset = new StringTokenizer(data1, "=");

				if(ifset.countTokens() == 2) {
					String aux = ifset.nextToken();
					aux = aux.toLowerCase();
					
					if(aux.equals("cambiartemperatura")) {
						dataSet = ifset.nextToken();
                        
						int aux2 = Integer.parseInt(dataSet);
                        
						if(-10 <= aux2 && aux2 <= 45) {
							data1 = "cambiartemperatura";
							set = true;
						}
					}
					else{
						if(aux.equals("cambiarhumedad")){
							dataSet = ifset.nextToken();
                        
							int aux2 = Integer.parseInt(dataSet);
                        
							if(0 <= aux2 && aux2 <= 100) {
								data1 = "cambiarhumedad";
								set = true;
							}
						}
					}
				}
                                
				if(data1.equals("id") || data1.equals("fecha")|| data1.equals("tipo") || data1.equals("valor")
					|| set) {
					
					StringTokenizer parts2 = new StringTokenizer(parts1.nextToken(), "=");
                    
					if(parts2.countTokens() == 2) {
						String dataName = parts2.nextToken();
						dataName = dataName.toLowerCase();
                                                
						String data3 = parts2.nextToken();
						Integer.parseInt(data3);
                                                
						if(dataName.equals("sonda")) {
							instructions = data1 + "," + data3 + "," + dataSet;
						}
						else {
							debug("No es una URL válida");
						}
					}
					else
						debug("No es una URL válida");
				}
				else {
					debug("Error checkOrder: Variable no válida.");
					returnFile("errorvariable.html", 409, "Variable no válida");
				}
			}
			else     
				debug("No es una URL válida");
		}
		catch(Exception e) {
			debug("Error checkOrder: " + e.toString());
		}

		return instructions;
	}

	protected void finalize() throws Throwable {
		counter--;
	}
}
