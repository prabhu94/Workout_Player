package hw3;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CSVFiler extends DataFiler{

	@Override
	public ObservableList<Exercise> readData(String filename) {
		List<Exercise> workouts = new ArrayList<>();
		// Reading the file input
				Scanner input = null;
				File file = new File(filename);
				try {
					input = new Scanner(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Using StringBuilder Class to take the input from the file
				StringBuilder inputLinesBuilder = new StringBuilder();
				while (input.hasNextLine()) {
					inputLinesBuilder.append(input.nextLine() + "\n");
				}

				// Using Split method to split the stringbuilder to string array for adding
				// datapoints into allexercises object
				String[] inputLines = inputLinesBuilder.toString().split("\n");
				for (int n = 0; n < inputLines.length; n++) {
					//Using delimiter [;]+\\s? for splitting each column in the document
					Scanner dataPoints = new Scanner(inputLines[n]).useDelimiter("[,]+\\s?");
					while (dataPoints.hasNext()) {
						workouts.add(new Exercise(dataPoints.next(), dataPoints.next(), dataPoints.nextInt(),
								dataPoints.nextInt(), dataPoints.nextInt(), dataPoints.next(), dataPoints.next()));
					}
				}
		return FXCollections.observableArrayList(workouts);
	}



	public void writeData(ObservableList<Exercise> selectedExercises, File file) {
		
		// Using stringbuilder to write data
		StringBuilder data = new StringBuilder();
		if (file != null) {
			for (Exercise exercise : selectedExercises) {
				String exerciseNotes = exercise.getExerciseNotes();
				// filter out unwanted punctuations and lines
				exerciseNotes =	exerciseNotes.replace(",", "");
				exerciseNotes = exerciseNotes.replace("\n", "");
				data.append(exercise.getName() + "," + exercise.getLevel() + "," + exercise.getRepTime() + ","
						+ exercise.getRepCount() + "," + exercise.getCalories() + "," + exercise.getImageFile()
						+ "," + exerciseNotes);
				data.append("\n");
			}
		}
		String output = data.toString();
		// Filewriter code to write the data to file
		try {
			FileWriter fileWriter;
			fileWriter = new FileWriter(file);
			fileWriter.write(output);
			fileWriter.close();
		} catch (Exception ex) {
			System.out.println("File not being saved. Exception thrown!!");
		}
		
	}
}
