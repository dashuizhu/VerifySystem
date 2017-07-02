package verifysystem.company.com.verifysystem.connection;

public class ConnectAction {
	/**
	 * 数据传递的key 值
	 */
	public static final String BROADCAST_DATA_value = "data";
	public static final String BROADCAST_DATA_TYPE = "type";
	public static final String BROADCAST_DEVICE_MAC = "deviceMac";
	public static final String BROADCAST_DEVICE_Name = "name";

	public static final int Broad_Cmd = 999;

	/**
	 * 广播事件名, 数据
	 */
	public static final String ACTION_RECEIVER_DATA = "com.wt.isensor.broadcast";

	/**
	 * 发现蓝牙设备广播   name  device  asrc
	 */
	public final static String ACTION_BLUETOOTH_FOUND = "com.example.bluetooth.le.ACTION_BLUETOOTH_FOUND";
	/**
	 * 连接上设备
	 */
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	/**
	 * 连接中
	 */
	public final static String ACTION_GATT_CONNECTING = "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
	/**
	 * 断开连接设备
	 */
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	/**
	 * BLE 发现service
	 */
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

	public static final String ACTION_SHOW_TOAST = "com.interphone.nolink";
}
