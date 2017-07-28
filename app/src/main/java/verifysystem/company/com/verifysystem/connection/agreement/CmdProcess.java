package verifysystem.company.com.verifysystem.connection.agreement;

import verifysystem.company.com.verifysystem.connection.ICmdParseInterface;
import verifysystem.company.com.verifysystem.utils.LogUtils;
import verifysystem.company.com.verifysystem.utils.MyHexUtils;

/**
 * 对收到的数据字节，全部缓存起来， 等识别出一条符合协议的数据时，将截取出来的协议数据进行解析
 * Created by zhuj on 2016/7/13 16:19.
 */
public class CmdProcess {

    //3个连起来是 {"A
    public static final byte CMD_HEAD_JSON = (byte) 0x7b;
    public static final byte CMD_HEAD_JSON1 = (byte)  0x22;
    public static final byte CMD_HEAD_JSON2 = (byte)  0x41;

    public static final byte CMD_END_JSON = (byte) 0x7d;
    public static final byte CMD_HEAD = (byte) 0xCF;
    public static final byte CMD_END = (byte) 0xEE;

    private final static String TAG = "cmdProcess";

    private ICmdParseInterface mCmdParse;

    private CmdProcess() {
    }

    public CmdProcess(ICmdParseInterface iCmdParseInterface) {
        this.mCmdParse = iCmdParseInterface;
    }

    private byte[] data_command = new byte[1024];//在构成一条协议数据时 清空
    private int data_index; //当前缓存的数据长度， 收到的字节个数， 在构成一条协议数据时 清0

    /**
     * 将收到的数据 截取符合协议的子集
     */
    public synchronized void ProcessDataCommand(byte[] command, int length) {
        if (command == null || length == 0) {
            return;
        }
        //解析数据达到缓存值时处理
        byte[] newArray;
        if (data_index +length >=1024) {
            newArray = new byte[100];
            System.arraycopy(data_command, data_index-100, newArray, 0, 100);
            data_command = new byte[1024];
            System.arraycopy(newArray, 0, data_command, 0, 100);
            data_index = 100;
        }
        System.arraycopy(command, 0, data_command, data_index, length);
        data_index += length;
        LogUtils.i(TAG, "收到数据~:" + MyHexUtils.buffer2String(command, length));
        //最短协议长度
        if (data_index < 3) return;
        int headIndex = 0;
        for (int index = 0; index < data_index; index++) {
            if (data_command[index] == CMD_HEAD &&data_command[index+1] == 0x02) { // 收到头码
                headIndex = index;
            } else if (data_command[index] == CMD_HEAD &&data_command[index+1] == 0x03) {
                if (data_index - index >=6) {
                    headIndex = index;
                    ProcessData(data_command, headIndex, 7);// 处理
                    data_index = 0;
                    data_command = new byte[1024];
                    break;
                }
            } else if (data_command[index] == CMD_HEAD_JSON) { //JSON数据解析
                // {"A开头
                if (index < data_index-2
                    && data_command[index+1] == CMD_HEAD_JSON1
                    && data_command[index+2] == CMD_HEAD_JSON2) {
                    headIndex = index;
                }
            } else if (data_command[index] == CMD_END_JSON) { //连续两个JSON右括号
                if (index >=1 &&data_command[index - 1] == CMD_END_JSON) {
                    ProcessData(data_command, headIndex, index-headIndex +1);// 处理
                    data_index = 0;
                    data_command = new byte[1024];
                    break;
                }
            }
        }
    }

    private void ProcessData(byte[] buffer, int start, int length) {
        LogUtils.logD(TAG, "收到缓存数据:" + MyHexUtils.buffer2String(data_command, data_index));
        if (mCmdParse != null) {
            byte[] bbb = new byte[length];
            System.arraycopy(buffer, start, bbb, 0, bbb.length);
            //byte[] buff = CmdEncrypt.processMessage(bbb);
            mCmdParse.parseData(bbb);
        }
    }
}
