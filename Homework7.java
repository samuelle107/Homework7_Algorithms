import java.io.*;
import java.util.*;

public class Homework7 {
    static File inputFile;
    static int totalActivities, intervalSize;
    static Activity[] activities; // Array of all of the activities
    static int[] memoTable;
    static int maxValue;
    static ArrayList<Integer> matchingValues;
    static ArrayList<Integer> includedJobs;


    static int latestNonConflicingActivity(int i) {
        for (int j = i - 1; j >= 0; j--) {
            if (activities[j].finishTime <= activities[i].startTime) {
                return j;
            }
        }
        return -1;
    }

    static int findMaxValue() {
        // memoTable[i] stores the max profit for activities til arr[i] (including arr[i])
        memoTable = new int[totalActivities];
        memoTable[0] = activities[0].value;
        matchingValues = new ArrayList<>();
        includedJobs = new ArrayList<>();

        for (int i = 1; i < totalActivities; i++) {
            int includedValue = activities[i].value;
            int nextCompatibleActivity = latestNonConflicingActivity(i); // Returns -1 if there are no activities that do not conflict with activities[i], else returns index of next available activity

            if (nextCompatibleActivity != -1) {
                includedValue += memoTable[nextCompatibleActivity];
            }

            if (includedValue == memoTable[i - 1]) { // Used to check if there are multiple solutions
                matchingValues.add(includedValue);

                if (activities[i].value + memoTable[nextCompatibleActivity] > memoTable[i - 1]) {
                    includedJobs.add(i);
                }
            }

            memoTable[i] = Math.max(includedValue, memoTable[i - 1]);
        }

        int result = memoTable[totalActivities - 1];
        maxValue = result;

        int j = totalActivities - 1;
        while (j > 0) {
            int nextCompatibleActivity = latestNonConflicingActivity(j);
            if (activities[j].value + memoTable[nextCompatibleActivity] > memoTable[j - 1]) {
                includedJobs.add(activities[j].id);
                j = nextCompatibleActivity;
            } else {
                j -= 1;
            }
        }

        return result;
    }

    static void readInputFile() {
        try {
            BufferedReader bufferedReader  = new BufferedReader(new FileReader(inputFile));
            String st;
            int lineNumber = 1;

            while ((st = bufferedReader.readLine()) != null) {
                String[] stringArray = st.split(" "); // Split the line by spaces and makes a String array
                Integer[] integerArray = new Integer[stringArray.length];

                for (int i = 0; i < stringArray.length; i++) { // Convert the string values from the string array and add it into an int array
                    integerArray[i] = Integer.parseInt(stringArray[i]);
                }

                if (lineNumber == 1) { // Line 1 is not an activity, but initialization values.
                    totalActivities = integerArray[0] + 1;
                    intervalSize = integerArray[1];
                    activities = new Activity[totalActivities];
                    Activity ac = new Activity(0, 0, 0, 0);
                    activities[0] = ac;
                }
                else { // All lines after 1 are activities
                    Activity activity = new Activity(integerArray[0], integerArray[1], integerArray[2], integerArray[3]);
                    activities[lineNumber -1] = activity;
                }
                lineNumber ++;
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }

    static void createOutput() {
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println(maxValue);
            writer.println("The optimal jobs are: " + Arrays.toString(includedJobs.toArray()));
            writer.println(matchingValues.contains(maxValue) ? "THIS HAS MULTIPLE SOLUTIONS" : "THIS HAS A UNIQUE SOLUTION");
            writer.close();
        } catch (Exception e) {
            System.out.print(e);
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0  ) {
            System.out.println("Enter: java Homework7 input1.txt");
            System.exit(-1);
        }

        inputFile = new File(args[0]);

        readInputFile(); // Reads the current input file and converts it and transforms it into an arraylist of activities

        Arrays.sort(activities, (a,b) -> Integer.compare(a.finishTime, b.finishTime));
        findMaxValue();
        createOutput();
    }
}

class Activity { // Implement Comparable to be able to sort by startTime
    int id;
    int startTime;
    int finishTime;
    int value;

    Activity(int id, int startTime, int finishTime, int value) {
        this.id = id;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.value = value;
    }
}
