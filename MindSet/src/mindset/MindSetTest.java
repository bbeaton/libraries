package mindset;

import processing.core.PApplet;


public class MindSetTest extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MindSet headset;

	public void setup() {
		headset = new MindSet(this);
		//headset.registerListener(this);
		headset.connect("/dev/cu.MindSet-DevB");

	}

	public void draw() {
		//headset.update();
		//println(headset.data.meditation + " " + headset.data.attention);
		//println(headset.data);
	}
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { mindset.MindSetTest.class.getName() });
	}
/*
	public void handleRawEEG(int data) {
		//println(data);
		
	}

	public void handleEEG(int delta, int theta, int lowAlpha, int highAlpha,
			int lowBeta, int highBeta, int lowGamma, int midGamma) {
		//println(delta);

	}
	*/
	public void mindSetEvent(MindSet ms){
		println(ms.data.delta + " " + ms.data.theta + " " + ms.data.alpha1 + " " + ms.data.alpha2 + " " + ms.data.beta1 + " " + ms.data.beta2 + " " + ms.data.gamma1 + " " + ms.data.gamma2);
	}
	
	public void mindSetRawEvent(MindSet ms){
		 // print(ms.getCurrentRawData());
	}
	
}
