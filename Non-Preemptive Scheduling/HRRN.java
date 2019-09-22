import java.util.*;
import java.io.*;

interface Global {
    Random r = new Random();
    final static int n = r.nextInt((5) + 1) + 5;
    static Vector<Process> copy = new Vector<Process>();
    static Vector<Process> readyQueue = new Vector<Process>();
    static Queue<Process> globalQueue = new LinkedList<Process>();
    static Vector<Integer> timeline = new Vector();
}

class HRRN implements Global {
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("\nHighest Response Ratio Next(HRRN) is non-preemptive scheduling algorithm where the process to be scheduled next is selected on basis of 'Response Ratio'.\nResponse Ratio = (waiting time + burst time) / burst time.\nIn this method, there will be no starvation, because the response ratio  will increase if it waits in RQ for longer time.\n");
        Thread.currentThread().sleep(3000);
        System.out.println("Number of processes = " + n + "\n");
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
    int at, bt, wt, tt, pid, pr;

    public Process(int at, int bt, int pr, int i) {
        this.at = at;
        this.bt = bt;
        this.pr = pr;
        this.wt = 0;
        this.tt = 0;
        this.pid = i;
    }

    @Override
    public String toString() {
        return pid + "\t\t" + at + "\t\t" + bt + "\t\t" + wt + "\t\t" + tt + "\n";
    }
}

class Response implements Global {
    public int getProcessByHighestResponseRatio() {
        Process first = readyQueue.firstElement();
        int index = 0;
        System.out.println("Response Ratios for processes at time instant = "+timeline.lastElement()+" are:\n");
        double ratio_first = Math.abs(((double) (timeline.lastElement() - first.at + first.bt)) / (double) first.bt);
        System.out.println("Process P"+first.pid+" : "+ratio_first);
        for (int i = 1; i < readyQueue.size(); i++) {
            double ratio_next = Math.abs(((double) (timeline.lastElement() - readyQueue.get(i).at + readyQueue.get(i).bt))
                    / (double) readyQueue.get(i).bt);
            System.out.println("Process P"+readyQueue.get(i).pid+" : "+ratio_next);
            if (ratio_first < ratio_next) {
                ratio_first = ratio_next;
                index = i;
            } else
                continue;
        }
        return index;
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
                Process p = new Process(startTime, r.nextInt(10) + 1, r.nextInt(6) + 1, (i + 1));
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
                    if(!(timeline.lastElement()==0 && readyQueue.firstElement().at>0)){
                        System.out.println("\nProcesses in ready queue are:");
                        System.out.println("\nPID\t\tAT\t\tBT\t\tWT\t\tTT");
                        System.out.println(readyQueue);
                    }
                    int i = new Response().getProcessByHighestResponseRatio();
                    System.out.println();
                    if (readyQueue.size() == 0 && timeline.lastElement() != 0) {
                        timeline.add(globalQueue.peek().at); 
                        ganttChart+= "____|IDLE|____"+globalQueue.peek().at;
                        continue;
                    }
                    if (readyQueue.get(i).at - timeline.lastElement() > 0 && timeline.lastElement() == 0) {
                        timeline.add(readyQueue.get(i).at);
                        ganttChart+= "____|IDLE|____"+readyQueue.get(i).at;
                        continue;
                    }
                    if (readyQueue.get(i).at - timeline.lastElement() > 0 && timeline.lastElement()!= 0) {
                        timeline.add(readyQueue.get(i).at);
                        ganttChart+= "____|IDLE|____"+readyQueue.get(i).at;
                        continue;
                    }
                    Process process = readyQueue.remove(i);
                    System.out.println("Process P"+process.pid+" selected as it has highest response ratio.\n");
                    process.wt = timeline.lastElement() - process.at;
                    ganttChart+="____|P" + process.pid + "|____";
                    process.tt = process.wt + process.bt;
                    timeline.add(timeline.lastElement() + process.bt);
                    ganttChart+=timeline.lastElement();
                    Thread.currentThread().sleep(process.bt * 1000);
                    System.out.println(ganttChart);
                }
                System.out.println("\nThe Gantt Chart is: \n"+ganttChart);
                System.out.println();
                System.out.println("\nPID\t\tAT\t\tBT\t\tWT\t\tTT");
                System.out.println(copy);
                break;
            }
        } catch (Exception e) {
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
