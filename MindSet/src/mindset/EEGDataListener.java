package mindset;

public interface EEGDataListener {
	public void handleRawEEG(int data);
	public void handleEEG(int delta, int theta, int lowAlpha, int highAlpha, int lowBeta, int highBeta, int lowGamma, int midGamma);
	
}
