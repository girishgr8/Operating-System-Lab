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
		System.out.println("\t\tContiguous File Allocation Scheme\n");
		ContiguousAllocation.totalMemorySize = totalMemorySize;
		ContiguousAllocation.totalBlockSize = totalBlockSize;
		ContiguousAllocation.totalBlocks = totalBlocks;
	    memory= new String[totalBlocks];
	    for(int i=0;i<totalBlocks;i++)
	        freeBlocks.add(i);
	    int choice;
	    System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
	    choice = sc.nextInt();
	    do{
	      switch(choice){
	        case 1: createFile(); break;
	        case 2: deleteFile(); break;
	        case 3: showAllAvailableFiles(); break;
	        case 4: showFATTable(); break;
	        default: break;
	      }
	      System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
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
		System.out.println("Free Blocks before allocation: "+freeBlocks);
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
		System.out.println("Free Blocks after allocation: "+freeBlocks);
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
		else 
			System.out.println("File "+fileName+" not found");  
		System.out.println("Free Blocks after deallocation: "+freeBlocks);
		return;
	}
}


class LinkedBlock{
	int blockNo, nextPtr;
	boolean isFree;
	public LinkedBlock() {}
	public LinkedBlock(int blockNo) {
		this.blockNo = blockNo;
		this.nextPtr = -1;
		this.isFree = true;
	}
}

class LinkedAllocation{
	static Scanner sc = new Scanner(System.in);
	static Vector<FAT> fat = new Vector<>();
	static int totalBlockSize;
	static int bptr = 0;
	static int totalMemorySize; 
	static int totalBlocks;
	static String memory[];
	static int cntFree;
	static Vector<LinkedBlock> freeBlocks = new Vector<LinkedBlock>();

	public static void performAllocation(int totalMemorySize,int totalBlockSize, int totalBlocks) {
		System.out.println("\t\tLinked File Allocation Scheme\n");
		LinkedAllocation.totalMemorySize = totalMemorySize;
		LinkedAllocation.totalBlockSize = totalBlockSize;
		LinkedAllocation.totalBlocks = totalBlocks;
		cntFree = totalBlocks;
		for(int i=0;i<totalBlocks;i++) {
			LinkedBlock b = new LinkedBlock(i);
	        freeBlocks.add(b);
		}
	    int choice;
	    System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
	    choice = sc.nextInt();
	    do{
	      switch(choice){
	        case 1: createFile(); break;
	        case 2: deleteFile(); break;
	        case 3: showAllAvailableFiles(); break;
	        case 4: showFATTable(); break;
	        default: break;
	      }
	      System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
	      choice = sc.nextInt();
	    }while(choice!=5);
	}
	public static void createFile() {
		System.out.printf("Enter name for file you want to create: ");
	    String fileName = sc.next();
	    System.out.printf("Enter file size in KB: ");
	    int fileSize = sc.nextInt();
	    int blocksNeeded = (int) Math.ceil((double)fileSize/(double)totalBlockSize);
	    System.out.println("Number of blocks needed to allocate new file \""+fileName+"\" are: "+blocksNeeded);
	    // One extra block needed to store the index which contains the pointers to the file blocks...
	    bptr = allocateBlocks(blocksNeeded);
	    if(bptr!=-1) {
	    	System.out.println("Created new file: \""+fileName+"\"");
	    	FAT file = new FAT(fileName,fileSize,bptr);
	    	fat.add(file);
	    }
	    else {
	    	System.out.println("Insufficient memory. Could not create new file: \""+fileName+"\". ");
	    	System.out.println("Free some memory by deleting some file. Do you want to delete some file? Y or N");
	    	if(sc.next().equals("Y")) {
	    		showAllAvailableFiles();
	    		deleteFile();
	    		bptr = allocateBlocks(blocksNeeded);
	    	    if(bptr!=-1) {
	    	    	System.out.println("Created new file: \""+fileName+"\"");
	    	    	FAT file = new FAT(fileName,fileSize,bptr);
	    	    	fat.add(file);
	    	    }
	    	}else {
	    		System.out.println("Could not create a new file");
	    	}
	    }
	}
	
	public static int allocateBlocks(int blocksNeeded) {
		int startPtr = -1;
		System.out.println("Free blocks before allocation: "+cntFree);
		if(blocksNeeded>cntFree)
			return -1;
		int entered = 0;
		int lastIndex = -1;
		int checkBlockCount = 0;
		LinkedBlock b = new LinkedBlock();
		for(int i=0;i<totalBlocks && checkBlockCount<blocksNeeded;i++) {
			b = (LinkedBlock) freeBlocks.get(i);
			if(b.isFree == true) {
				checkBlockCount++;
				entered++;
				// If found the first free block then it will become the start pointer of the LinkedList...
				if(entered==1)
					startPtr = b.blockNo;
				if(lastIndex!=-1)
					freeBlocks.get(lastIndex).nextPtr = b.blockNo;
				lastIndex = b.blockNo;
				b.isFree = false;
				cntFree--;
			}
		}
		System.out.println("Free blocks after allocation: "+cntFree);
		return startPtr;
	} 
	
	public static void deleteFile() {
		String fileName;
		System.out.printf("Enter name of file you want to delete: ");
		fileName = sc.next();
		deallocateBlocks(fileName);
	}
	
	public static void deallocateBlocks(String fileName) {
		int ptr = -1;
		System.out.println("Free blocks before deallocation: "+cntFree);
		FAT delFile = new FAT();
		System.out.println("Free blocks before deallocation: "+cntFree);
		for(FAT file: fat) {
			if(file.fileName.equals(fileName)) {
				delFile = file;
				ptr = file.startBlock;
				break;
			}
		}
		// If the initial startPointer doesn't exist.....
		if(ptr==-1) {
			System.out.println("File "+fileName+" is not found");
			return;
		}
		while(ptr!=-1) {
			int prevPtr = freeBlocks.get(ptr).blockNo;
			cntFree++;
			freeBlocks.get(ptr).isFree = true;
			ptr = freeBlocks.get(ptr).nextPtr;
			freeBlocks.get(prevPtr).nextPtr=-1;
		}
		fat.remove(delFile);
		System.out.println("Free blocks after deallocation: "+cntFree);
	}
	
	public static void showAllAvailableFiles() {
		System.out.println("\n\t\t\tList of available files: ");
		System.out.println("SrNo.\tFile Name\t\t\t\tFile Size\t\t");
		int i = 1;
		for(FAT file: fat)
			System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t\t"+file.fileSize+"KB");
		System.out.println();
	}
	
	public static void showFATTable() {
		System.out.println("\n\t\t\tFAT Table for Linked File Allocation Scheme: ");
		System.out.println("SrNo.\tFile Name\t\t\tFile Size\t\tLinkPointers");
		int i = 1;
		for(FAT file: fat) {
			LinkedBlock b = new LinkedBlock();
			System.out.printf((i++)+"\t"+file.fileName+"\t\t\t\t"+file.fileSize+"KB\t\t\t");
			b = freeBlocks.get(file.startBlock);
			while(b.nextPtr!=-1) {
				System.out.printf(b.blockNo+"->");
				b = freeBlocks.get(b.nextPtr);
			}
			System.out.printf(""+b.blockNo+"\n");
		}
		System.out.println();
	}
}


class IndexBlock{
	int blockNo;
	String blockContent;
	Vector<Integer> blockList;
	boolean isFree;
	public IndexBlock() {}
	public IndexBlock(int blockNo){
		this.blockNo = blockNo;
		this.blockContent = "";
		this.blockList = new Vector<Integer>();
		this.isFree = true;
	}
}

class IndexedAllocation{
	static Scanner sc = new Scanner(System.in);
	static Vector<FAT> fat = new Vector<>();
	static int totalBlockSize;
	static int bptr = 0;
	static int totalMemorySize; 
	static int totalBlocks;
	static String memory[];
	static int cntFree;
	static Vector<IndexBlock> freeBlocks = new Vector<IndexBlock>();
	public static void performAllocation(int totalMemorySize,int totalBlockSize, int totalBlocks) {
		System.out.println("\t\Indexed File Allocation Scheme\n");
		IndexedAllocation.totalMemorySize = totalMemorySize;
		IndexedAllocation.totalBlockSize = totalBlockSize;
		IndexedAllocation.totalBlocks = totalBlocks;
		cntFree = totalBlocks;
		for(int i=0;i<totalBlocks;i++) {
			IndexBlock b = new IndexBlock(i);
			freeBlocks.add(b);
		}
		
	    int choice;
	    System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
	    choice = sc.nextInt();
	    do{
	      switch(choice){
	        case 1: createFile(); break;
	        case 2: deleteFile(); break;
	        case 3: showAllAvailableFiles(); break;
	        case 4: showFATTable(); break;
	        default: break;
	      }
	      System.out.printf("Choose operation you want to perform:\n1)Create new file\t2)Delete a file\t3)Show all available files\t4)Show FAT table\t5)Exit\nEnter your choice: ");
	      choice = sc.nextInt();
	    }while(choice!=5);
	}
	public static void createFile() {
		System.out.printf("Enter name for file you want to create: ");
	    String fileName = sc.next();
	    System.out.printf("Enter file size in KB: ");
	    int fileSize = sc.nextInt();
	    int blocksNeeded = (int) Math.ceil((double)fileSize/(double)totalBlockSize);
	    System.out.println("Number of blocks needed to allocate new file \""+fileName+"\" are: "+blocksNeeded);
	    // One extra block needed to store the index which contains the pointers to the file blocks...
	    bptr = allocateBlocks(blocksNeeded+1);
	    if(bptr!=-1) {
	    	System.out.println("Created new file: \""+fileName+"\"");
	    	FAT file = new FAT(fileName,fileSize,bptr);
	    	fat.add(file);
	    }
	    else {
	    	System.out.println("Insufficient memory. Could not create new file: \""+fileName+"\".");
	    	System.out.println("Free some memory by deleting some file. Do you want to delete some file? Y or N");
	    	if(sc.next().equals("Y")) {
	    		showAllAvailableFiles();
	    		deleteFile();
	    		bptr = allocateBlocks(blocksNeeded+1);
	    	    if(bptr!=-1) {
	    	    	System.out.println("Created new file: \""+fileName+"\"");
	    	    	FAT file = new FAT(fileName,fileSize,bptr);
	    	    	fat.add(file);
	    	    }
	    	}else {
	    		System.out.println("Could not create a new file");
	    	}
	    }
	}
	
	public static int allocateBlocks(int blocksNeeded) {
		int startPtr = -1;
		System.out.println("Free blocks before allocation: "+cntFree);
		if(blocksNeeded>cntFree)
			return -1;
		IndexBlock firstBlock = new IndexBlock();
		for(int i=0;i<totalBlocks;i++) {
			firstBlock = (IndexBlock) freeBlocks.get(i);
			if(firstBlock.isFree == true) {
				startPtr = firstBlock.blockNo;
				// Getting the first free block to store the index values....
				firstBlock.isFree = false;
				break;
			}
		}
		int checkBlockCount = 0;
		for(int i=startPtr+1;(i<totalBlocks && checkBlockCount<blocksNeeded-1);i++) {
			IndexBlock b = freeBlocks.get(i);
			if(b.isFree == true) {
				checkBlockCount++;
				b.isFree = false;
				firstBlock.blockList.add(b.blockNo);
			}
		}
		cntFree-= blocksNeeded;
		// To indicate the null pointer;
		// firstBlock.blockList.add(-1);
		System.out.println("Free blocks after allocation: "+cntFree);
		return startPtr;
	}
	
	public static void deleteFile() {
		String fileName;
		System.out.printf("Enter name of file you want to delete: ");
		fileName = sc.next();
		deallocateBlocks(fileName);
	}
	
	public static void deallocateBlocks(String fileName) {
		int startPtr = -1;
		FAT delFile = new FAT();
		System.out.println("Free blocks before deallocation: "+cntFree);
		for(FAT file: fat) {
			if(file.fileName.equals(fileName)) {
				delFile = file;
				startPtr = file.startBlock;
				break;
			}
		}
		// If the initial startPointer doesn't exist....
		if(startPtr==-1) {
			System.out.println("File "+fileName+" is not found");
			return;
		}
		while(freeBlocks.get(startPtr).blockList.size()>0) {
			freeBlocks.get(freeBlocks.get(startPtr).blockList.firstElement()).isFree = true;
			cntFree++;
			freeBlocks.get(startPtr).blockList.remove(0);
		}
		freeBlocks.get(startPtr).isFree = true;
		cntFree++;
		System.out.println("Free blocks after deallocation: "+cntFree);
		fat.remove(delFile);
	}
	
	public static void showAllAvailableFiles() {
		System.out.println("\n\t\t\tList of available files: ");
		System.out.println("SrNo.\tFile Name\t\t\t\tFile Size\t\t");
		int i = 1;
		for(FAT file: fat)
			System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t\t"+file.fileSize+"KB");
		System.out.println();
	}

	public static void showFATTable() {
		System.out.println("\n\t\t\tFAT Table for Indexed File Allocation Scheme: ");
		System.out.println("SrNo.\tFile Name\t\t\tFile Size\t\tIndexBlock\t\tPointers");
		int i = 1;
		for(FAT file: fat) {
			IndexBlock b = new IndexBlock();
			for(int k=0;k<freeBlocks.size();k++) {
				b = freeBlocks.get(k);
				if(b.blockNo == file.startBlock)
					break;
			}
			System.out.println((i++)+"\t"+file.fileName+"\t\t\t\t"+file.fileSize+"KB\t\t\t"+file.startBlock+"\t\t"+b.blockList);			  
		}
		System.out.println();
	}
}


public class FileAllocationStrategy {
  	static Scanner sc = new Scanner(System.in);
  	static Vector<FAT> fat = new Vector<>();
  	static int totalBlockSize;
  	static int totalMemorySize; 
  	static int totalBlocks;
  
  	public static void main(String[] args) {
		System.out.println("\t\tFile Allocation Strategies\n");
		System.out.printf("Enter total memory size in KB: ");
	    totalMemorySize = sc.nextInt(); 
	    System.out.printf("Enter size of each block in KB: ");
	    totalBlockSize = sc.nextInt();
	    totalBlocks = (int) totalMemorySize/totalBlockSize;
	    System.out.println("Total number of blocks present in memory: "+totalBlocks);
	    System.out.printf("1)Contiguous Allocation\n2)Linked Allocation\n3)Indexed Allocation\n4)Exit\nEnter your choice: ");
	    int choice = sc.nextInt();
    	do {
	    	switch(choice) {
	    		case 1: ContiguousAllocation.performAllocation(totalMemorySize,totalBlockSize, totalBlocks); break;
	    		case 2: LinkedAllocation.performAllocation(totalMemorySize,totalBlockSize, totalBlocks); break;
	    		case 3: IndexedAllocation.performAllocation(totalMemorySize, totalBlockSize, totalBlocks); break;
	    		default: System.out.println("Invalid Choice\n"); break;
    		}
    		System.out.printf("1)Contiguous Allocation\n2)Linked Allocation\n3)Indexed Allocation\n4)Exit\nEnter your choice: ");
        	choice = sc.nextInt();
    	}while(choice!=4);
  	}  
}