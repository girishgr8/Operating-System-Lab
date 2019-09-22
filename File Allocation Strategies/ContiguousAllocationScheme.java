
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;

// Object of FAT class stores the File Allocation Table or Directory Table....
class FAT{
  String fileName;
  int fileSize;
  int startBlock;
  public FAT(){}
  public FAT(String fileName, int fileSize, int startBlock){
    this.fileName = fileName;
    this.fileSize = fileSize;
    this.startBlock = startBlock;
  }
}

class ContiguousAllocation{
	static Scanner sc = new Scanner(System.in);
	static Vector<FAT> fat = new Vector<>();
	static int totalBlockSize;
	static int bptr = 0;
	static int totalMemorySize; 
	static int totalBlocks;
	static String memory[];
	static int cntFree;
	static TreeSet<Integer> freeBlocks = new TreeSet<Integer>();

	public static void performAllocation(int totalMemorySize,int totalBlockSize, int totalBlocks) {
		ContiguousAllocation.totalMemorySize = totalMemorySize;
		ContiguousAllocation.totalBlockSize = totalBlockSize;
		ContiguousAllocation.totalBlocks = totalBlocks;
	    memory= new String[totalBlocks];
	    for(int i=0;i<totalBlocks;i++)
	        freeBlocks.add(i);
	    int choice;
	    System.out.printf("Choose operation you want to perform:\n1)Create new file\n2)Delete a file\n3)Show all available files\n4)Show FAT table\n5)Exit\nEnter your choice: ");
	    choice = sc.nextInt();
	    do{
	      switch(choice){
	        case 1: createFile(); break;
	        case 2: deleteFile(); break;
	        case 3: showAllAvailableFiles(); break;
	        case 4: showFATTable(); break;
	        default: break;
	      }
	      System.out.printf("Choose operation you want to perform:\n1)Create new file\n2)Delete a file\n3)Show all available files\n4)Show FAT table\n5)Exit\nEnter your choice: ");
	      choice = sc.nextInt();
	    }while(choice!=5);
	}
	public static void createFile(){
	    System.out.printf("Enter name for file you want to create: ");
	    String fileName = sc.next();
	    System.out.printf("Enter file size in KB: ");
	    int fileSize = sc.nextInt();
	    int blocksNeeded = (int) Math.ceil((double)fileSize/(double)totalBlockSize);
	    System.out.println("Number of blocks needed to allocate new file \""+fileName+"\" are: "+blocksNeeded);
	    bptr = allocateContiguousAvailable(blocksNeeded);
	    if(bptr!=-1) {
	    	FAT file = new FAT(fileName,fileSize,bptr);
	    	fat.add(file);
	    }
	    else {
	    	System.out.println("Sufficient memory not available to store new file. Delete some file and free up your memory.");
	    	showAllAvailableFiles();
	    	System.out.printf("Do you want to delete some file ? Y or N: ");
	    	if(sc.next().equals("Y"))
	    		deleteFile();
	    }
	  }
	  public static void deleteFile(){
		  System.out.printf("Enter name of file you want to delete: ");
		  String fileName = sc.next();
		  deallocateContiguousBlocks(fileName);
	  }
	  
	  public static void showFATTable() {
		  System.out.println("\n\t\t\tFAT Table for Contiguous File Allocation Scheme: ");
		  System.out.println("SrNo.\tFile Name\t\t\tFile Size\t\tStartBlock");
		  int i = 1;
		  for(FAT file: fat)
			  System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t"+file.fileSize+"KB\t\t\t"+file.startBlock);
		  System.out.println();
	  }
	  
	  public static void showAllAvailableFiles() {
		  System.out.println("\n\t\t\tList of available files: ");
		  System.out.println("SrNo.\tFile Name\t\t\t\tFile Size\t\t");
		  int i = 1;
		  for(FAT file: fat)
			  System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t\t"+file.fileSize+"KB");
		  System.out.println();
	  }
	  public static int allocateContiguousAvailable(int blocksNeeded) {
		  boolean isAvailable = false;
		  int startPtr = -1, endPtr = -1;
		  System.out.println("Free Blocks before allocation: "+freeBlocks.size());
		  if(blocksNeeded>freeBlocks.size())
			  return -1;
		  for(int i=0;i<totalBlocks;i++) {
			  startPtr = -1;
			  if(freeBlocks.contains(i)) {
				  int checkBlockCount = 0;
				  int j = i;
				  startPtr = i;
				  while(freeBlocks.contains(j)) {
					  checkBlockCount++;
					  if(checkBlockCount==blocksNeeded) {
						  isAvailable = true;
						  endPtr = j;
						  break;
					  }
					  j++;
				  }
			  }
			  if(isAvailable==true)
				  break;
		  }
		  
		  if(isAvailable) {
			  cntFree-=(endPtr-startPtr+1);
			  for(int i=startPtr;i<=endPtr;i++)
				  freeBlocks.remove(i);
		  }
		  System.out.println("Free Blocks after allocation: "+freeBlocks.size());
		  return startPtr;
	  }

	  public static void deallocateContiguousBlocks(String fileName) {
		  int start = -1, end = -1;
		  System.out.println("Free Blocks before deallocation: "+freeBlocks);
		  for(FAT file: fat) {
			  if(file.fileName.equals(fileName)) {
				  start = file.startBlock;
				  end = start + (int) file.fileSize/totalBlockSize;
				  fat.remove(file);
				  break;
			  } 
		  }
		  if(start!=-1) {
			  while(start<=end) {
				freeBlocks.add(start);
				cntFree++; start++;
			  }
			  System.out.println("Deleted file "+fileName+"successfully.");
		  }
		  else {
			  System.out.println("File "+fileName+" not found");
		  }
		  System.out.println("Free Blocks after deallocation: "+freeBlocks);
		  return;
	  }
}


public class ContiguousAllocationScheme {
  static Scanner sc = new Scanner(System.in);
  static Vector<FAT> fat = new Vector<>();
  static int totalBlockSize;
  static int totalMemorySize; 
  static int totalBlocks;
  
  public static void main(String[] args) {
	System.out.println("\t\tContiguous File Allocation Strategies\n");
	System.out.printf("Enter total memory size in KB: ");
    totalMemorySize = sc.nextInt(); 
    System.out.printf("Enter size of each block in KB: ");
    totalBlockSize = sc.nextInt();
    totalBlocks = (int) totalMemorySize/totalBlockSize;
    System.out.println("Total number of blocks present in memory: "+totalBlocks);
	ContiguousAllocation.performAllocation(totalMemorySize,totalBlockSize, totalBlocks);
  }  
}