/* Name : Prabhat turlapati
*  ID : sturlapa
*/
package hw3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PTData {

	ObservableList<Exercise> masterData = FXCollections.observableArrayList(); //data read from csv or xml file 
	ObservableList<Exercise> selectedExercises = FXCollections.observableArrayList() ; //exercises displayed in exerciseTableView 

	/** loadData method checks if the file has extension .xml. If so, it creates an object of 
	 * XMLFiler class. Else it assumes it to be of CSV type and creates an object of CSVFiler class. 
	 * It then invokes readData() method and passes the File object of filename provided as the argument for this method.
	 * 
	 * The list returned by readData method is used to initialize masterObservables and is also returned by this method.
	 * Notice that the data files must reside in the DEFAULT_PATH as defined in PersonalTrainer class.
	 * @param filename
	 * @return
	 */
	ObservableList<Exercise> loadData(String filename) {
		DataFiler dataFiler;
		// using polymorphism on datafiler to decide XML and CSV
		if(filename.contains(".xml")) {
			dataFiler =  new XMLFiler();
			masterData.addAll(dataFiler.readData(filename));
			
		}
		else {
			dataFiler =  new CSVFiler();
			masterData.addAll(dataFiler.readData(filename));
		}
		return masterData;
	}
	
	
}