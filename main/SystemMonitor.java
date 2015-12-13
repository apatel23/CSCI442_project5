package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import gui.SystemMonitorWindow;

public class SystemMonitor {
	
	final static int DEFUALT_INTERVAL = 5000; 	// default for PIDs
	static int interval = 500; 					// default for CPU and mem threads
	static Scheduler cpumemSched; 				// CPU and Memory scheduler
	static Scheduler pidsSched; 				// PIDs (processes) scheduler 
	static BufferedReader br; 					// BufferedReader to open files
	static int cores;							// number of cores in current machine
	
	
	// This is called when the user selects a new update interval
	// from the GUI
	public static void setUpdateInterval(int updateInterval)
	{
		interval = updateInterval;
		// set the delay for cpumemSched to interval
		cpumemSched.setDelay(interval);
	}
	
	
	public static void main (String[] args)
	{
		SystemMonitorWindow mySysMon = new SystemMonitorWindow();
		
		// number of cores
		cores = Runtime.getRuntime().availableProcessors();
		
		// ArrayLists of type Harvester for CPU/memory scheduler and PIDs scheduler  
		ArrayList<Harvester> cpumemSchedList = new ArrayList<Harvester>();
		ArrayList<Harvester> pidsSchedList = new ArrayList<Harvester>();

		
		// add CPU and mem info to cpumemSchedList and PIDs info to pidsSchedList
		cpumemSchedList.add(cpuSchedPopulate(mySysMon)); 
		cpumemSchedList.add(memSchedPopulate(mySysMon));
		pidsSchedList.add(pidsSchedPopulate(mySysMon));
		//cpuSchedPopulate(mySysMon);
		//memSchedPopulate(mySysMon);
		//pidsSchedPopulate(mySysMon);

		// create the Schedulers, cpumemSched uses interval specified by the user
		// pidsSched uses the default interval of 5000 milliseconds
		cpumemSched = new Scheduler(interval, cpumemSchedList);
		pidsSched = new Scheduler(DEFUALT_INTERVAL, pidsSchedList);
		
		/* test CPU calculation
		ArrayList<ArrayList<Integer>> step1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> step2 = new ArrayList<ArrayList<Integer>>();
		calcCPUPercent(step1,step2, mySysMon);
		*/
		
		// start the schedulers
		cpumemSched.start();
		pidsSched.start();
	}

	/* cpuSchedPopulate(SystemMonitorWindow smw)
	 * populates the cpumemSched ArrayList from /proc/stat
	 * updates the GUI with CPU info 
	 * returns a Harvester object
	 */
	private static Harvester cpuSchedPopulate(SystemMonitorWindow smw) {
		
		Harvester h = new Harvester(smw,false,false,true);
		return h;
	}

	/* pidsSchedPopulate(SystemMonitorWindow smw)
	 * populates the cpumemSched ArrayList from /proc/<pid> for every pid that exists
	 * updates the GUI with the PIDs and their state
	 * returns a Harvester object
	 * ****use status for a human-readable approach
	 * ****use stat for machine-oriented approach
	 */
	private static Harvester pidsSchedPopulate(SystemMonitorWindow smw) {
		Harvester h = new Harvester(smw,true,false,false);
		return h;
	}
	
	
	/* memSchedPopulate(SystemMonitorWindow smw)
	 * populates the cpumemSched ArrayList from /proc/meminfo
	 * updates the GUI with the memory info
	 * returns a Harvester object
	 */
	private static Harvester memSchedPopulate(SystemMonitorWindow smw) {
		Harvester h = new Harvester(smw,false,true,false);
		return h;
	}

}
