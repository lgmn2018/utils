package com.lgmn.utils.printer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Date;
import java.util.Vector;

public class EthernetPort extends Port {
    public Log log = LogFactory.getLog(EthernetPort.class);
	InetAddress mIPAddress;
	Socket m_Socket = null;
	Thread m_Thread;
	OutputStream m_OutStream;
	InputStream m_InStream;

	EthernetPort(PortParameters parameters) {
		super(parameters);
	}

	@Override
	boolean isPortOpen() {
		boolean retval = true;
		if (this.m_Socket != null) {
			if ((!this.m_Socket.isConnected()) || (!this.m_Socket.isBound()) || (this.m_Socket.isClosed())
					|| (this.m_Socket.isInputShutdown()) || (this.m_Socket.isOutputShutdown())) {
				retval = false;
			}
		} else {
			retval = false;
		}
		return retval;
	}

	@Override
	GpCom.ERROR_CODE openPort() {
        log.info("------- openPort method -------");
		GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
		this.m_Exception = null;

		if(this.m_Thread == null) {
			this.m_Thread = new Thread(new Runnable() {
				@Override
				public void run() {
					log.info("Thread started");
					log.info("Buffers cleared");
					try {
						log.info("IP address: " + EthernetPort.this.mPortParameters.mIPAddress);
						log.info("Port number: " +

								Integer.toString(EthernetPort.this.mPortParameters.mPortNumber));

						EthernetPort.this.mIPAddress = Inet4Address.getByName(EthernetPort.this.mPortParameters.mIPAddress);
						SocketAddress remoteAddr = new InetSocketAddress(EthernetPort.this.mIPAddress,
								EthernetPort.this.mPortParameters.mPortNumber);
						EthernetPort.this.m_Socket = new Socket();
						EthernetPort.this.m_Socket.connect(remoteAddr, 4000);
						log.info("Socket created");
						EthernetPort.this.m_OutStream = EthernetPort.this.m_Socket.getOutputStream();
						EthernetPort.this.m_InStream = EthernetPort.this.m_Socket.getInputStream();
						log.info("Streams created");
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Exception occured creating sockets and streams: " + e.getMessage());
						EthernetPort.this.m_Exception = e;
					}

					if (EthernetPort.this.m_Exception == null) {

						log.info("Starting network loop");
						EthernetPort.this.m_ThreadRunning = false;
						EthernetPort.this.m_CloseFlag = Boolean.valueOf(false);
						while (!EthernetPort.this.m_CloseFlag.booleanValue()) {
							try {
								if (EthernetPort.this.m_SendFlag.booleanValue()) {
									log.info("Sending data: " +

											Integer.toString(EthernetPort.this.m_SendData.length) + " bytes");
									EthernetPort.this.m_OutStream.write(EthernetPort.this.m_SendData);
									EthernetPort.this.m_OutStream.flush();
									EthernetPort.this.m_SendFlag = Boolean.valueOf(false);
									log.info("Finished sending data");
								}
								EthernetPort.this.m_bytesAvailable = EthernetPort.this.m_InStream.available();
								if (EthernetPort.this.m_bytesAvailable > 0) {
									log.info("Receiving data: " +

											Integer.toString(EthernetPort.this.m_bytesAvailable) + " bytes");
									int n = EthernetPort.this.m_InStream.read(EthernetPort.this.m_receiveData);
									for (int i = 0; i < n; i++) {
										EthernetPort.this.m_receiveBuffer
												.add(Byte.valueOf(EthernetPort.this.m_receiveData[i]));
									}

									EthernetPort.this.saveData(EthernetPort.this.m_receiveBuffer);
									if (EthernetPort.this.mCallback != null) {
										EthernetPort.this.mCallback.ReceiveData(EthernetPort.this.m_receiveBuffer);
										EthernetPort.this.m_receiveBuffer.clear();
									}
								} else {
									Thread.sleep(50L);
								}

								EthernetPort.this.m_ThreadRunning = true;
							} catch (Exception e) {
								e.printStackTrace();
								log.error("Exception occured in run loop: " + e.getMessage());

								EthernetPort.this.m_Exception = e;
								EthernetPort.this.m_CloseFlag = Boolean.valueOf(true);
							}
						}

						log.info("Closing network");
						try {
							EthernetPort.this.m_OutStream = null;
							EthernetPort.this.m_Socket.close();
							EthernetPort.this.m_Socket = null;
						} catch (Exception localException1) {
							localException1.printStackTrace();
						}

						EthernetPort.this.m_ThreadRunning = false;
					}
				}
			});
		}
		this.m_Thread.start();
		try {
			Thread.sleep(50L);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		while ((!this.m_ThreadRunning) && (this.m_Exception == null)) {
			try {
				Thread.sleep(50L);
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
		}
		if (this.m_Exception != null) {
			if ((this.m_Exception instanceof UnknownHostException)) {
				retval = GpCom.ERROR_CODE.INVALID_IP_ADDRESS;
			} else if ((this.m_Exception instanceof SocketTimeoutException)) {
				retval = GpCom.ERROR_CODE.TIMEOUT;
			} else {
				retval = GpCom.ERROR_CODE.FAILED;
			}
		}

		return retval;
	}

	@Override
	GpCom.ERROR_CODE closePort() {
		GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;

		Date NowDate = new Date();
		Date TimeoutDate = new Date(NowDate.getTime() + 2000L);
		while (((this.m_SendFlag.booleanValue()) || (this.m_bytesAvailable > 0)) && (NowDate.before(TimeoutDate))) {
			try {
				Thread.sleep(50L);
			} catch (Exception localException) {
			}
			NowDate = new Date();
		}
		if (NowDate.before(TimeoutDate)) {
			try {
				this.m_OutStream.flush();
				this.m_Socket.close();
				this.m_CloseFlag = Boolean.valueOf(true);
			} catch (IOException e) {
				retval = GpCom.ERROR_CODE.FAILED;
			}
		} else {
			retval = GpCom.ERROR_CODE.TIMEOUT;
		}
		return retval;
	}

	@Override
	GpCom.ERROR_CODE writeData(Vector<Byte> data) {
		GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;

		if ((data != null) && (data.size() > 0)) {
			parseOutgoingData(data);

			if ((this.m_Socket != null) && (this.m_OutStream != null)) {
				Date NowDate = new Date();
				Date TimeoutDate = new Date(NowDate.getTime() + 3000L);

				while ((this.m_SendFlag.booleanValue()) && (NowDate.before(TimeoutDate))) {
					try {
						Thread.sleep(50L);
					} catch (InterruptedException localInterruptedException) {
					}
					NowDate = new Date();
				}

				if (NowDate.before(TimeoutDate)) {
					this.m_SendData = new byte[data.size()];

					if (data.size() > 0) {
						for (int i = 0; i < data.size(); i++) {
							this.m_SendData[i] = ((Byte) data.get(i)).byteValue();
						}
						this.m_SendFlag = Boolean.valueOf(true);
					}
				} else {
					retval = GpCom.ERROR_CODE.TIMEOUT;
				}
			} else {
				retval = GpCom.ERROR_CODE.FAILED;
			}
		}

		return retval;
	}

	@Override
	protected GpCom.ERROR_CODE writeDataImmediately(Vector<Byte> data) {
		GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;

		if ((data != null) && (data.size() > 0)) {
			byte[] sendData = new byte[data.size()];

			if (data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {
					sendData[i] = ((Byte) data.get(i)).byteValue();
				}
				try {
					this.m_OutStream.write(sendData);
					this.m_OutStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
                    log.error("Exception occured while sending data immediately: " + e.getMessage());
					retval = GpCom.ERROR_CODE.FAILED;
				}
			}
		}

		return retval;
	}
}