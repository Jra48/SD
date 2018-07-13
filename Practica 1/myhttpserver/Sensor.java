import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;

public class Sensor extends UnicastRemoteObject implements SensorInterface, Serializable {
	private String sensor, id, fecha, valor, tipo;

	Sensor(String sensor) throws RemoteException {
		try {
			this.sensor = sensor;

			getData(sensor);
		}
		catch(Exception e) {
			debug("Error constructor: " + e.toString());
		}
	}

	void debug(String mensaje) {
		System.out.println("Mensaje: " + mensaje);
	}

	public String getSensor() throws RemoteException {
		getData(sensor);

		return sensor;
	}

	public String getTipo() throws RemoteException {
		return tipo;
	}
	public String getID() throws RemoteException {
		getData(sensor);

		return id;
	}

	public String getFecha() throws RemoteException {
 		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date fecha = new Date();
    return(dateFormat.format(fecha));
	}


	public String getValor() throws RemoteException {
		getData(sensor);
		
		return valor;
	}

	public void getData(String sensor) {
		String line;
		File file;
		try {
			file = new File("sensor" + sensor + ".txt");

			if(file.exists()) {

				BufferedReader buffer = new BufferedReader(new FileReader(file));

				while((line = buffer.readLine()) != null) {
					String [] dataRead = line.split("=");

					if(dataRead[0].equals("id")){
						id = dataRead[1];
					}else if(dataRead[0].equals("tipo")){
						tipo = dataRead[1];
					}else if(dataRead[0].equals("valor")){
						valor = dataRead[1];
					}else if(dataRead[0].equals("fecha")){
						fecha = dataRead[1];
					}  
				}

				buffer.close();
			}
		}
		catch(Exception e) {
			debug("Error getData: " + e.toString());
		}
	}

	public void setData(String dataRequest, String dataSet) throws RemoteException {
		try {
			File file = new File("sensor" + sensor + ".txt");
			
			if(file.exists()) {
				String line;
				List<String> lines = new ArrayList<String>();
				BufferedReader bufferReader = new BufferedReader(new FileReader(file));
				
				while((line = bufferReader.readLine()) != null) {
					if(line.toLowerCase().contains(dataRequest)) {
						String [] dataRead = line.split("=");
						
						
						line = dataRead[0] + "=" + dataSet;
					}

					lines.add(line + "\n");
				}

				bufferReader.close();

				BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(file));

				for(String cad : lines)
					bufferWriter.write(cad);
				
				bufferWriter.close();
			}
		}
		catch(Exception e) {
			debug("Error constructor: " + e.toString());
		}
	}

	
	public static void main(String[] args) throws Exception {
		if(args.length < 2) {
			System.out.println("Debe indicar la dirección del controller y el nombre de un archivo de texto.");
			System.out.println("$./Controller controller_ip sensorX.txt");
			System.exit(1);
		}

		final String host = args[0];
		final String name = args[1];
		String tipo = "";
		int cont = 1;

		try {
			String sensorName = "sensor";

			for(int i = 0; i < 6; i++) {
				if(name.charAt(i) != sensorName.charAt(i)) {
					System.out.println(Character.toString(name.charAt(i)) + " ---- " + Character.toString(sensorName.charAt(i)));
					System.out.println("Debe indicar un nombre de archivo correcto: sensorX.txt");
					System.exit(1);
				}
			}

			File file = new File(name);

			if(!file.exists()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				// VALORES POR DEFECTO
				bw.write("id=0\n");
				bw.write("fecha=" + (int) (Math.random()*28+1) + "/" + (int) (Math.random()*12+1) + "/"
									+ (int) (Math.random()*2017+2000) + " " + (int) (Math.random()*1+0) + (int) (Math.random()*9+0) + ":"
									+ (int) (Math.random()*5+1) + (int) (Math.random()*9+0) + ":" + (int) (Math.random()*5+1) +
									+ (int) (Math.random()*9+1) + "\n");
				int tiporandom = 0;
				tiporandom = (int) (Math.random()* 2+0);

				if(tiporandom == 0){
					bw.write("tipo=humedad\n");
					tipo = "humedad";
				}
				else{
					bw.write("tipo=temperatura\n");
					tipo = "temperatura";
				}

				if(tipo.equals("humedad")){
					bw.write("valor=" + (int) (Math.random()*101+0));
				}
				else{
					if(tipo.equals("temperatura")){
						bw.write("valor=" + ((int) (Math.random()*55+0) - 10));
					}
				}
				
				bw.close();
			}

			if(System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());

			Sensor sensor = new Sensor(Character.toString(name.charAt(6)));
			RemoteRegisterInterface registrador = (RemoteRegisterInterface)Naming.lookup("rmi://" + host + ":1099/Registrador");
			registrador.register(sensor);
		}
		catch(Exception e) {
			System.out.println("Error en Sensor.main: " + e.toString());
			System.exit(1);
		}
  }
}
