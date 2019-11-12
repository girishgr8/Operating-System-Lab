import java.util.*;

class RequestResource{
	public BankerAlgorithm b;
	public RequestResource(BankerAlgorithm b) {
		this.b = b;
	}
	public void allocateResource(int pid, int request[]) {
		System.out.println("\n\tRunning Request Resource Algorithm to allocate resources\n");
		int c1 = Arrays.compare(request,Arrays.copyOf(b.need[pid],b.m));
		int c2 = Arrays.compare(request,Arrays.copyOf(b.available,b.m));
		if(c1<=0 && c2<=0) {
			for(int j=0;j<b.m;j++) {
				b.available[j] = b.available[j] - request[j];
				b.allocation[pid][j] = b.allocation[pid][j] + request[j];
				b.need[pid][j] = b.need[pid][j] - request[j];
			}
			System.out.println("New Available: "+Arrays.toString(b.available));
			System.out.println("New Allocation: "+Arrays.toString(b.allocation[pid]));
			System.out.println("New Need: "+Arrays.toString(b.need[pid]));
		}
	}
}

class Safety{
	public BankerAlgorithm b;
	Safety(){}
	public Safety(BankerAlgorithm b) {
		this.b = b;
	}
	
	public Vector<String> checkForSafety() {
		System.out.println("\n\tRunning Safety Algorithm to check if system ran into deadlock or not\n");
		// Make a copy of 'available' vector as 'work'
		int work[] = Arrays.copyOf(b.available,b.m);
		System.out.println("Work Vector is: "+Arrays.toString(work));
		boolean finish[] = new boolean[b.n];
		Vector<String> sequence = new Vector<String>();
		
		Arrays.fill(finish, false);
		boolean runLoop = true;
		while(runLoop==true && sequence.size()!=b.n) {
			boolean updationDone = false;
			for(int i=0;i<b.n;i++) {
				boolean satisfied = true;
				for(int p=0;p<b.m;p++) {
					if(b.need[i][p]>work[p]) {
						satisfied=false;
						break;
					}
				}
				if(finish[i]==false && satisfied==true) {
					finish[i] = true;
					for(int j=0;j<b.m;j++)
						work[j] = work[j] + b.allocation[i][j];
					sequence.add("P"+i);
					updationDone = true;
				}
				else continue;
			}
			if(updationDone==false)
				runLoop = false;
		}
		if(runLoop==true && sequence.size()==b.n)
			return sequence;
		else{
      Vector<String> notSafe = new Vector<String>();
      for(int i=0;i<b.n;i++)
        if(!sequence.contains("P"+i))
          notSafe.add("P"+i);
      
      System.out.println("System is not in safe state currently...Deadlock exists !!!");
      System.out.println("Processes in deadlock are: "+notSafe);
			return null;
	}
}
}



class BankerAlgorithm {
	int m,n;
	int resource[];
	int available[];
	int claim[][];
	int allocation[][];
	int need[][];
	public BankerAlgorithm(int m, int n, int[] resource, int[] available, int[][] claim, int[][] allocation, int[][] need) {
		this.m = m;
		this.n = n;
		this.resource = resource;
		this.available = available;
		this.claim = claim;
		this.allocation = allocation;
		this.need = need;
	}
	public static void BankerAlgorithm(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("\t\tBanker's Algorithm\n");
//		m: number of resources
//		n: number of processes
		int m,n;
		System.out.printf("Enter number of resources: "); 
		m = sc.nextInt();
		System.out.printf("Enter number of processes: ");
		n = sc.nextInt();
		int resource[] = new int[m];
    Vector<String> process = new Vector<String>();
		int available[] = new int[m];
		int claim[][] = new int[n][m];
		int allocation[][] =  new int[n][m];
		int need[][] =  new int[n][m];
    for(int i=0;i<n;i++)
      process.add("P"+i);
		// Input resource vector......
		System.out.printf("Enter number of instances of each resource: ");
		for(int i=0;i<m;i++)
			resource[i] = sc.nextInt();
		
		// Input claim matrix....
		System.out.println("Enter Claim Matrix: ");
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				claim[i][j] = sc.nextInt();
				if(claim[i][j]>resource[j]){
          System.out.println("Kindly provide valid input.....");
					System.exit(0);
        }
			}
		}
		// Input allocation matrix....
		System.out.println("Enter Allocation Matrix: ");
		for(int i=0;i<n;i++)
			for(int j=0;j<m;j++)
				allocation[i][j] = sc.nextInt();
		
		BankerAlgorithm b = new BankerAlgorithm(m,n,resource,available,claim,allocation,need);
		b.checkForValidAllocation(b);
		b.findAvailableVector(b);
		// Find the Need Matrix....  
		b.findNeedMatrix(b);
		
		// Firstly, check if the system is not in deadlock...
		Safety safety = new Safety(b);
		Vector <String> sequence = safety.checkForSafety();
		if(sequence!=null)
			System.out.println("System is safe.\tThe Safe Sequence is: "+sequence);
		else {
			System.out.println("System is not in safe state currently...Deadlock exists !!!");
			System.exit(0);
		}
		System.out.printf("Enter process number which requesting: ");
		int pid = sc.nextInt();
		System.out.printf("Enter Request Resource Vector for P"+pid+": ");
		int request[] = new int[m];
		for(int i=0;i<m;i++) 
				request[i] = sc.nextInt();

		RequestResource requestResource = new RequestResource(b);
		requestResource.allocateResource(pid, request);
		sequence = safety.checkForSafety();
		if(sequence!=null)
			System.out.println("System is safe.\tThe Safe Sequence is: "+sequence);
		else {
			System.exit(0);
		}
		sc.close();
	}
	
	public void findNeedMatrix(BankerAlgorithm b) {
		// Calculate Need Matrix....
		for(int i=0;i<b.n;i++)
			for(int j=0;j<b.m;j++)
				need[i][j] = claim[i][j] - allocation[i][j];
		System.out.println("Need Matrix is: ");
		for(int i=0;i<b.n;i++) {
			for(int j=0;j<b.m;j++)
				System.out.printf(need[i][j]+" ");
			System.out.println();
		}
	}
	
	public void findAvailableVector(BankerAlgorithm b) {
		for(int i=0;i<b.m;i++) { 
			for(int j=0;j<b.n;j++)
				b.available[i]+= b.allocation[j][i];
			b.available[i]= b.resource[i]-b.available[i];
      if(b.available[i]<0){
        System.out.println("You have entered invalid Allocation Matrix. Allocated is more than Available");
        System.exit(0);
      }
		}
		
		System.out.println("Available Vector is: "+Arrays.toString(available));
	}
	
	public void checkForValidAllocation(BankerAlgorithm b){
		for(int i=0;i<b.n;i++){
			for(int j=0;j<b.m;j++){
				if(allocation[i][j]>claim[i][j]){
					System.out.println("Kindly avoid valid inputs....");
					System.exit(0);
				}
			}
		}
	}
}

/*
 
Example 1: 
Claim Matrix:
7 5 3
3 2 2 
9 0 2
2 2 2
4 3 3

Allocation Matrix:
0 1 0
2 0 0
3 0 2
2 1 1
0 0 2

**************************************************************************
Example 2: 

Claim Matrix: 
9 5 5 5
2 2 3 3
7 5 4 4 
3 3 3 2
5 2 2 1
4 4 4 4

Allocation Matrix:
2 0 2 1
0 1 1 1
4 1 0 2
1 0 0 1
1 1 0 0
1 0 1 1
 
*/
