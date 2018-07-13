import java.rmi.Remote;

public interface RemoteRegisterInterface extends Remote {
	public void register(SensorInterface sensor) throws java.rmi.RemoteException;
}