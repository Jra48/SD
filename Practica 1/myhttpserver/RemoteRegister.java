import java.rmi.server.*;
import java.rmi.*;
import java.rmi.registry.Registry;

public class RemoteRegister extends UnicastRemoteObject implements RemoteRegisterInterface 	{
	
	static final String RMI_NAME = RemoteRegister.class.getSimpleName();

	private final Registry registry;

	protected RemoteRegister(Registry registry) throws RemoteException {
		super();
		this.registry = registry;
	}
	
	@Override
	public void register(SensorInterface sensor) throws RemoteException {
		String name;
		
		try {
			name = sensor.getSensor();
			registry.rebind(name, sensor);
		}
		catch(Exception e) {
			System.out.println("Error register: " + e.toString());
		}
	}
}