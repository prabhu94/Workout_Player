/* Name : Prabhat turlapati
*  ID : sturlapa
*/
package hw3;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class PersonalTrainer extends Application {

	/******* all HW2 member variables here ********/
	public PTViewer ptView = new PTViewer(); // will perform all view-related operations that do not need
												// data-components
	public PTData ptData = new PTData(); // will perform all data-related operations that do not need view-components
	Stage mainStage; // to be used for FileChooser in OpenHandler
	GridPane workoutGrid; // will hold the central grid populated with GUI components and will be attached
							// to root in New and Open handlers
	DataFiler dataFiler; // will hold CSVFiler or XMLFiler
	Exercise currentExercise; // this points to whichever exercise is selected in exerciseComboBox or in
								// exercisetableView
	int minutes; // will hold the calculated minutes value
	int count; // will hold the calculated count value
	int calories; // will hold the calculated calories value
	int totalTime; // will hold the calculated total time value
	int totalCalories; // will hold the calculated total calories Value
	private String inputFilename;// inputfilename variable;
	/***********************************/

	/** New or changed member variables here */
	static final String PT_DATA_PATH = "resources"; // relative path for all data files to reside
	static final String PT_IMAGE = "personaltrainer.jpg"; // Welcome image
	static final String PT_MUSIC = "Kalimba.mp3"; // audio played in background for images
	static final String PT_YOUDIDIT_IMAGE = "youdidit.jpg"; // workout completion image
	WorkoutViewer workoutViewer;
	WorkoutPlayer player = new WorkoutPlayer(); // Used in Play or Close handlers
	static MediaPlayer videoPlayer, audioPlayer;
	SuggestWindow suggestWindow = new SuggestWindow(); // Suggest Window object for the suggest button functionality
	Stage suggestStage = new Stage(); // The stage for the suggest window

	/****************************/

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * start() method creates the opening screen with menus. It also creates the
	 * screenGrid by invoking setupScreen() method of PTViewer class but doesn't
	 * attach it to the root yet *
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Setting the stage for the application
		mainStage = primaryStage;
		mainStage.setTitle("Personal Trainer");
		ptView.setupMenus();
		// disabling the close,save,play and suggest buttons till the file is loaded
		ptView.closeWorkoutMenuItem.setDisable(true);
		ptView.saveWorkoutMenuItem.setDisable(true);
		ptView.playWorkoutMenuItem.setDisable(true);
		ptView.suggestWorkoutMenuItem.setDisable(true);
		ptView.setupWelcomeScreen();
		Background b = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
		ptView.root.setBackground(b);
		Scene scene = new Scene(ptView.root, 875, 500);
		mainStage.setScene(scene);
		workoutGrid = ptView.setupScreen(); // populate the grid, but don't attach to the root yet
		setActions();
		mainStage.show();

	}

	/**
	 * loadInputGrid() populates various components in the inputGrid. It links
	 * exerciseComboBox with masterData It also attaches listeners to timeSlider and
	 * exerciseComboBox to change the labels for timeValue, caloriesValue, and
	 * repsCountValue. One new handler is for notesTextArea when user types in some
	 * text into it. Use setOnKeyTyped() handler for this. You may attach it here as
	 * anonymous class or as a member class in setupActions method
	 */
	private void loadInputGrid() {

		// loading the workout application grid for first use
		loadSelectionGrid(ptData.selectedExercises);
		ptView.exerciseComboBox.setPromptText("Select an exercise");
		ptView.exerciseComboBox.setItems(ptData.masterData);

		//
		calorieTimeUpdater();
		ptView.caloriesValue.setText(0 + "");
		ptView.repsCountValue.setText(0 + "");
		ptView.timeValue.setText(0 + "");
		ptView.exerciseTableView.getSelectionModel().selectFirst();
		showImage(ptData.selectedExercises.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex())
				.getImageFile(), ptView.imageStackPane);
		ptView.notesTextArea.setText(ptData.selectedExercises
				.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex()).getExerciseNotes());

		// listener for the table view, listens for changes in addition and removal of
		// exercise.
		ptView.exerciseTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Exercise>() {
			@Override
			public void changed(ObservableValue<? extends Exercise> observable, Exercise oldValue, Exercise newValue) {
				try {
					if (ptView.exerciseTableView.getSelectionModel().getSelectedItem() != null) {
						ptView.updateButton.setStyle("-fx-text-fill:black;");
						showImage(ptData.selectedExercises
								.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex()).getImageFile(),
								ptView.imageStackPane);
						ptView.notesTextArea.setText(ptData.selectedExercises
								.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex())
								.getExerciseNotes());
					} else {
						showImage(PT_IMAGE, ptView.imageStackPane);
						ptView.notesTextArea.setText("");
					}
				} catch (Exception e) {
					// leaving it empty, try-catch added as an additional check
				}
			}
		});

		// listener for changes in combo box, which means setting slider value and the
		// lables for calories, time and rep count
		ptView.exerciseComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Exercise>() {

			@Override
			public void changed(ObservableValue<? extends Exercise> observable, Exercise oldValue, Exercise newValue) {
				if (ptView.exerciseComboBox.getSelectionModel().getSelectedItem() != null) {
					// setting minutes, count and calories
					ptView.updateButton.setStyle("-fx-text-fill:black;");
					minutes = ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getRepTime();
					count = ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getRepCount();
					calories = ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getCalories();
					ptView.timeValue.setText(minutes + "");
					ptView.repsCountValue.setText(count + "");
					ptView.caloriesValue.setText(calories + "");
					ptView.timeSlider.setValue(minutes);
					showImage(ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getImageFile(),
							ptView.imageStackPane);
					ptView.notesTextArea
							.setText(ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getExerciseNotes());

				}
			}

		});

		// adding the listener for time slider to reflect changes in slider
		ptView.timeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				try {

					if (ptView.exerciseComboBox.getSelectionModel().getSelectedItem() != null) {
						minutes = (int) Math.round(newValue.doubleValue());
						count = (int) ((double) ptView.exerciseComboBox.getSelectionModel().getSelectedItem()
								.getRepCount() * minutes
								/ (double) ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getRepTime());
						calories = (ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getCalories()
								/ ptView.exerciseComboBox.getSelectionModel().getSelectedItem().getRepTime()) * minutes;
						ptView.timeSlider.setValue(newValue.doubleValue());
						ptView.timeValue.setText(minutes + "");
						ptView.repsCountValue.setText(count + "");
						ptView.caloriesValue.setText(calories + "");
						ptView.timeSlider.setValue(minutes);
					}
				} catch (Exception e) {
					// leaving it empty, try-catch added as an additional check
				}
			}
		});

		// add button handler logic to add the exercise into table view.
		ptView.addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				ptView.updateButton.setStyle("-fx-text-fill:black;");
				if (ptView.exerciseComboBox.getSelectionModel().getSelectedItem() != null) {
					Exercise selected = ptView.exerciseComboBox.getSelectionModel().getSelectedItem();
					currentExercise = new Exercise(selected.getName(), selected.getLevel(), minutes, count, calories,
							selected.getImageFile(), selected.getExerciseNotes());
					ptData.selectedExercises.add(currentExercise);
					ptView.exerciseTableView.setItems(ptData.selectedExercises);
					calorieTimeUpdater();
					ptView.exerciseTableView.getSelectionModel().selectLast();
				}

			}

		});

		// remove button handler logic to remove the exercise from table view.
		ptView.removeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					ptView.updateButton.setStyle("-fx-text-fill:black;");
					if (!ptData.selectedExercises.isEmpty() && ptData.selectedExercises.size() > 0) {
						ptData.selectedExercises
								.remove(ptView.exerciseTableView.getSelectionModel().getSelectedIndex());
						calorieTimeUpdater();
					}
				} catch (Exception e) {
					// leaving it empty, try added as an additional check
				}

			}

		});

		ptView.playWorkoutMenuItem.setOnAction(new PlayWorkoutHandler());

		// On key typed event listener to change the colors in case the search notes
		// don't match the actual exercise notes
		ptView.notesTextArea.setOnKeyTyped(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					if (!ptView.notesTextArea.getText().equals(ptData.selectedExercises
							.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex()).getExerciseNotes()))
						ptView.updateButton.setStyle("-fx-text-fill:red;");
					else {
						ptView.updateButton.setStyle("-fx-text-fill:black;");
					}
				} catch (Exception e) {
					// Additional check
				}
			}

		});

		// search button handler to search the data for the string given
		ptView.searchButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
				ptView.updateButton.setStyle("-fx-text-fill:black;");
				ptView.searchTextField.setOnKeyTyped(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						ptView.searchTextField.setStyle("-fx-text-fill:black;");
					}
				});
				String search = ptView.searchTextField.getText();
				ObservableList<Exercise> searchList = FXCollections.observableArrayList();

				//Search three fields exercise name, level and notes
				for (Exercise exercise : ptData.masterData) {
					if (exercise.getName().contains(search) || exercise.getLevel().contains(search)
							|| exercise.getExerciseNotes().contains(search)) {
						searchList.add(exercise);
					}
				}
				
				// if size is more than 0 and the size  != master data
				if (searchList.size() > 0 && searchList.size() != ptData.masterData.size()) {
					ptData.selectedExercises.clear();
					for (Exercise exercise : searchList) {
						Exercise temp = new Exercise(exercise.getName(), exercise.getLevel(), exercise.getRepTime(),
								exercise.getRepCount(), exercise.getCalories(), exercise.getImageFile(),
								exercise.getExerciseNotes());
						ptData.selectedExercises.add(temp);
					}
					ptView.exerciseTableView.setItems(ptData.selectedExercises);
				} else if (searchList.size() == ptData.masterData.size()) {
					ptData.selectedExercises.clear();
					for (Exercise exercise : searchList) {
						Exercise temp = new Exercise(exercise.getName(), exercise.getLevel(), exercise.getRepTime(),
								exercise.getRepCount(), exercise.getCalories(), exercise.getImageFile(),
								exercise.getExerciseNotes());
						ptData.selectedExercises.add(temp);
					}
					ptView.exerciseTableView.setItems(ptData.selectedExercises);

				} else {
					ptView.searchTextField.setText("");
					ptView.searchTextField.setText(search + " not found");
					ptView.searchTextField.setStyle("-fx-text-fill:red;");
					ptData.selectedExercises.clear();

					for (Exercise exercise : ptData.masterData) {
						Exercise temp = new Exercise(exercise.getName(), exercise.getLevel(), exercise.getRepTime(),
								exercise.getRepCount(), exercise.getCalories(), exercise.getImageFile(),
								exercise.getExerciseNotes());
						ptData.selectedExercises.add(temp);
					}
					ptView.exerciseTableView.setItems(ptData.selectedExercises);
				}
				calorieTimeUpdater();
				ptView.exerciseTableView.getSelectionModel().selectFirst();
			}
			
			catch (Exception e) {
				//
			}
		}
		});

		// update button handler to handle changes to the notes
		ptView.updateButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					ptView.updateButton.setStyle("-fx-text-fill:black;");
					ptData.selectedExercises.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex())
							.setExerciseNotes(ptView.notesTextArea.getText());
					calorieTimeUpdater();
				} catch (Exception e) {
					// leaving it empty, try added as an additional check
				}

			}
		});
	}

	/**
	 * loadSelectionGrid() reads the data from data file and populates the
	 * components in selectionGrid. This method is called in two scenarios. One -
	 * when user opens a data file, and two- when the user invokes the menu option
	 * Tools-Suggest. The workout loaded from the data file or as suggested in the
	 * SuggestMenuItemHandler is passed as a parameter selectedExercises to this
	 * method. This method first clears the table-view from past-data, if any, and
	 * then adds all exercises from selectedExercises array to various components.
	 * 
	 * @param selectedExercises
	 */
	void loadSelectionGrid(ObservableList<Exercise> selectedExercises) {

		// Initializing the selected exercises to reflect the data in the file chosen
		selectedExercises = FXCollections.observableArrayList();
		String filename = PT_DATA_PATH + "/" + inputFilename;
		ptData.selectedExercises.clear();
		// This method will load the data into the ptData variables
		ptData.loadData(filename);
		// Setting all the required lists with the data
		for (Exercise exercise : ptData.masterData) {
			Exercise temp = new Exercise(exercise.getName(), exercise.getLevel(), exercise.getRepTime(),
					exercise.getRepCount(), exercise.getCalories(), exercise.getImageFile(),
					exercise.getExerciseNotes());
			selectedExercises.add(temp);
		}
		ptData.selectedExercises.addAll(selectedExercises);
		ptView.exerciseTableView.setItems(ptData.selectedExercises);
	}

	/**
	 * setActions() method attaches all action-handlers to their respective GUI
	 * components. All GUI has been defined in PTViewer.
	 */
	private void setActions() {
		// The various GUI menu components are handled here
		ptView.openWorkoutMenuItem.setOnAction(new OpenWorkoutHandler());
		ptView.closeWorkoutMenuItem.setOnAction(new CloseWorkoutHandler());
		ptView.aboutHelpMenuItem.setOnAction(new AboutHandler());
		ptView.saveWorkoutMenuItem.setOnAction(new SaveWorkoutHandler());
		ptView.suggestWorkoutMenuItem.setOnAction(new SuggestWorkoutHandler());
		ptView.exitWorkoutMenuItem.setOnAction(new ExitWorkoutHandler());

	}

	// write your event handlers' inner classes here
	private class CloseWorkoutHandler implements EventHandler<ActionEvent> {
		
		// The handler for closing the workout
		@Override
		public void handle(ActionEvent event) {
			try {
				ptView.root.setCenter(null);
				ptData.masterData.clear();
				ptData.selectedExercises.clear();
				ptView.exerciseTableView.setItems(ptData.masterData);
				ptView.openWorkoutMenuItem.setDisable(false);
				ptView.closeWorkoutMenuItem.setDisable(true);
				ptView.saveWorkoutMenuItem.setDisable(true);
				ptView.playWorkoutMenuItem.setDisable(true);
				ptView.suggestWorkoutMenuItem.setDisable(true);
				ptView.clearScreen();
				ptView.setupWelcomeScreen();
				ptView.searchTextField.setText("");
				ptView.searchTextField.setStyle("-fx-text-fill:black;");
			} catch (Exception e) {
				System.out.println("Caught exception on exercise table view null and close handler after that");
			}

		}
	}

	/**
	 * OpenWorkoutHandler has been provided as a dummy to display the workoutGrid.
	 * The workoutGrid needs to be populated with data in this handler
	 */
	private class OpenWorkoutHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			// Handling Exceptions that may occur, other than FileNotFound, hence, using
			// general Exception class
			File file = null;
			try {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select File");
				fileChooser.setInitialDirectory(new File(PT_DATA_PATH));
				file = fileChooser.showOpenDialog(mainStage);
				inputFilename = file.getName();
				ptView.workoutNameValue.setText(inputFilename);
				// Loading
				loadInputGrid();
				ptView.root.setBottom(null);
				ptView.root.setCenter(workoutGrid);
				ptView.openWorkoutMenuItem.setDisable(true);
				ptView.closeWorkoutMenuItem.setDisable(false);
				ptView.saveWorkoutMenuItem.setDisable(false);
				ptView.playWorkoutMenuItem.setDisable(false);
				ptView.suggestWorkoutMenuItem.setDisable(false);

			} catch (Exception e) {
				if (file == null) {
					System.out.println("please select something!!");
				} else {
					// alert for the file format exception
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("File Format Error");
					alert.setHeaderText("The Personal Trainer");
					alert.setContentText("Invalid format in " + inputFilename
							+ "\n Expected CSV format String, String, int, int, String, String");
					alert.showAndWait();
				}
			}
		}
	}

	// The save functionality
	private class SaveWorkoutHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			try {

				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter csvfilter = new ExtensionFilter("CSV Files", "*.csv");
				FileChooser.ExtensionFilter xmlfilter = new ExtensionFilter("XML Files", "*.xml");
				fileChooser.getExtensionFilters().addAll(csvfilter, xmlfilter);
				fileChooser.setTitle("Save as");
				fileChooser.setInitialDirectory(new File(PT_DATA_PATH));
				File file = fileChooser.showSaveDialog(mainStage);
				inputFilename = file.getName();

				// usage of polymorphism to save data into xml or csv files
				if (inputFilename.contains("xml")) {
					DataFiler xmlDataFiler = new XMLFiler();
					xmlDataFiler.writeData(ptData.selectedExercises, file);
				} else {
					DataFiler csvDataFiler = new CSVFiler();
					csvDataFiler.writeData(ptData.selectedExercises, file);
				}
				ptView.workoutNameValue.setText(inputFilename);
				ObservableList<Exercise> tempSelectedExercises = FXCollections.observableArrayList();
				for (Exercise exercise : ptData.selectedExercises) {
					Exercise temp = new Exercise(exercise.getName(), exercise.getLevel(), exercise.getRepTime(),
							exercise.getRepCount(), exercise.getCalories(), exercise.getImageFile(),
							exercise.getExerciseNotes());
					tempSelectedExercises.add(temp);
				}
				ptView.exerciseComboBox.setItems(tempSelectedExercises);
			} catch (Exception e) {
				System.out.println("please write something!!");
			}
		}

	}

	private class AboutHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("The Personal Trainer");
			alert.setContentText(
					"Version 1.0 \nRelease 1.0\nCopyleft Java Nerds\nThis software is designed purely for educational purposes.\nNo commercial use intended");
			Image image = new Image(getClass().getClassLoader().getResourceAsStream(PT_IMAGE));
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			imageView.setFitWidth(300);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			alert.setGraphic(imageView);
			alert.showAndWait();
		}
	}

	private void calorieTimeUpdater() {
		
		// Initializing and calculating
		totalCalories = 0;
		totalTime = 0;
		for (Exercise exercise : ptView.exerciseTableView.getItems()) {

			totalTime += exercise.getRepTime();
			totalCalories += exercise.getCalories();
		}
		ptView.totalTimeValue.setText(totalTime + "");
		ptView.totalCaloriesValue.setText(totalCalories + "");
	}

	private void showImage(String filename, StackPane stackPane) {
		WorkoutViewer.createViewer(filename).view(stackPane);
	}

	// To be attached to Play menu item that will be activated only when a file is
	// open.
	// You should not need to make any changes to this handler.
	private class PlayWorkoutHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			player.pauseButton.setDisable(false); // if last playlist was completed, the button would be disabled
			player.pauseButton.setText("Pause"); // if the player was closed with this button on Resume status, it needs
													// to be reset back to "Pause"
			player.playWorkout(ptData.selectedExercises); // play the exercises stored in selectedExercises
		}
	}

	// exit handler when exit button is pressed
	private class ExitWorkoutHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Platform.exit();
		}
	}

	// suggest implementation, where there are two buttons suggest and cancel
	private class SuggestWorkoutHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {

			suggestStage.setScene(new Scene(suggestWindow.setupScreen()));
			suggestStage.show();
			suggestWindow.minutesInput.setText("0");
			suggestWindow.caloriesInput.setText("0");

			// suggest button actions
			suggestWindow.suggest.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						Workout workout = new Workout();
						String minuteText = suggestWindow.minutesInput.getText();
						String calorieText = suggestWindow.caloriesInput.getText();
						
						// If it is a word
						if ((!suggestWindow.minutesInput.getText().matches("\\d+") && minuteText.length() > 0)
								|| (!suggestWindow.caloriesInput.getText().matches("\\d+")
										&& calorieText.length() > 0)) {
							
							System.out.println("Invalid entry");
						} else {
							
							// if null input
							if (!suggestWindow.minutesInput.getText().matches("\\d+")) {
								minuteText = "0";

							}
							
							// if null input
							if (!suggestWindow.caloriesInput.getText().matches("\\d+")) {
								calorieText = "0";
							}
							int minutes = Integer.parseInt(minuteText);
							int calories = Integer.parseInt(calorieText);
							
							// if both are zero
							if ((minutes == 0 && calories == 0)) {
								suggestStage.close();
							} else {
								ptData.selectedExercises.clear();
								ptData.selectedExercises
										.addAll(workout.buildWorkoutPlan(ptData.masterData, minutes, calories));
								ptView.exerciseTableView.setItems(ptData.selectedExercises);
								calorieTimeUpdater();
								ptView.exerciseTableView.getSelectionModel().selectFirst();
								showImage(ptData.selectedExercises
										.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex())
										.getImageFile(), ptView.imageStackPane);
								ptView.notesTextArea.setText(ptData.selectedExercises
										.get(ptView.exerciseTableView.getSelectionModel().getSelectedIndex())
										.getExerciseNotes());
								suggestStage.close();
							}
						}
					} catch (Exception e) {
						System.out.println("Invalid input!!");
					}

				}
			});

			// cancel button actions
			suggestWindow.cancel.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					suggestStage.close();
				}
			});
		}

	}

	class SuggestWindow {
		
		//initializing GUI components
		Label warning = new Label("0 input will be ignored");
		Label minutes = new Label("Enter time in minutes");
		Label calories = new Label("Enter calories to burn");
		TextField minutesInput = new TextField();
		TextField caloriesInput = new TextField();
		Button suggest = new Button("Suggest");
		Button cancel = new Button("Cancel");

		//Setting up GUI screen
		GridPane setupScreen() {
			GridPane root = new GridPane();
			root.setAlignment(Pos.CENTER);
			root.setHgap(8);
			root.setVgap(8);
			//padding between fields and  borders
			root.setPadding(new Insets(4, 4, 4, 4));
			
			//setting widths of text fields
			minutesInput.setPrefWidth(100);
			caloriesInput.setPrefWidth(100);
			root.add(minutes, 0, 0);
			root.add(calories, 0, 1);
			root.add(minutesInput, 1, 0);
			root.add(caloriesInput, 1, 1);
			root.add(warning, 0, 2, 2, 1);
			root.add(suggest, 0, 3);
			root.add(cancel, 1, 3);
			for (Node n : root.getChildren()) {
				GridPane.setHalignment(n, HPos.CENTER);
				GridPane.setValignment(n, VPos.CENTER);
			}
			return root;
		}
	}

	// write your event handlers' inner classes here

}
