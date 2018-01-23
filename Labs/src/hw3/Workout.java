package hw3;

import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Workout {
	int timeSpent, caloriesBurned;
	public ObservableList<Exercise>  buildWorkoutPlan(List<Exercise> exercisesAll, int timeInput, int caloriesInput) {
		//new code
		timeSpent =0 ;
		caloriesBurned=0;
		ObservableList<Exercise> suggestedExercisesList = FXCollections.observableArrayList();
		

		// Creating the suggestedExercises object array with exercises
		Exercise[] suggestedExercises = new Exercise[exercisesAll.size()];
		for (int i = 0; i < suggestedExercises.length; i++) {
			suggestedExercises[i] = new Exercise(exercisesAll.get(i).getName(), 
					exercisesAll.get(i).getLevel(), 
					exercisesAll.get(i).getRepTime(),
					exercisesAll.get(i).getRepCount(), 
					exercisesAll.get(i).getCalories(), 
					exercisesAll.get(i).getImageFile(),
					exercisesAll.get(i).getExerciseNotes());

		}

		// Logic to calculate the number of times each exercise figures in the workout
		// plan
		int[] numberOfPasses = new int[suggestedExercises.length];
		while ((timeInput > 0 || caloriesInput > 0)) {
			for (int i = 0; i < exercisesAll.size(); i++) {
				if ((timeInput > 0 || caloriesInput > 0)) {
					timeInput -= suggestedExercises[i].repTime.get();
					caloriesInput -= suggestedExercises[i].calories.get();
					numberOfPasses[i]++;
				}
			}
		}

		// Logic to manipulate each exercise value based on the number of times they
		// occur
		for (int i = 0; i < suggestedExercises.length; i++) {
			suggestedExercises[i].calories.set(suggestedExercises[i].calories.get() * numberOfPasses[i]);
			suggestedExercises[i].repCount.set(suggestedExercises[i].repCount.get() * numberOfPasses[i]);
			suggestedExercises[i].repTime.set(suggestedExercises[i].repTime.get() * numberOfPasses[i]);
			timeSpent += suggestedExercises[i].repTime.get();
			caloriesBurned += suggestedExercises[i].calories.get();
		}
		suggestedExercisesList.addAll(Arrays.asList(suggestedExercises));
		return suggestedExercisesList;
	}
}
