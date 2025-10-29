package sorting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;

public class SortingAlgorithms {

    // Number of warm-ups and test iterations
    private static final int WARMUP_ITERATIONS = 5;
    private static final int TEST_ITERATIONS = 10;


    public static void insertionSort(String[] arr) {
        for (int i = 1; i < arr.length; i++) {
            String key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j].compareTo(key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }


    public static void quickSort(String[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(String[] arr, int low, int high) {
        if (low < high) {

            int pi = partition(arr, low, high);


            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(String[] arr, int low, int high) {

        String pivot = arr[high];

        int i = low - 1;

        for (int j = low; j < high; j++) {

            if (arr[j].compareTo(pivot) <= 0) {
                i++;


                String temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }


        String temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

// merge
    public static void mergeSort(String[] arr) {

        String[] temp = new String[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }


    private static void mergeSort(String[] arr, String[] temp, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Recursive sorting left and right
            mergeSort(arr, temp, left, mid);
            mergeSort(arr, temp, mid + 1, right);

            // Normalize the sorted subarrays
            merge(arr, temp, left, mid, right);
        }
    }

    private static void merge(String[] arr, String[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }

        int i = left;      // left
        int j = mid + 1;   // right
        int k = left;      // previous

        // merge
        while (i <= mid && j <= right) {
            if (temp[i].compareTo(temp[j]) <= 0) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }


        while (i <= mid) {
            arr[k++] = temp[i++];
        }


    }

// read date
    public static String[] loadDataset(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();


        try {

            InputStream is = SortingAlgorithms.class.getResourceAsStream("/sorting/data/" + filePath);


            if (is == null) {
                is = SortingAlgorithms.class.getResourceAsStream("/data/" + filePath);
            }


            if (is == null) {
                try (BufferedReader br = new BufferedReader(new FileReader("src/sorting/data/" + filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (IOException e) {

                    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                        }
                    }
                }
            } else {

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dataset: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Unable to load dataset " + filePath, e);
        }

        return lines.toArray(new String[0]);
    }

//analysis
    private static void analyzeDataset(String[] data) {
        int sortedCount = 0;
        for (int i = 1; i < data.length; i++) {
            if (data[i-1].compareTo(data[i]) <= 0) {
                sortedCount++;
            }
        }

    }

//Warm-up all sort
    private static void warmupSortingAlgorithms(String[] data) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {

            String[] insertionData = data.clone();
            insertionSort(insertionData);


            String[] quickData = data.clone();
            quickSort(quickData);


            String[] mergeData = data.clone();
            mergeSort(mergeData);
        }
    }

//test
    public static Map<String, Long> testDataset(String datasetPath) throws IOException {
        Map<String, Long> results = new HashMap<>();

        String[] data = loadDataset(datasetPath);


        analyzeDataset(data);


        warmupSortingAlgorithms(data);


        long insertionTotal = 0;
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String[] insertionData = data.clone();
            long startTime = System.nanoTime();
            insertionSort(insertionData);
            long endTime = System.nanoTime();
            insertionTotal += (endTime - startTime);
        }
        results.put("Insertion", insertionTotal / TEST_ITERATIONS);


        long quickTotal = 0;
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String[] quickData = data.clone();
            long startTime = System.nanoTime();
            quickSort(quickData);
            long endTime = System.nanoTime();
            quickTotal += (endTime - startTime);
        }
        results.put("Quick", quickTotal / TEST_ITERATIONS);


        long mergeTotal = 0;
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String[] mergeData = data.clone();
            long startTime = System.nanoTime();
            mergeSort(mergeData);
            long endTime = System.nanoTime();
            mergeTotal += (endTime - startTime);
        }
        results.put("Merge", mergeTotal / TEST_ITERATIONS);

        return results;
    }

// time
    public static String formatTime(long timeNanos) {

        double timeMillis = timeNanos / 1_000_000.0;


        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setGroupingUsed(true);
        formatter.setMaximumFractionDigits(3);
        formatter.setMinimumFractionDigits(3);

        return formatter.format(timeMillis);
    }


    public static void main(String[] args) {

        String[] datasets = {
                "1000places_sorted.csv",
                "1000places_random.csv",
                "10000places_sorted.csv",
                "10000places_random.csv"
        };

        try {
            for (String dataset : datasets) {
                System.out.println(dataset + ":");

                Map<String, Long> results = testDataset(dataset);


                System.out.println("  Insertion Sort: " + formatTime(results.get("Insertion")) + " ms");
                System.out.println("  Quick Sort: " + formatTime(results.get("Quick")) + " ms");
                System.out.println("  Merge Sort: " + formatTime(results.get("Merge")) + " ms");
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Error testing datasets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}