import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.*;


public class ControllerThread extends Thread {
	private String server_ip;
	private String [] instruction;
	private Socket skClient;
	private PrintWriter out;

	ControllerThread(Socket client, String server_ip) {
		skClient = client;
		this.server_ip = server_ip;

	}

	void debug(String mensaje) {
		System.out.println("Mensaje: " + mensaje);
	}


	public void run() {
		BufferedReader fromServer = null;
		PrintWriter toServer = null;
		String command = null, data = null, numSonda = null, dataSet = null;
		Registry registry = null;
		boolean found = false, delete = false;
		String aux = "";

		try {
			fromServer = new BufferedReader(new InputStreamReader(skClient.getInputStream()));			

			command = fromServer.readLine();

			if(!command.equals("")) {
				StringTokenizer parts = new StringTokenizer(command, ",");
				data = parts.nextToken();
				numSonda = parts.nextToken();

				if(parts.hasMoreTokens())
					dataSet = parts.nextToken();
			}

			if(System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());


			registry = LocateRegistry.getRegistry("localhost", 1099);
			String[] remoteObjNames = registry.list();
			remoteObjNames = Arrays.copyOfRange(remoteObjNames, 1, remoteObjNames.length);
			found = Arrays.asList(remoteObjNames).contains(numSonda); // buscamos el valor, de haberlo, en la lista del registro

			toServer = new PrintWriter(new OutputStreamWriter(skClient.getOutputStream(),"utf-8"), true);

			toServer.println("HTTP/1.0 200 ok");
			toServer.println("Server: Jra48 Server/1.0");
			toServer.println("Content-Type: text/html");
			toServer.println("Content-Length: ");
			toServer.println("\n");


			toServer.println("<html>");
			// HEADER
			toServer.println("<head>");
			toServer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
			toServer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">");
			toServer.println("<link rel=\"shortcut icon\" href=\"data:image/x-icon;\" type=\"image/x-icon\">");
			toServer.println("<title>Control Invernadero</title>");
			toServer.println("</head>");

			// BODY
			toServer.println("<body>");
			toServer.println("<section id=\"main\">");
			toServer.println("<section id=\"container\">");
			toServer.println("<section id=\"contforms\">");
			toServer.println("<section id=\"conttitle\">");
			toServer.println("<p id=\"title\">CONTROLADOR");

			if(found)
				toServer.println(" - Sonda " + numSonda);

			toServer.println("</p>");
			toServer.println("</section>"); //conttitle
                        
			if(command.equals("")) {
				toServer.println("<div id=\"contSensors\">");

				if(remoteObjNames.length > 0) {
					SensorInterface sensor = null;

					for(String numSensor : remoteObjNames) {
						try {
							sensor = (SensorInterface)registry.lookup(numSensor);

							sensor.getID();
							toServer.print("<div class=\"sonda");

							/*if(Integer.parseInt(sensor.getID()) < 30)
								toServer.print(" libre");
							else
								toServer.print(" ocupada");
							*/
							toServer.print("\">");
				        	toServer.println("<div class=\"sonda-title\">");
							toServer.println(sensor.getSensor());
							toServer.println("</div>");

								toServer.println("<div class=\"sonda-content\">");
									toServer.println("<span> ID: <span class=\"data\">" + sensor.getID() + "</span></span>");
									toServer.println("<span> Fecha: <span class=\"data\">" + sensor.getFecha() + "</span></span>");
									toServer.println("<span> Tipo: <span class=\"data\">" + sensor.getTipo() + "</span></span>");
// 					
									toServer.println("<div class=\"dato\">");
									toServer.println("<span> Valor: </span>");
									
									toServer.println("<input type=\"number\" placeholder="+ sensor.getValor() +" id=\"number" + sensor.getSensor() + "\"  onKeyUp=\"changeAction(" + sensor.getSensor() + "); return false;\" min=\"-50\" max=\"50\" required style=\"border: 1px solid #fff; width: 100px;\">");
									toServer.println("<a href=\"#\" id=\"link" + sensor.getSensor() + "\"> Cambiar</a>");
									
									toServer.println("</div>");
								toServer.println("</div>");
							
							toServer.println("</div>");
							aux = sensor.getTipo();
						}
						catch(Exception e) {
							debug("No se puede conectar con la sonda.");
							delete = true;

							try {
								registry.unbind(numSensor);
							}
							catch(Exception ne) {
								debug("No se pudo borrar la sonda: " + ne.toString());
							}
						}
					}

					if(delete)
						toServer.println("<br><p class=\"nosondas\">Algunas sondas se han eliminado al no poder contactar con ellas</p>");
				}
				else
					toServer.println("<p class=\"nosondas\">No hay sondas registradas</p>");


				toServer.println("</div>");	
			}
			else {
				toServer.println("<section class=\"contprobe\">");
				toServer.println("<div id=\"contValue\">");

				if(found) {
					SensorInterface sensor = (SensorInterface)registry.lookup(numSonda);
               
					if(dataSet != null) {
						toServer.println("<p id=\"ledact\">");
						sensor.setData("valor", dataSet);
						toServer.println("La sonda " + numSonda + " ahora tiene el valor = " + sensor.getValor());
						aux = sensor.getTipo();
					}
					else {
						toServer.println("<p id=\"value\">");
						if(data.equalsIgnoreCase("ultimafecha"))
							toServer.print("Última fecha");
						else
							toServer.print(data);

						toServer.println(": ");
						if(data.equals("id"))
							toServer.println(sensor.getID());
						else if(data.equals("fecha"))
							toServer.println(sensor.getFecha());
						else if(data.equals("tipo"))
							toServer.println(sensor.getTipo());
						else if(data.equals("valor"))
							toServer.println(sensor.getValor());
						else {
							DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
							String date = df.format(Calendar.getInstance().getTime());
							toServer.println(date);
						}
					}

					toServer.println("</p>");
					toServer.println("<p>");

					toServer.println("<a class=\"atras\" href=\"/controladorSD\">Atrás</a>");
					toServer.println("</p>");
					toServer.println("</div>");
				}
				else {
					toServer.println("<p>");
					toServer.println("No existe el número de sonda.");
					toServer.println("</p>");
					toServer.println("<p>");
					toServer.println("<a class=\"atras\" href=\"/controladorSD\">Atrás</a>");
					toServer.println("</p>");
				}

				toServer.println("</div>");
				toServer.println("</section>");	//contprobe
			}
						
			toServer.println("</section>"); //contforms
			toServer.println("</section>");	//containter
			toServer.println("</section>"); //main

			toServer.println("<script>");
			toServer.println("function changeAction(numSensor) {");
			toServer.println("var link = document.getElementById(\"link\" + numSensor);");
			toServer.println("var number = document.getElementById(\"number\" + numSensor);");
			//String auxtemp = "valor";
			toServer.println("link.href = \"http://" + server_ip + ":9999/controladorSD/cambiar"+ aux +"=\" + number.value + \"?sonda=\" + numSensor;");

			toServer.println("}");
			toServer.println("");
			toServer.println("");
			toServer.println("");
			toServer.println("");
			toServer.println("</script>");

			//String temp = "temperatura";
			//toServer.println("link.href = \"http://" + server_ip + ":9999/controladorSD/obtener"+ temp +"=\" + number.value + \"?sonda=\" + numSensor;");



			toServer.println("</body>");
			toServer.println("</html>");

			

			toServer.close();
			fromServer.close();
		}
		catch(Exception e) {
			debug("Error run ControllerThread: " + e.toString());
			try {
				if(toServer != null)
					toServer.close();
			
				if(fromServer != null)
					fromServer.close();
			}
			catch (IOException ioe) {
				debug("Error al cerrar BufferedReader: " + ioe.toString());
			}
		}


	}
}
