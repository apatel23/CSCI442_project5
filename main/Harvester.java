package main;

public class Harvester implements Runnable {
	
	int cores;

	@Override
	public void run() {
		while(true) {
			collect();
			pause();
		}
	}

	// wait until the program is ready
	private void pause() {
		try {
			wait();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	// abstract method 
	public void collect() {
		// TODO Auto-generated method stub
	}
	
	
	/* getCores()
	 * use Runnable to find the number of cores in the current machine
	 * return the number of cores (int)
	 */
	public int getCores() {
		cores = Runtime.getRuntime().availableProcessors();
		return cores;
	}

}
