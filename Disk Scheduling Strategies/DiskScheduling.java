package Exp8;
import java.util.*;
import java.io.*;

interface Global {
	Random r = new Random();
	final static int totalTracks = r.nextInt(200)+1;
	final static int n = r.nextInt(30);
	static Vector<Integer> copyTrackNumbers = new Vector<Integer>();
	static Vector<Integer> trackNumbers = new Vector<Integer>();
}

class DiskScheduling implements Global {
	Scanner sc = new Scanner(System.in);
	static int headPosition = r.nextInt((int)(totalTracks/2));
	static int totalHeadMovement = 0;
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("\nFCFS(First Come First Serve) Disk Scheduling");
		Thread.currentThread().sleep(500);
		System.out.println("Total number of tracks on the disk are: " + totalTracks + "\n");
		System.out.println("Current Header Position is: "+ headPosition+"\n");
		System.out.println("n = "+n);
		Output output = new Output();
		output.setName("Output Thread");
		ForLoop forloop = new ForLoop();
		forloop.setName("ForLoop Thread");
		forloop.start();
		output.start();
	}
}


class ForLoop extends Thread implements Global {
	static int trackNo = 0;
	public ForLoop() {
	}

	public void run() {
		try {
			for (int i = 0; i < n; i++) {
				Thread.currentThread().sleep(1000);
				trackNo = r.nextInt(totalTracks);
				trackNumbers.add(trackNo);
				copyTrackNumbers.add(trackNo);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}

class Output extends Thread implements Global {
	String headTraversal = DiskScheduling.headPosition + " ";
	public Output() {
	}

	public void run() {
		try {
			while (true) {
				Thread.currentThread().sleep(1500);
				while (trackNumbers.size() != 0) {
					System.out.println("Queue is: "+trackNumbers);
					int moveToTrack = trackNumbers.remove(0); 
					DiskScheduling.totalHeadMovement += Math.abs(DiskScheduling.headPosition - moveToTrack);
					headTraversal += "->" + moveToTrack + " ";
					System.out.println(headTraversal);
					Thread.currentThread().sleep(Math.abs(DiskScheduling.headPosition - moveToTrack)*90);
				}
				
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
