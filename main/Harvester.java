package main;

import gui.SystemMonitorWindow;

public class Harvester implements Runnable {
	
	private int cores;
	private SystemMonitorWindow smw;
	
	
	public Harvester(SystemMonitorWindow smw, Boolean proc, Boolean mem, Boolean cpu) {
		this.smw = smw;
		
		if(proc) {
			System.out.println("proc");
			//collectProc();
		} 
		if(mem) {
			System.out.println("proc");
			//collectMem();
		}
		if(cpu) {
			System.out.println("proc");
			//collectCPU();
		}
	}

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
