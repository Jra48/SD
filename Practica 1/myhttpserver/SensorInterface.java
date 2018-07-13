import java.rmi.Remote;

public interface SensorInterface extends Remote {
	public String getSensor() throws java.rmi.RemoteException;
	public String getID() throws java.rmi.RemoteException;
	public String getFecha() throws java.rmi.RemoteException;
	public String getTipo() throws java.rmi.RemoteException;
	public String getValor() throws java.rmi.RemoteException;
	public void getData(String sensor) throws java.rmi.RemoteException;
	public void setData(String dataRequest, String dataSet) throws java.rmi.RemoteException;
}
