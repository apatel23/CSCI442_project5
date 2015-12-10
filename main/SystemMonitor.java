package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		// TODO : set the delay for cpumemSched to interval
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
		// put initial info in step1, final info in step2, then use to calculate %
		ArrayList<ArrayList<Integer>> step1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> step2 = new ArrayList<ArrayList<Integer>>();
		
		try {
			
			String line = ""; // current line of the buffered reader
			br = new BufferedReader(new FileReader("/proc/stat"));
			line = br.readLine();
			
			// step1
			while(( line = br.readLine()) != null) {
				ArrayList<Integer> info = new ArrayList<Integer>();
				if(line.startsWith("cpu")) {
					String[] tokens = line.split(" ");
					info.add(Integer.parseInt(tokens[1]));
					info.add(Integer.parseInt(tokens[2]));
					info.add(Integer.parseInt(tokens[3]));
					info.add(Integer.parseInt(tokens[4]));
					step1.add(info);
				}
				
			}
			
			// delay in-between the file read
			Thread.sleep(250);
			
			line = ""; // re-initialize
			br = new BufferedReader(new FileReader("/proc/stat"));
			line = br.readLine();
			
			// step2
			while(( line = br.readLine()) != null) {
				ArrayList<Integer> info = new ArrayList<Integer>();
				if(line.startsWith("cpu")) {
					String[] tokens = line.split(" ");
					info.add(Integer.parseInt(tokens[1]));
					info.add(Integer.parseInt(tokens[2]));
					info.add(Integer.parseInt(tokens[3]));
					info.add(Integer.parseInt(tokens[4]));
					step2.add(info);
				}
				
			}
			
		} catch (FileNotFoundException e) { // catch for BufferedReader
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine method
			e.printStackTrace();
		} catch (InterruptedException e) { // catch for sleep method
			e.printStackTrace();
		}
		
		// close the file
		try {
			br.close();
		} catch (IOException e) { // catch for close method
			e.printStackTrace();
		}
		
		
		// final step is to calculate the CPU usage percentage 
		// using the info stored in the step1 and step2 ArrayLists
		// then update the Graph in the GUI
		calcCPUPercent(step1, step2, smw);
		Harvester h = new Harvester();
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
		String name = "";
		String pid = "";
		String state = "";
		String threads = "";
		String voluntary = "";
		String nonvoluntary = "";
		File directory;
		String[] names;
		String[] processInfo;
		String status;
		
		directory = new File("/proc"); // directory to start search of PIDs
		names = directory.list();
		processInfo = new String[6];
		status = "";
		
		try {
		
			// clear out the process list
			smw.removeAllRowsFromProcList();
			
			for(int i = 0; i < names.length; i++) {
				
				// is it a directory?
				// first check if an integer
				if(true) {
				
					status = "/proc/" + names[i].toString() + "/status";
					
					// current line
					String line = "";
					
					
					br = new BufferedReader(new FileReader(status));
					
					
					while((line = br.readLine()) != null) {
						
						// name
						
						// PID
						
						// state
						
						// threads
						
						// voluntary
						
						// non-voluntary
						
					} // end while
	
					// update GUI
	
				} // end if
				

			} // end for
				
		} catch (FileNotFoundException e) { // catch for br
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine()
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) { // catch close()
			e.printStackTrace();
		}
			
		Harvester h = new Harvester();
		return h;
	}
	
	
	/* memSchedPopulate(SystemMonitorWindow smw)
	 * populates the cpumemSched ArrayList from /proc/meminfo
	 * updates the GUI with the memory info
	 * returns a Harvester object
	 */
	private static Harvester memSchedPopulate(SystemMonitorWindow smw) {
		int memTotal = 10; 	// total amount of physical RAM
		int memFree = 0; 	// amount of physical RAM left unused
		int active = 0; 	// buffer or cache memory in use
		int inactive = 0; 	// buffer or cache memory that is free or unavailable
		int swapTotal = 0; 	// total amount of swap available
		int swapFree = 0; 	// total amount of swap free
		int dirty = 0; 		// total amount of memory waiting to be written back to disk
		int writeback = 0; 	// total amount of memory actively being written back to disk
		
		try {
			// current line in /proc/meminfo
			String line = "";
			
			br = new BufferedReader(new FileReader("/proc/meminfo"));
			
			while((line = br.readLine()) != null) {
				
				String [] a = line.split(" "); // break up the current line into an array, split by spaces, integer value will be 2nd to last element
				//System.out.println("line: " + line);
				
				// get memory total
				if(line.startsWith("MemTotal:")) {
					//System.out.println("memTotal: " + a[a.length - 2]);
					memTotal = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory free
				if(line.startsWith("MemFree:")) {
					//System.out.println("memTotal: " + a[a.length - 2]);
					memFree = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory active
				if(line.startsWith("Active:")) {
					//System.out.println("Active: " + a[a.length - 2]);
					active = Integer.parseInt(a[a.length - 2]);
				}
				
				// get memory inactive
				if(line.startsWith("Inactive:")) {
					//System.out.println("Inactive: " + a[a.length - 2]);
					inactive = Integer.parseInt(a[a.length - 2]);
				}
				
				// get swap total
				if(line.startsWith("SwapTotal:")) {
					//System.out.println("SwapTotal: " + a[a.length - 2]);
					swapTotal = Integer.parseInt(a[a.length - 2]);
				}
				
				// get swap free
				if(line.startsWith("SwapFree:")) {
					//System.out.println("SwapFree: " + a[a.length - 2]);
					swapFree = Integer.parseInt(a[a.length - 2]);
				}
				
				// get dirty pages
				if(line.startsWith("Dirty:")) {
					//System.out.println("dirty: " + a[a.length - 2]);
					dirty = Integer.parseInt(a[a.length - 2]);
				}
				
				// get write back
				if(line.startsWith("Writeback:")) {
					//System.out.println("Writeback: " + a[a.length - 2]);
					writeback = Integer.parseInt(a[a.length - 2]);
				}
				
				
			}
			
			// calculate RAM
			int ram = ((memTotal - memFree)/memTotal)*100;
			
			// update the GUI
			smw.updateMemoryInfo(memTotal, memFree, active, inactive, swapTotal, swapFree, dirty, writeback);
			smw.getCPUGraph().addDataPoint(cores, ram);
			
		} catch (FileNotFoundException e) { // catch for file
			e.printStackTrace();
		} catch (IOException e) { // catch for readLine()
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) { // catch for close()
			e.printStackTrace();
		}
		
		
		Harvester h = new Harvester();
		return h;
	}
	
	
	/*	calcCPUPercent(ArrayList<ArrayList<Integer>> step1, ArrayList<ArrayList<Integer>> step2, SystemMonitorWindow smw)
	 *  calculates the CPU percentage and updates the GUI
	 *  returns void
	 *  Calculation for CPU percentage:
	 *  ( (time2 - time1)+(idleTime2 - idleTime1)+(mode2 - mode1) ) / 
	 *  ( [All the stuff on top] + (task2 - task1) ) * 100 
	 */
	 
	private static void calcCPUPercent(ArrayList<ArrayList<Integer>> step1, ArrayList<ArrayList<Integer>> step2, SystemMonitorWindow smw) {
		int time1 = 0;
		int time2 = 0;
		int idleTime1 = 0;
		int idleTime2 = 0;
		int mode1 = 0;
		int mode2 = 0;
		int task1 = 0;
		int task2 = 0;
		
		// loop over the number of cores
		for(int i = 0; i < cores; i++) {
			// step1
			time1 = step1.get(0).get(0);
			idleTime1 = step1.get(0).get(1);
			mode1 = step1.get(0).get(2);
			task1 = step1.get(0).get(3);
			
			// step2
			time2 = step2.get(0).get(0);
			idleTime2 = step2.get(0).get(1);
			mode2 = step2.get(0).get(2);
			task2 = step2.get(0).get(3);
		}
		// end loop
			
		// calculation: (top / bottom) * 100
		double top = (time2 - time1) + (idleTime2 - idleTime1) + (mode2 - mode1);
		double bottom = (top + (task2 - task1));
		int calculation = (int) ((top / bottom) * 100);
		System.out.println("calculation: " + calculation);
		
		// update the GUI
		synchronized(smw) {
			smw.getCPUGraph().addDataPoint(0, calculation);
			smw.getCPUGraph().repaint();
		}
	}

}
