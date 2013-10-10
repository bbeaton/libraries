package mindset;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import processing.core.PApplet;
import processing.serial.Serial;

public class MindSet extends TimerTask implements DataListener {
	Method mindSetEventListenerMethod;
	Method mindSetRawEventListenerMethod;
	Method mindSetAttentionEventListenerMethod;
	Method mindSetMeditationEventListenerMethod;



	Serial headsetConnection = null;
	PApplet parent;
	public HeadsetData data;
	StreamParser parser;
	ArrayList<EEGDataListener> listeners;
	public int rawData;
	Timer timer;
	boolean rawDataIsFresh = false;


	public MindSet(PApplet parent){
		this.parent = parent;
		data = new HeadsetData();
		parser = new StreamParser(StreamParser.PARSER_TYPE_PACKETS, this, null);
		listeners = new ArrayList<EEGDataListener>();
		parent.registerDispose(this);
		try {
			mindSetEventListenerMethod =
				parent.getClass().getMethod("mindSetEvent",
						new Class[] { MindSet.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			mindSetRawEventListenerMethod =
				parent.getClass().getMethod("mindSetRawEvent",
						new Class[] { MindSet.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			mindSetMeditationEventListenerMethod =
				parent.getClass().getMethod("mindSetMedtationEvent",
						new Class[] { MindSet.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			mindSetAttentionEventListenerMethod =
				parent.getClass().getMethod("mindSetAttentionEvent",
						new Class[] { MindSet.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
	}

	public void dispose(){
		if(timer!=null)
			timer.cancel();
		//if(headsetConnection!=null)
		//	headsetConnection.stop();
	}

	public boolean connect(){
		for(String portName:Serial.list()){
			if(portName.contains("cu.MindSet")){
				connect(portName);
				return true;
			}
		}
		for(String portName:Serial.list()){
			if(portName.contains("tty.MindSet")){
				connect(portName);
				return true;
			}
		}
		return false;
	}

	public void connect(String portName){
		headsetConnection = new Serial(parent, portName, 57600);
		timer = new Timer();
		timer.schedule(this, 1,1);
		
	}


	public void dataValueReceived(int extendedCodeLevel, int code,
			int numBytes, byte[] valueBytes, Object customData) {

		//if(code!= StreamParser.PARSER_CODE_RAW)
		//	System.out.println(code);
		switch(code){
		case(StreamParser.PARSER_CODE_POOR_SIGNAL):
			//System.out.println("POOR SIGNAL: "+(valueBytes[0]&0xff));
			data.errorRate = valueBytes[0] & 0xFF;

			break;
		case(StreamParser.PARSER_CODE_ATTENTION):
			data.attention =valueBytes[0] & 0xFF;
		if (mindSetAttentionEventListenerMethod != null) {
			try {
				mindSetAttentionEventListenerMethod.invoke(parent, new Object[] { this });
			} catch (Exception e) {
				System.err.println("Disabling mindSetAttentionEventListenerMethsod because of an error.");
				e.printStackTrace();
				mindSetAttentionEventListenerMethod = null;
			}
		}
		break;
		case(StreamParser.PARSER_CODE_MEDITATION):
			data.meditation = valueBytes[0] & 0xFF;
		if (mindSetMeditationEventListenerMethod != null) {
			try {
				mindSetMeditationEventListenerMethod.invoke(parent, new Object[] { this });
			} catch (Exception e) {
				System.err.println("Disabling mindSetMeditationEventListenerMethsod because of an error.");
				e.printStackTrace();
				mindSetMeditationEventListenerMethod = null;
			}
		}
		break;
		case(StreamParser.PARSER_CODE_BATTERY):
			data.batteryLevel = valueBytes[0] & 0xFF;
		break;

		case(StreamParser.PARSER_CODE_RAW):
			//TODO: I'm not 100% sure the endianness is right here...
			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.order(ByteOrder.BIG_ENDIAN);
			bb.put(valueBytes[0]);
			bb.put(valueBytes[1]);
			short rawValue = bb.getShort(0);
			//int rawValue = (valueBytes[0] & 0xFF) << 0  |  (valueBytes[1] & 0xFF) << 8; 
			rawData = rawValue;
			//if(rawDataIsFresh) System.err.println("Warning: Raw data wasn't read");
			rawDataIsFresh = true;
	
			for(EEGDataListener listener: listeners){
				listener.handleRawEEG(rawValue);
			}
	
			if (mindSetRawEventListenerMethod != null) {
				try {
					mindSetRawEventListenerMethod.invoke(parent, new Object[] { this });
				} catch (Exception e) {
					System.err.println("Disabling mindSetRawEventListenerMethsod because of an error.");
					e.printStackTrace();
					mindSetRawEventListenerMethod = null;
				}
			}

		break;
		case(StreamParser.PARSER_CODE_EEG_POWERS):
			//System.out.println(valueBytes);
		data.delta = (valueBytes[0] & 0xFF) << 16  |  (valueBytes[1] & 0xFF) << 8  |  (valueBytes[2] & 0xFF) << 0;
		data.theta = (valueBytes[3] & 0xFF) << 16  |  (valueBytes[4] & 0xFF) << 8  |  (valueBytes[5] & 0xFF) << 0;
		data.alpha1 = (valueBytes[6] & 0xFF) << 16  |  (valueBytes[7] & 0xFF) << 8  |  (valueBytes[8] & 0xFF) << 0;
		data.alpha2 = (valueBytes[9] & 0xFF) << 16  |  (valueBytes[10] & 0xFF) << 8  |  (valueBytes[11] & 0xFF) << 0;
		data.beta1 = (valueBytes[12] & 0xFF) << 16  |  (valueBytes[13] & 0xFF) << 8  |  (valueBytes[14] & 0xFF) << 0;
		data.beta2 = (valueBytes[15] & 0xFF) << 16  |  (valueBytes[16] & 0xFF) << 8  |  (valueBytes[17] & 0xFF) << 0;
		data.gamma1 = (valueBytes[18] & 0xFF) << 16  |  (valueBytes[19] & 0xFF) << 8  |  (valueBytes[20] & 0xFF) << 0;
		data.gamma2 = (valueBytes[21] & 0xFF) << 16  |  (valueBytes[22] & 0xFF) << 8  |  (valueBytes[23] & 0xFF) << 0;
		for(EEGDataListener listener: listeners){
			listener.handleEEG(data.delta, data.theta, data.alpha1, data.alpha2, data.beta1, data.beta2, data.gamma1, data.gamma2);
		}

		if (mindSetEventListenerMethod != null) {
			try {
				mindSetEventListenerMethod.invoke(parent, new Object[] { this });
			} catch (Exception e) {
				System.err.println("Disabling mindSetEventListenerMethsod because of an error.");
				e.printStackTrace();
				mindSetEventListenerMethod = null;
			}
		}

		break;

		}


	}

	public void update(){

		int data = headsetConnection.read();
		
		while(data != -1){
			parser.parseByte(data);
			data = headsetConnection.read(); 
		}
	}

	public HeadsetData getCurrentData(){
		return data;
	}
	
	public boolean isThereNewRawData(){
		return rawDataIsFresh;
	}
	public int getCurrentRawData(){
		rawDataIsFresh = false;
		return rawData;
	}

	public void registerListener(EEGDataListener listener){
		listeners.add(listener);
	}

	
	public void run() {
		this.update();
		
	}


}
