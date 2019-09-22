import java.util.*;
import java.io.*;

interface Global {
    Random r = new Random();
    final static int n = r.nextInt((10) + 1) + 5;
    final static int timeSlice = r.nextInt(6) + 1;
    static Vector<Process> copy = new Vector<Process>();
    static Vector<Process> readyQueue = new Vector<Process>();
    static Queue<Process> globalQueue = new LinkedList<Process>();
    static Vector<Integer> timeline = new Vector();
}

class RoundRobin implements Global {
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(
                "Round Robin(RR) is preemptive scheduling algorithm where the process executes for timeslice if its burst time is greater than timeslice and gets preempted to end of RQ.\nThis algorithm is nothing but FCFS with timeslice.");
        Thread.currentThread().sleep(3000);
        System.out.println("Number of processes = " + n + "\n");
        System.out.println("Time Slice to be considered: " + timeSlice + " units\n");
        Output output = new Output();
        output.setName("Output Thread");
        ForLoop forloop = new ForLoop();
        forloop.setName("ForLoop Thread");
        timeline.add(0);
        forloop.start();
        output.start();
    }
}

class Process {
    int at, bt, wt, tt, pid, rt;

    public Process(int at, int bt, int rt, int i) {
        this.at = at;
        this.bt = bt;
        this.rt = rt;
        this.wt = 0;
        this.tt = 0;
        this.pid = i;
    }

    @Override
    public String toString() {
        return pid + "\t\t" + at + "\t\t" + bt + "\t\t" + wt + "\t\t" + tt + "\n";
    }
}

class ForLoop extends Thread implements Global {
    static int startTime = 0;

    public ForLoop() {
    }

    public void run() {
        try {
            Random r = new Random();
            for (int i = 0; i < n; i++) {
                Thread.currentThread().sleep(1000);
                startTime = Math.abs(r.nextInt(((startTime + 5) - startTime) + 1) + startTime);
                int burstTime = r.nextInt(10) + 1;
                Process p = new Process(startTime, burstTime, burstTime, (i + 1));
                copy.add(p);
                globalQueue.add(p);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class Output extends Thread implements Global {
    String ganttChart = "0";

    public Output() {
    }

    public void run() {
        try {
            while (true) {
                Thread.currentThread().sleep(5000);
                while (readyQueue.size() != 0 || globalQueue.size() != 0) {
                    addProcessByArrivalTime();
                    if (!(timeline.lastElement() == 0 && readyQueue.firstElement().at > 0)) {
                        System.out.println("\nProcesses in ready queue are:");
                        System.out.println("\nPID\t\tAT\t\tBT\t\tRT");
                        for (Process temp : readyQueue)
                            System.out.println(temp.pid + "\t\t" + temp.at + "\t\t" + temp.bt + "\t\t" + temp.rt);
                    }
                    if (readyQueue.size() == 0 && timeline.lastElement() != 0) {
                        timeline.add(globalQueue.peek().at);
                        ganttChart += "____|IDLE|____" + globalQueue.peek().at;
                        continue;
                    }
                    int process_rt = 0;
                    if (readyQueue.firstElement().at - timeline.lastElement() > 0 && timeline.lastElement() == 0) {
                        timeline.add(readyQueue.firstElement().at);
                        ganttChart += "____|IDLE|____" + readyQueue.firstElement().at;
                        continue;
                    } else {
                        Process process = readyQueue.remove(0);
                        System.out.println("Process P" + process.pid + " selected for execution.\n");
                        if (process.rt <= timeSlice) {
                            timeline.add(timeline.lastElement() + process.rt);
                            ganttChart += "____|P" + process.pid + "|____" + timeline.lastElement();
                            process_rt = process.rt;
                            process.rt = 0;
                            process.tt = timeline.lastElement() - process.at;
                            process.wt = process.tt - process.bt;
                        } else {
                            process_rt = timeSlice;
                            process.rt -= timeSlice;
                            timeline.add(timeline.lastElement() + timeSlice);
                            ganttChart += "____|P" + process.pid + "|____" + timeline.lastElement();
                            addProcessByArrivalTime();
                            readyQueue.add(process);
                        }
                    }
                    Thread.currentThread().sleep(process_rt * 1000);
                    System.out.println(ganttChart);
                }
                System.out.println("\nThe Gantt Chart is: \n" + ganttChart);
                System.out.println();
                System.out.println("\nPID\t\tAT\t\tBT\t\tWT\t\tTT");
                System.out.println(copy);
                int total_wait = 0, total_turn = 0;
                for (Process p : copy) {
                    total_wait += p.wt;
                    total_turn += p.tt;
                }
                System.out.println(
                        "Average Waiting Time in the queue: " + (double) (total_wait) / (double) (n) + " units");
                System.out.println(
                        "Average Waiting Time in the queue: " + (double) (total_turn) / (double) (n) + " units");
                break;
            }
        } catch (

        Exception e) {
            System.out.println(e);
        }
    }

    public static void addProcessByArrivalTime() {
        while (true) {
            if (timeline.lastElement() == 0) {
                readyQueue.add(globalQueue.poll());
                break;
            }
            if (globalQueue.size() != 0 && globalQueue.peek().at <= timeline.lastElement())
                readyQueue.add(globalQueue.poll());
            else
                break;
        }
    }
}
