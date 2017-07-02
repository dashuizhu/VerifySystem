package verifysystem.company.com.verifysystem.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import verifysystem.company.com.verifysystem.connection.ICmdParseInterface;
import verifysystem.company.com.verifysystem.connection.IConnectInterface;
import verifysystem.company.com.verifysystem.global.Constant;

public class ConnectBluetoothImpl implements IConnectInterface {
	private BluetoothChatManager BTchat;

	private BluetoothAdapter adapter;

	private BluetoothDevice mBluetoothDevice;

	public ConnectBluetoothImpl(Context context) {
		BTchat = new BluetoothChatManager(context);
	}

	@Override
	public boolean connect(String address, String pwd) {
		// TODO Auto-generated method stub
		adapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothDevice = adapter.getRemoteDevice(address);
		BTchat.connect(mBluetoothDevice);
		return true;
	}

	@Override
	public void stopConncet() {
		// TODO Auto-generated method stub
		if(BTchat!=null) {
			BTchat.stop();
		}
	}

	@Override
	public void write(byte[] buffer) {
		// TODO Auto-generated method stub
		if(BTchat != null) {
			BTchat.write(buffer);
		}
	}

	@Override
	public void writeAgreement(byte[] buffer) {
		// TODO Auto-generated method stub
		if(BTchat!=null) {
			BTchat.write(buffer);
		}
	}

	@Override
	public boolean isLink() {
		// TODO Auto-generated method stub
		if (Constant.isDemo) {
			return true;
		}
		return BTchat.islink();
	}

	@Override
	public boolean isConnecting() {
		return false;
	}

	@Override public int getConnectType() {
		return IConnectInterface.type_bluetooth;
	}


	@Override public void setCmdParse(ICmdParseInterface cmdParse) {
		BTchat.setCmdParse(cmdParse);
	}

}
