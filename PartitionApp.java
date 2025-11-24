
import java.util.Random;

class PartitionApp {

    private long[] theArray;
    private int nElems;

    // Counters for the lab requirement
    public static long compCount = 0;
    public static long swapCount = 0;

    public PartitionApp(int max) {
        theArray = new long[max];
        nElems = 0;
    }

    public void insert(long value) {
        theArray[nElems] = value;
        nElems++;
    }

    public int size() {
        return nElems;
    }

    // Helper to fill array with random numbers
    public void fillRandom(int size) {
        Random rand = new Random();
        nElems = 0;
        for (int i = 0; i < size; i++) {
            insert(rand.nextInt(1000)); // Random numbers 0-999
        }
    }

    public void display() {
        for (int i = 0; i < nElems; i++) {
            System.out.print(theArray[i] + " ");
        }
        System.out.println("");
    }

    // --- CORE PARTITION METHOD ---
    public int partitionIt(int left, int right, long pivot) {
        int leftPtr = left - 1;
        int rightPtr = right;

        while (true) {
            // 1. Find bigger item
            // We increment comparisons inside the loop
            while (leftPtr < right && theArray[++leftPtr] < pivot) {
                compCount++;
            }
            compCount++; // Count the final comparison that stopped the loop

            // 2. Find smaller item
            while (rightPtr > left && theArray[--rightPtr] > pivot) {
                compCount++;
            }
            compCount++; // Count the final comparison that stopped the loop

            // 3. Check if pointers crossed
            if (leftPtr >= rightPtr) {
                break;
            } else {
                swap(leftPtr, rightPtr); // Swap elements
            }
        }
        swap(leftPtr, right); // Restore pivot
        return leftPtr; // Return partition index
    }

    public void swap(int dex1, int dex2) {
        swapCount++; // Lab requirement: count swaps
        long temp = theArray[dex1];
        theArray[dex1] = theArray[dex2];
        theArray[dex2] = temp;
    }

    // --- PIVOT SELECTION HELPER ---
    // This handles the requirement: "Do the previous exercise with different pivots"
    public long selectPivot(int left, int right, String strategy) {
        int pivotIndex = right; // Default to last item

        switch (strategy) {
            case "BEGINNING":
                pivotIndex = left;
                break;
            case "MIDDLE":
                pivotIndex = (left + right) / 2;
                System.out.println("Selected Pivot (Middle) -> Index: " + pivotIndex + ", Value: " + theArray[pivotIndex]);
                break;
            case "RANDOM":
                pivotIndex = left + new Random().nextInt(right - left + 1);
                break;
            case "END":
            default:
                pivotIndex = right;
                break;
        }

        // Move the chosen pivot to the rightmost position to simplify partitioning
        // (We don't count this swap as part of the algorithm's work logic usually, 
        // but if your prof is strict, this might increment swapCount)
        long pivotValue = theArray[pivotIndex];
        swap(pivotIndex, right);
        return pivotValue;
    }

    public static void main(String[] args) {
        int maxSize = 100; // Array size
        int runs = 100;    // Lab requirement: 100 runs

        long totalComps = 0;
        long totalSwaps = 0;

        System.out.println("Running " + runs + " experiments...");

        for (int i = 0; i < runs; i++) {
            PartitionApp arr = new PartitionApp(maxSize);
            arr.fillRandom(maxSize);

            // Reset counters for this run
            compCount = 0;
            swapCount = 0;

            int left = 0;
            int right = arr.size() - 1;

            // --- CHANGE STRATEGY HERE: "BEGINNING", "MIDDLE", "RANDOM", "END" ---
            long pivot = arr.selectPivot(left, right, "RANDOM");

            // Perform partition
            int partIndex = arr.partitionIt(left, right, pivot);

            // Accumulate totals
            totalComps += compCount;
            totalSwaps += swapCount;

            // Optional: Print individual run stats (comment out if too noisy)
            // System.out.println("Run " + i + ": Pivot at " + partIndex + ", Comps: " + compCount + ", Swaps: " + swapCount);
        }

        // Calculate Averages
        System.out.println("---------------------------------");
        System.out.println("Results over " + runs + " runs (Strategy: RANDOM):");
        System.out.println("Avg Comparisons: " + (totalComps / runs));
        System.out.println("Avg Swaps:       " + (totalSwaps / runs));
        System.out.println("---------------------------------");
    }
}
