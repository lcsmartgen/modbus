package modbus_rtu;

/**
*
* Copyright (c) 2009-2020 Freedomotic Team http://www.freedomotic-iot.com
*
* This file is part of Freedomotic
*
* This Program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation; either version 2, or (at your option) any later version.
*
* This Program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License along with
* Freedomotic; see the file COPYING. If not, see
* <http://www.gnu.org/licenses/>.
*/

import com.serotonin.modbus4j.serial.SerialPortWrapper;
import jssc.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import jssc.SerialPortException;

/**
*
*/
public class SerialPortWrapperImpl implements SerialPortWrapper {
	
   private SerialPort port;
   private String commPortId;
   private int baudRate;
   private int dataBits;
   private int stopBits;
   private int parity;
   private int flowControlIn;
   private int flowControlOut;

   public SerialPortWrapperImpl(String commPortId, int baudRate, int dataBits, int stopBits, int parity, int flowControlIn,
                                int flowControlOut) {

       this.commPortId = commPortId;
       this.baudRate = baudRate;
       this.dataBits = dataBits;
       this.stopBits = stopBits;
       this.parity = parity;
       this.flowControlIn = flowControlIn;
       this.flowControlOut = flowControlOut;

       port = new SerialPort(this.commPortId);

   }

   public void close() throws Exception {
       port.closePort();
       //listeners.forEach(PortConnectionListener::closed);
       System.out.println(String.format("Serial port {} closed", port.getPortName()));
   }

   public void open() {
       try {
           port.openPort();
           port.setParams(this.getBaudRate(), this.getDataBits(), this.getStopBits(), this.getParity());
           port.setFlowControlMode(this.getFlowControlIn() | this.getFlowControlOut());

           //listeners.forEach(PortConnectionListener::opened);
           System.out.println(String.format("Serial port {} opened", port.getPortName()));
       } catch (SerialPortException ex) {
           System.out.println(String.format("Error opening port : {} for {} ", port.getPortName(), ex));
       }
   }

   public InputStream getInputStream() {
       return new SerialInputStream(port);
   }

   public OutputStream getOutputStream() {
       return new SerialOutputStream(port);
   }

   public int getBaudRate() {
       return baudRate;
       //return SerialPort.BAUDRATE_9600;
   }

   public int getFlowControlIn() {
       return flowControlIn;
       //return SerialPort.FLOWCONTROL_NONE;
   }

   public int getFlowControlOut() {
       return flowControlOut;
       //return SerialPort.FLOWCONTROL_NONE;
   }

   public int getDataBits() {
       return dataBits;
       //return SerialPort.DATABITS_8;
   }

   public int getStopBits() {
       return stopBits;
       //return SerialPort.STOPBITS_1;
   }

   public int getParity() {
       return parity;
       //return SerialPort.PARITY_NONE;
   }
}