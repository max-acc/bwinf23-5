/*
"Aufgabe 5 - Hüpfburg"

@author Max Wenk
@date 18/11/2022

Note: The input file has to be a text.txt file inside the sub folder called "parcours.txt".
The numbers have to be separated by whitespace and a linebreak.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class Main {
    public static void main(String args[]) throws Exception {
        // --- Reading the input file and writing it into a string array -----------------------------------------------
        System.out.println("Reading the text file ...");
        File file = new File("parcours.txt");
        Scanner sc = new Scanner(file);

        // Reading and counting the lines in the text document for creating an array to save the input information
        int rowCount = 0;
        while (sc.hasNextLine()) {
            rowCount++;
            String line = sc.nextLine();
        }

        // Creating a string array which contains the words from the text file
        int[][] readFile = new int[rowCount][2];
        sc = new Scanner(file);
        for (int i = 0; i < rowCount; i++) {
            String str = sc.nextLine();
            String[] splitStr = str.split("\\s+");  // Separates the input by whitespace
            if (splitStr.length == 2) {
                readFile[i][0] = Integer.parseInt(splitStr[0]); // The input is saved in a string and has to be parsed to an integer for further calculations
                readFile[i][1] = Integer.parseInt(splitStr[1]);
            } else {
                System.out.println("An error occurred while reading the input file");
            }
        }
        sc.close();

        // Creating a list array for storing the graph data as useful data
        ArrayList<Integer>[][] connections = new ArrayList[readFile[0][0]][2];
        for (int i = 0; i < readFile[0][0]; i++) {
            connections[i][0] = new ArrayList<>(1);
            connections[i][0].add(0, 0);
            connections[i][1] = new ArrayList<>();
        }

        // --- Transforming the input to useful data -------------------------------------------------------------------
        System.out.println("Transforming the input data");
        // --- Writing the input data as a useful list array => [node][connects to ...]
        for (int i = 0; i < readFile[0][0]; i++) {  // In readFile[0][0] is the number of nodes stored
            outer:
            for (int j = 1; j < readFile[0][1] +1; j++) {   // In readFile[0][1] is the number of overall connections stored
                if (connections[i][0].get(0) == 0 || connections[i][0].get(0) == null) {
                    if (i >= 1) {
                        for (int k = 1; k <= i; k++) {
                            if (connections[i - k][0].get(0) == readFile[j][0]) {
                                continue outer;
                            }
                        }
                    }
                    connections[i][0].set(0, readFile[j][0]);
                }
                if (connections[i][0].get(0) == readFile[j][0]) {
                    connections[i][1].add(readFile[j][1]);

                } else {
                    continue;
                }
            }
        }

        // --- User Input (Node) ---------------------------------------------------------------------------------------
        // Getting the user input data for the starting nodes
        sc = new Scanner(System.in);
        System.out.print("Enter the first starting node:  ");
        int startingPoint1 = sc.nextInt();
        ArrayList<Integer> path1 = new ArrayList<Integer>();

        sc = new Scanner(System.in);
        System.out.print("Enter the second starting node: ");
        int startingPoint2 = sc.nextInt();
        ArrayList<Integer> path2 = new ArrayList<Integer>();
        boolean pathFound = false;

        // Check if the input nodes are in the graph
        for (int i = 1; i < rowCount; i++) {
            if (startingPoint1 == readFile[i][0]) {break;}
            if (i == rowCount-1) {
                System.out.println("The first starting node is not on the graph or has no connections to other nodes (-> there is no possible path)! \nPlease restart the program.");
                System.exit(1);
            }
        }
        for (int i = 1; i < rowCount; i++) {
            if (startingPoint2 == readFile[i][0]) {break;}
            if (i == rowCount-1) {
                System.out.println("The second starting node is not on the graph or has no connections to other nodes (-> there is no possible path)! \nPlease restart the program.");
                System.exit(1);
            }
        }


        // If the starting nodes have the same value, the program stops working -> they have to be different
        if (startingPoint1 == startingPoint2) {
            System.out.println("The starting nodes have to have different values! \nPlease restart the program.");
            System.exit(1);
        }

        // Setting the first path to the connections of the first node
        for (int j = 0; j < readFile[0][0]; j++) {
            if (connections[j][0].get(0) == startingPoint1) {
                path1 = connections[j][1];
            }
            if (connections[j][0].get(0) == startingPoint2) {
                path2 = connections[j][1];
            }
        }

        // --- Finding valid paths -------------------------------------------------------------------------------------
        System.out.println("Finding a valid path ...");
        // Variables for saving important output data
        int pathIteration = 0;
        int pathPoint = 0;

        // If the path is already found after the first iteration -> the nodes only have one directly connected node in-between
        for (int j = 0; j < path1.size(); j++)  {
            if (path2.contains(path1.get(j)))  {
                pathIteration = 1;
                pathPoint = path1.get(j);
                pathFound = true;
            }
        }
        // If no path has been found already -> the nodes do not have one directly connected node in-between -> more or none
        if (!pathFound) {
            validPath:
            for (int i = 0; i < readFile[0][0] * readFile[0][1]; i++) {
                ArrayList<Integer> path1Save = new ArrayList<Integer>();
                ArrayList<Integer> path2Save = new ArrayList<Integer>();

                // Resetting temporary variables and setting them to the current connections (for both)
                path1Save.clear();
                path2Save.clear();
                for (int j = 0; j < path1.size(); j++) {
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (path1.get(j) == connections[k][0].get(0)) {
                            path1Save.addAll(connections[k][1]);

                        }
                    }
                }

                for (int j = 0; j < path2.size(); j++) {
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (path2.get(j) == connections[k][0].get(0)) {
                            path2Save.addAll(connections[k][1]);

                        }
                    }
                }

                // Removing the duplicates in the possible paths => multiple are unnecessary
                path1 = removeDuplicates(path1Save);
                path2 = removeDuplicates(path2Save);

                // --- Check if there are equal elements and saving the number of iterations
                for (int j = 0; j < path1.size(); j++)  {
                    if (path2.contains(path1.get(j)))  {
                        pathIteration = i +2;
                        pathPoint = path1.get(j);
                        pathFound = true;
                        break validPath;
                    }
                }
            }
        }

        // If a valid path has been found, the user gets short information to show that the program is still running
        if (pathFound) {
            System.out.println("A valid path has been found!");
        }


        // --- Finding and saving one valid path -----------------------------------------------------------------------
        // Initialising variables for saving the possible paths
        ArrayList<Integer>[][] path1PossiblePaths = new ArrayList[readFile[0][0]][2];
        ArrayList<Integer>[][] path2PossiblePaths = new ArrayList[readFile[0][0]][2];
        ArrayList<Integer>[][] path1PossiblePathsSave = new ArrayList[readFile[0][0]][2];
        ArrayList<Integer>[][] path2PossiblePathsSave = new ArrayList[readFile[0][0]][2];

        if (pathFound) {
            for (int i = 0; i < readFile[0][0]; i++) {
                path1PossiblePaths[i][0] = new ArrayList<>(1);
                path1PossiblePaths[i][1] = new ArrayList<>();
                path2PossiblePaths[i][0] = new ArrayList<>(1);
                path2PossiblePaths[i][1] = new ArrayList<>();
                path1PossiblePathsSave[i][0] = new ArrayList<>(1);
                path1PossiblePathsSave[i][1] = new ArrayList<>();
                path2PossiblePathsSave[i][0] = new ArrayList<>(1);
                path2PossiblePathsSave[i][1] = new ArrayList<>();
            }
            // The following part for finding valid paths is the same as above
            for (int j = 0; j < readFile[0][0]; j++) {
                if (connections[j][0].get(0) == startingPoint1) {
                    path1 = connections[j][1];
                    //System.out.println(path1);
                }
                if (connections[j][0].get(0) == startingPoint2) {
                    path2 = connections[j][1];
                    //System.out.println(path2);
                }
            }

            // --- Recalculating the paths and saving the different paths
            for (int i = 0; i < pathIteration -1; i++) {
                ArrayList<Integer> path1Save = new ArrayList<Integer>();
                ArrayList<Integer> path2Save = new ArrayList<Integer>();


                path1Save.clear();
                path2Save.clear();
                for (int j = 0; j < path1.size(); j++) {
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (path1.get(j) == connections[k][0].get(0)) {
                            path1Save.addAll(connections[k][1]);
                        }
                    }
                }

                for (int j = 0; j < path2.size(); j++) {
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (path2.get(j) == connections[k][0].get(0)) {
                            path2Save.addAll(connections[k][1]);
                        }
                    }
                }
                path1Save = removeDuplicates(path1Save);    // Removing the duplicates in the possible paths => multiple are unnecessary
                path2Save = removeDuplicates(path2Save);

                // --- Setting the first value in the first iteration
                // --- Finding valid paths and saving them starts here
                for (int j = 0; j < readFile[0][0]; j++) {
                    // Setting the first path with the first node -> has to be initialised as exception because they have to be initialised for further calculations
                    if (connections[j][0].get(0) == startingPoint1 && i == 0) {
                        path1 = connections[j][1];
                        for (int k = 0; k < path1.size(); k++) {
                            path1PossiblePaths[k][0].add(path1.get(k));
                            path1PossiblePaths[k][1].add(startingPoint1);
                            path1PossiblePaths[k][1].add(path1.get(k));
                        }
                    }
                    if (connections[j][0].get(0) == startingPoint2 && i == 0) {
                        path2 = connections[j][1];
                        for (int k = 0; k < path2.size(); k++) {
                            path2PossiblePaths[k][0].add(path2.get(k));
                            path2PossiblePaths[k][1].add(startingPoint2);
                            path2PossiblePaths[k][1].add(path2.get(k));
                        }
                    }
                }

                // Adding the new nodes to the previous path elements
                for (int j = 0; j < path1Save.size(); j++) {
                    path1PossiblePathsSave[j][0].add(path1Save.get(j));
                    outer:
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (connections[k][1].contains(path1Save.get(j))) {
                            for (int l = 0; l < path1.size(); l++) {
                                if (connections[k][0].get(0) == path1.get(l)) {
                                    path1PossiblePathsSave[j][1].addAll(path1PossiblePaths[l][1]);
                                    break outer;
                                }
                            }
                        }
                    }
                    path1PossiblePathsSave[j][1].add(path1Save.get(j));
                }

                for (int j = 0; j < path2Save.size(); j++) {
                    path2PossiblePathsSave[j][0].add(path2Save.get(j));
                    outer:
                    for (int k = 0; k < readFile[0][0]; k++) {
                        if (connections[k][1].contains(path2Save.get(j))) {
                            for (int l = 0; l < path2.size(); l++) {
                                if (connections[k][0].get(0) == path2.get(l)) {
                                    path2PossiblePathsSave[j][1].addAll(path2PossiblePaths[l][1]);
                                    break outer;
                                }
                            }
                        }
                    }
                    path2PossiblePathsSave[j][1].add(path2Save.get(j));
                }

                // Clearing the array list with the path data -> to prevent unexpected errors
                for (int j = 0; j < readFile[0][0]; j++) {
                    path1PossiblePaths[j][0].clear();
                    path1PossiblePaths[j][1].clear();
                }
                // Setting the new path data to the result path data (for further calculations) and clearing the path data element by element (=> it caused storage errors before)
                for (int j = 0; j < path1Save.size(); j++) {
                    path1PossiblePaths[j][0].add(path1PossiblePathsSave[j][0].get(0));
                    path1PossiblePaths[j][1].addAll(path1PossiblePathsSave[j][1]);
                    path1PossiblePathsSave[j][0].remove(0);
                    for (int k = path1PossiblePathsSave[j][1].size() -1; k >= 0; k--) {
                        path1PossiblePathsSave[j][1].remove(k);
                    }
                }

                // Same as above, just for the other path
                for (int j = 0; j < readFile[0][0]; j++) {
                    path2PossiblePaths[j][0].clear();
                    path2PossiblePaths[j][1].clear();
                }
                for (int j = 0; j < path2Save.size(); j++) {
                    path2PossiblePaths[j][0].add(path2PossiblePathsSave[j][0].get(0));
                    path2PossiblePaths[j][1].addAll(path2PossiblePathsSave[j][1]);
                    path2PossiblePathsSave[j][0].remove(0);
                    for (int k = path2PossiblePathsSave[j][1].size() -1; k >= 0; k--) {
                        path2PossiblePathsSave[j][1].remove(k);
                    }
                }
                path1 = path1Save;
                path2 = path2Save;
            }
        }

        // --- Printing an output --------------------------------------------------------------------------------------
        if (pathFound) {
            System.out.print("\n\nThey will meet after ");
            System.out.print(pathIteration);
            System.out.print(" iterations at the node ");
            System.out.println(pathPoint);
            if (pathIteration == 1) {
                System.out.println("There is just one node in-between");
            }
            if (pathIteration > 1) {
                System.out.print("\nPath for starting node 1: ");
                for (int i = 0; i < readFile[0][0]; i++) {
                    if (pathPoint == path1PossiblePaths[i][0].get(0)) {
                        System.out.println(path1PossiblePaths[i][1]); break;}
                }

                System.out.print("Path for starting node 2: ");
                for (int i = 0; i < readFile[0][0]; i++) {
                    if (pathPoint == path2PossiblePaths[i][0].get(0)){
                        System.out.println(path2PossiblePaths[i][1]); break;}
                }
            }
        }else if (!pathFound) {
            System.out.print("\n\nThe paths won't meet!");
        }
    }


    // --- Functions for simplifying the program -----------------------------------------------------------------------
    // --- Function for removing duplicates inside a list
    public static ArrayList<Integer> removeDuplicates (ArrayList<Integer> list) {
        ArrayList<Integer> newList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++)  {
            if (!newList.contains(list.get(i)))  {
                newList.add(list.get(i));
            }
        }
        return newList;
    }
}
