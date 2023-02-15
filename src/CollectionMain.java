
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.serotonin.modbus4j.msg.WriteRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

import modbus_rtu.SerialPortWrapperImpl;
 
/**
 * 通过串口解析MODBUS协议
 */
public class CollectionMain {
    // 设定MODBUS网络上从站地址
    private final static int SLAVE_ADDRESS = 1;
    //串行波特率
    private final static int BAUD_RATE = 9600;
 
    public static void main(String[] args) {
        SerialPortWrapper serialParameters = new
                SerialPortWrapperImpl("COM8", BAUD_RATE, 8, 1, 0, 0, 0);
        /* 创建ModbusFactory工厂实例 */
        ModbusFactory modbusFactory = new ModbusFactory();
        /* 创建ModbusMaster实例 */
        ModbusMaster master = modbusFactory.createRtuMaster(serialParameters);
        /* 初始化 */
        try {
            master.init();
            //发送通过
            writeSingleHoldingRegister(master, SLAVE_ADDRESS, 102, 2);
            
            //读取状态数据
            byte[] zhuangtaibyte=readHoldingRegisters(master, SLAVE_ADDRESS, 102, 1);
            int zhuangtai=getInt(zhuangtaibyte);
            System.out.println("状态数据："+zhuangtai);
            
            //读取称重数据
            byte[] chengzhongbyte=readHoldingRegisters(master, SLAVE_ADDRESS, 100, 2);
            float chengzhong=getFloat(chengzhongbyte);
            System.out.println("称重数据："+chengzhong);
            
            
//            writeMultipleHoldingRegisters(master, SLAVE_ADDRESS, 0, (short)1,(short)1);
        } catch (ModbusInitException e) {
            e.printStackTrace();
        } finally {
            master.destroy();
        }
    }
 
    /**
     * 0x03
     * 读保持寄存器上的内容
     * @param master 主站
     * @param slaveId 从站地址
     * @param start 起始地址的偏移量
     * @param len 待读寄存器的个数
     */
    private static byte[] readHoldingRegisters(ModbusMaster master, int slaveId, int start, int len) {
    	byte[] list=null;
        try {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
            ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse)master.send(request);
            System.out.println(response.toString());
            if (response.isException()) {
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            } else {
                list = response.getData();
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
        return list;
    }
 
    // 0x06 写单个保持寄存器
    // 线圈寄存器是coil，所以保持寄存器method-name非holding修饰
    private static void writeSingleHoldingRegister(ModbusMaster master, int slaveId, int offset, int value){
        try {
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
            WriteRegisterResponse response =
                    (WriteRegisterResponse)master.send(request);
            if (response.isException()) {
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            } else {
                System.out.println("RunRight reponse: messgae="+"no-response-data");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }
 
    // 0x10 写多个保持寄存器
    //
    private static void writeMultipleHoldingRegisters(ModbusMaster master, int slaveId, int offset, short... sdata){
        try {
            WriteRegistersRequest request = new WriteRegistersRequest(slaveId, offset, sdata);
            WriteRegistersResponse response =
                    (WriteRegistersResponse)master.send(request);
            if (response.isException()) {
                System.out.println("Exception response: message=" + response.getExceptionMessage());
            } else {
                System.out.println("RunRight reponse: messgae="+"no-response-data");
            }
        } catch (ModbusTransportException e) {
            e.printStackTrace();
        }
    }
    
    public static int getInt(byte[] bytes)
    {
    	// byte数组中序号小的在右边
        return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
    }
    
    public static float getFloat(byte[] bytes)
    {
		byte[] list=new byte[bytes.length];
		int j=0;
		//翻转高低位
		//获取高位
		for (int i = 2; i < bytes.length; i++) {
			list[j]=bytes[i];
			j++;
        }
		//获取低位
		for (int i = 0; i < 2; i++) {
			list[j]=bytes[i];
			j++;
        }
		ByteBuffer bb = ByteBuffer.wrap(list);

    	FloatBuffer fb = bb.asFloatBuffer();

    	return fb.get();
    }
}