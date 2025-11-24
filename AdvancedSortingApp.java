
import java.util.Random;

public class AdvancedSortingApp {

    // Global counters for the lab metrics
    static long comparisons = 0;
    static long copies = 0;
    static long swaps = 0;

    public static void main(String[] args) {
        // The specific sizes requested in the lab
        int[] sizes = {10000, 15000, 20000, 25000, 30000, 35000, 40000, 45000, 50000};

        System.out.println("======================================================================================================");
        System.out.printf("%-10s | %-30s | %-30s | %-30s%n", "Size", "Merge Sort (Cp/Cm/Sw)", "Shell Sort (Cp/Cm/Sw)", "Quick Sort3 (Cp/Cm/Sw)");
        System.out.println("======================================================================================================");

        for (int size : sizes) {
            int[] original = generateRandomArray(size);

            // --- 1. Merge Sort Run ---
            int[] arrMerge = original.clone();
            resetCounters();
            mergeSort(arrMerge, new int[size], 0, size - 1);
            String mergeStats = String.format("%d / %d / %d", copies, comparisons, swaps);

            // --- 2. Shell Sort Run ---
            int[] arrShell = original.clone();
            resetCounters();
            shellSort(arrShell);
            String shellStats = String.format("%d / %d / %d", copies, comparisons, swaps);

            // --- 3. Quick Sort (Median-of-3) Run ---
            int[] arrQuick = original.clone();
            resetCounters();
            recQuickSort(arrQuick, 0, size - 1);
            String quickStats = String.format("%d / %d / %d", copies, comparisons, swaps);

            // Print Row
            System.out.printf("%-10d | %-30s | %-30s | %-30s%n", size, mergeStats, shellStats, quickStats);
        }
        System.out.println("======================================================================================================");
        System.out.println("*Legend: Cp = Copies, Cm = Comparisons, Sw = Swaps");
    }

    // Helper: Generate Random Data
    public static int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(100000);
        }
        return arr;
    }

    // Helper: Reset Counters
    public static void resetCounters() {
        comparisons = 0;
        copies = 0;
        swaps = 0;
    }

    // ----------------------------------------------------------------------
    // ALGORITHM 1: MERGE SORT (Listing 6.6 logic)
    // ----------------------------------------------------------------------
    public static void mergeSort(int[] arr, int[] workSpace, int lowerBound, int upperBound) {
        if (lowerBound == upperBound) {
            return;
        }

        int mid = (lowerBound + upperBound) / 2;
        mergeSort(arr, workSpace, lowerBound, mid);
        mergeSort(arr, workSpace, mid + 1, upperBound);
        merge(arr, workSpace, lowerBound, mid + 1, upperBound);
    }

    public static void merge(int[] arr, int[] workSpace, int lowPtr, int highPtr, int upperBound) {
        int j = 0;
        int lowerBound = lowPtr;
        int mid = highPtr - 1;
        int n = upperBound - lowerBound + 1;

        while (lowPtr <= mid && highPtr <= upperBound) {
            comparisons++;
            if (arr[lowPtr] < arr[highPtr]) {
                workSpace[j++] = arr[lowPtr++];
                copies++; // Copy to workspace
            } else {
                workSpace[j++] = arr[highPtr++];
                copies++; // Copy to workspace
            }
        }

        while (lowPtr <= mid) {
            workSpace[j++] = arr[lowPtr++];
            copies++;
        }

        while (highPtr <= upperBound) {
            workSpace[j++] = arr[highPtr++];
            copies++;
        }

        for (j = 0; j < n; j++) {
            arr[lowerBound + j] = workSpace[j];
            copies++; // Copy back to original
        }
    }

    // ----------------------------------------------------------------------
    // ALGORITHM 2: SHELL SORT (Listing 7.1 logic)
    // ----------------------------------------------------------------------
    public static void shellSort(int[] arr) {
        int inner, outer;
        int temp;
        int h = 1;

        // Knuth's Interval Sequence
        while (h <= arr.length / 3) {
            h = h * 3 + 1;
        }

        while (h > 0) {
            for (outer = h; outer < arr.length; outer++) {
                temp = arr[outer];
                copies++; // Copy to temp variable
                inner = outer;

                // While loop checks
                while (inner > h - 1 && arr[inner - h] >= temp) {
                    comparisons++; // The check passed
                    arr[inner] = arr[inner - h];
                    copies++; // Shift is a copy
                    inner -= h;
                }
                // Count the final failed comparison if inner didn't reach 0
                if (inner > h - 1) {
                    comparisons++;
                }

                arr[inner] = temp;
                copies++; // Copy from temp back to array
            }
            h = (h - 1) / 3;
        }
    }

    // ----------------------------------------------------------------------
    // ALGORITHM 3: QUICK SORT 3 (Median-of-Three) (Listing 7.5 logic)
    // ----------------------------------------------------------------------
    public static void recQuickSort(int[] arr, int left, int right) {
        int size = right - left + 1;
        // Manual sort for small sizes (standard optimization in Listing 7.5)
        if (size <= 3) {
            manualSort(arr, left, right);
        } else {
            long median = medianOf3(arr, left, right);
            int partition = partitionIt(arr, left, right, median);
            recQuickSort(arr, left, partition - 1);
            recQuickSort(arr, partition + 1, right);
        }
    }

    public static long medianOf3(int[] arr, int left, int right) {
        int center = (left + right) / 2;

        if (arr[left] > arr[center]) {
            swap(arr, left, center);
        }
        if (arr[left] > arr[right]) {
            swap(arr, left, right);
        }
        if (arr[center] > arr[right]) {
            swap(arr, center, right);
        }

        // Comparisons for the above 3 checks
        comparisons += 3;

        swap(arr, center, right - 1); // Put pivot on right
        return arr[right - 1];
    }

    public static int partitionIt(int[] arr, int left, int right, long pivot) {
        int leftPtr = left;
        int rightPtr = right - 1;

        while (true) {
            while (arr[++leftPtr] < pivot) {
                comparisons++;
            }
            comparisons++; // fail count

            while (arr[--rightPtr] > pivot) {
                comparisons++;
            }
            comparisons++; // fail count

            if (leftPtr >= rightPtr) {
                break;
            }
            swap(arr, leftPtr, rightPtr);
        }
        swap(arr, leftPtr, right - 1); // Restore pivot
        return leftPtr;
    }

    public static void manualSort(int[] arr, int left, int right) {
        int size = right - left + 1;
        if (size <= 1) {
            return;
        }
        if (size == 2) {
            comparisons++;
            if (arr[left] > arr[right]) {
                swap(arr, left, right);
            }
            return;
        } else { // size is 3
            if (arr[left] > arr[right - 1]) {
                swap(arr, left, right - 1);
            }
            if (arr[left] > arr[right]) {
                swap(arr, left, right);
            }
            if (arr[right - 1] > arr[right]) {
                swap(arr, right - 1, right);
            }
            comparisons += 3;
        }
    }

    public static void swap(int[] arr, int dex1, int dex2) {
        int temp = arr[dex1];
        arr[dex1] = arr[dex2];
        arr[dex2] = temp;
        swaps++;
    }
}
