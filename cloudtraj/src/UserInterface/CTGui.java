package UserInterface;

import processing.core.PApplet;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import bdss.model.TDatabase;
import bdss.model.TaxiCheckPoint;
import bdss.model.TaxiTrip;
import bdss.model.Trajectory;
import bdss.model.Marker;
import bdss.model.coordinates;
import bdss.preprocessing.Parse;
import bdss.trajdb.Database;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import g4p_controls.*;

public class CTGui extends PApplet {
	UnfoldingMap map;
	String fileName;
	String path;
	String fileSelected;
	Parse parser = new Parse();
	Database db = new Database();
	TDatabase tdb = new TDatabase();
	private List<SimplePointMarker> placingMarker = new ArrayList<SimplePointMarker>();
	private List<SimpleLinesMarker> lineMarker = new ArrayList<SimpleLinesMarker>();
	private List<Integer> linecolor = new ArrayList<Integer>();
	
	private List<TaxiTrip> taxiTrip = new ArrayList<TaxiTrip>();
	private List<TaxiCheckPoint> cpTrip = new ArrayList<TaxiCheckPoint>();
	
	// Serial ID is optional and added by Eclipse
	@Override
	public void setup() {
		size(900, 600, P3D);
		if (frame != null) {
			frame.setResizable(true);
		}
		createGUI();
		customGUI();
		// Place your setup code here

	}

	@Override
	public void draw() {
		background(230);
		map.draw();
		db.connect();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { CTGui.class.getName() });
	}

	// Use this method to add additional statements
	// to customise the GUI controls
	public void customGUI() {
		map = new UnfoldingMap(this, 20, 86, 570, 400, new OpenStreetMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
	}
	
	public long stringToUnix(String date) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a"); 
		long temp = 0;
		format.setTimeZone(TimeZone.getTimeZone("GMT+1")); //Specify your timezone
		try {
			temp = format.parse(date).getTime();
			temp = temp/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temp;
	}
	
	public void fileSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			println("User selected " + selection.getAbsolutePath());
			path = selection.getAbsolutePath();
			if (fileSelected.equals("trip")) {
				fileLocation.setText(path);
				try {
					parser.read(path);
//					tdb.importDataset(path);
//					setColor();
					
					db.taxiIndexing(parser.getTempTrajectory());
					db.tripIndexing(parser.getTempTrajectory());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				map = new UnfoldingMap(this, 20, 86, 570, 400, new OpenStreetMapProvider());
				map.zoomAndPanTo(12, new Location(41.1491,-8.6107));
//				viewPlaceOnly();
//				viewTraj();
				MapUtils.createDefaultEventDispatcher(this, map);
			}
		}
	}
	
	public void viewPlaceOnly() {
		placingMarker = new ArrayList<SimplePointMarker>();
		for (coordinates c: tdb.coordinate) {
			Location placeX = new Location(c.lng,c.lat);
			SimplePointMarker placeMarker = new SimplePointMarker(placeX);
			placeMarker.setStrokeWeight(1);
			placingMarker.add(placeMarker);
			map.addMarkers(placeMarker);
		}
	}
	
	public void setColor() {
		for (Trajectory t : tdb.trajectories) {
			Random rand = new Random();
			// Java 'Color' class takes 3 floats, from 0 to 1.
			float r = rand.nextInt(256);
			float g = rand.nextInt(256);
			float b = rand.nextInt(256);
			linecolor.add(color(r, g, b));
		}
	}
	
	public void setColor(int size) {
		for (int i = 0; i <size; i++) {
			Random rand = new Random();
			// Java 'Color' class takes 3 floats, from 0 to 1.
			float r = rand.nextInt(256);
			float g = rand.nextInt(256);
			float b = rand.nextInt(256);
			linecolor.add(color(r, g, b));
		}
	}
	
	public void viewTraj() {
		lineMarker = new ArrayList<SimpleLinesMarker>();
		int traj=0;
		for (Trajectory t: tdb.trajectories) {
			int i = 0;
			for (Marker p: t.baseline) {
				Location placeX = new Location(p.lng,p.lat);
				if (i != 0) {
					Location placePrev = new Location(t.baseline.get(i-1).lng,t.baseline.get(i-1).lat);
					SimpleLinesMarker connectionMarker = new SimpleLinesMarker(placePrev, placeX);
					connectionMarker.setColor(linecolor.get(traj));
					connectionMarker.setStrokeWeight(1);
					lineMarker.add(connectionMarker);
					map.addMarkers(connectionMarker);
				}
				i++;
			}
			traj++;
		}
	}

	/* =========================================================
	 * ====                   WARNING                        ===
	 * =========================================================
	 * The code in this tab has been generated from the GUI form
	 * designer and care should be taken when editing this file.
	 * Only add/edit code inside the event handlers i.e. only
	 * use lines between the matching comment tags. e.g.

	 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
	     // It is safe to enter your event code here  
	 } //_CODE_:button1:12356:
	 
	 * Do not rename this tab!
	 * =========================================================
	 */

	public void textfield1_change1(GTextField source, GEvent event) { //_CODE_:fileLocation:941412:
	  println("textfield1 - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:fileLocation:941412:

	public void button1_click1(GButton source, GEvent event) { //_CODE_:loadButton:288552:
	  println("loadButton - GButton >> GEvent." + event + " @ " + millis());
	  selectInput("Select a file to process:", "fileSelected");
	  fileSelected = "trip";
	} //_CODE_:loadButton:288552:

	public void button2_click1(GButton source, GEvent event) { //_CODE_:clearButton:472670:
	  println("clearButton - GButton >> GEvent." + event + " @ " + millis());
	} //_CODE_:clearButton:472670:

	public void textfield2_change1(GTextField source, GEvent event) { //_CODE_:pathStart:294212:
	  println("textfield2 - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:pathStart:294212:

	public void textfield3_change1(GTextField source, GEvent event) { //_CODE_:pathEnd:354994:
	  println("textfield3 - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:pathEnd:354994:

	public void button1_click2(GButton source, GEvent event) { //_CODE_:pathSearch:662089:
	  println("button1 - GButton >> GEvent." + event + " @ " + millis());
	  map = new UnfoldingMap(this, 20, 86, 570, 400, new OpenStreetMapProvider());
	  map.zoomAndPanTo(12, new Location(41.1491,-8.6107));
	  
	  cpTrip = db.taxiQuery(taxiId.getText(), Long.toString(stringToUnix(pathStart.getText())), Long.toString(stringToUnix(pathEnd.getText())));
	 
	  
	  int traj=0;
	  setColor(cpTrip.size());
	  int i = 0;
	  String ss= "Path ";
	  for (TaxiCheckPoint trip : cpTrip) {
		  ss=ss+"["+trip.lng+"-"+trip.lat+"]-";
		  Location placeX = new Location(trip.getLng(),trip.getLat());
		  if (i != 0) {
				Location placePrev = new Location(cpTrip.get(i-1).getLng(), cpTrip.get(i-1).getLat());
				SimpleLinesMarker connectionMarker = new SimpleLinesMarker(placePrev, placeX);
				connectionMarker.setColor(linecolor.get(traj));
				connectionMarker.setStrokeWeight(3);
				lineMarker.add(connectionMarker);
				map.addMarkers(connectionMarker);
		  }
		  i++;
		}
	  traj++;
	  trajectoryList.setText(ss);

	  MapUtils.createDefaultEventDispatcher(this, map);
	} //_CODE_:pathSearch:662089:

	public void textfield4_change1(GTextField source, GEvent event) { //_CODE_:stStart:277840:
	  println("textfield4 - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:stStart:277840:

	public void textfield5_change1(GTextField source, GEvent event) { //_CODE_:stEnd:723072:
	  println("textfield5 - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:stEnd:723072:

	public void button4_click1(GButton source, GEvent event) { //_CODE_:stSearch:818899:
	  println("button4 - GButton >> GEvent." + event + " @ " + millis());
	  map = new UnfoldingMap(this, 20, 86, 570, 400, new OpenStreetMapProvider());
	  map.zoomAndPanTo(12, new Location(41.1491,-8.6107));
	  	  
	  taxiTrip = db.tripQuery(Long.toString(stringToUnix(stStart.getText())), Long.toString(stringToUnix(stEnd.getText())));
	  int traj=0;
	  setColor(taxiTrip.size());
	  String ss= "Trip ID: ";
	  for (TaxiTrip trip : taxiTrip) {
		 ss=ss+trip.getTaxiId()+"\n";
		  int i = 0;
		  for (Marker p : trip.getPolylines()) {
			Location placeX = new Location(p.lng,p.lat);
			if (i != 0) {
				Location placePrev = new Location(trip.getPolylines().get(i-1).lng, trip.getPolylines().get(i-1).lat);
				SimpleLinesMarker connectionMarker = new SimpleLinesMarker(placePrev, placeX);
				connectionMarker.setColor(linecolor.get(traj));
				connectionMarker.setStrokeWeight(3);
				lineMarker.add(connectionMarker);
				map.addMarkers(connectionMarker);
			}
			i++;
		}
		  traj++;
	  }
	  trajectoryList.setText(ss);
//
//	  MapUtils.createDefaultEventDispatcher(this, map);
	} //_CODE_:stSearch:818899:

	public void textarea1_change1(GTextArea source, GEvent event) { //_CODE_:trajectoryList:490883:
	  println("trajectoryList - GTextArea >> GEvent." + event + " @ " + millis());
	} //_CODE_:trajectoryList:490883:

	public void textfield1_change2(GTextField source, GEvent event) { //_CODE_:taxiId:948204:
	  println("taxiId - GTextField >> GEvent." + event + " @ " + millis());
	} //_CODE_:taxiId:948204:



	// Create all the GUI controls. 
	// autogenerated do not edit
	public void createGUI(){
	  G4P.messagesEnabled(false);
	  G4P.setGlobalColorScheme(GConstants.BLUE_SCHEME);
	  G4P.setCursor(ARROW);
	  if(frame != null)
	    frame.setTitle("Sketch Window");
	  fileLabel = new GLabel(this, 29, 30, 80, 20);
	  fileLabel.setText("GPS Record");
	  fileLabel.setOpaque(false);
	  fileLocation = new GTextField(this, 128, 27, 224, 30, GConstants.SCROLLBARS_NONE);
	  fileLocation.setOpaque(true);
	  fileLocation.addEventHandler(this, "textfield1_change1");
	  loadButton = new GButton(this, 373, 26, 80, 30);
	  loadButton.setText("Load");
	  loadButton.addEventHandler(this, "button1_click1");
	  clearButton = new GButton(this, 465, 26, 80, 30);
	  clearButton.setText("Clear");
	  clearButton.addEventHandler(this, "button2_click1");
	  label1 = new GLabel(this, 605, 86, 144, 24);
	  label1.setText("Id-Temporal Query");
	  label1.setTextBold();
	  label1.setOpaque(false);
	  label2 = new GLabel(this, 606, 126, 80, 20);
	  label2.setText("Taxi Id");
	  label2.setOpaque(false);
	  label3 = new GLabel(this, 607, 166, 80, 20);
	  label3.setText("Start Date");
	  label3.setOpaque(false);
	  label4 = new GLabel(this, 605, 212, 80, 20);
	  label4.setText("End Date");
	  label4.setOpaque(false);
	  pathStart = new GTextField(this, 713, 162, 160, 30, GConstants.SCROLLBARS_NONE);
	  pathStart.setOpaque(true);
	  pathStart.addEventHandler(this, "textfield2_change1");
	  pathEnd = new GTextField(this, 714, 206, 160, 30, GConstants.SCROLLBARS_NONE);
	  pathEnd.setOpaque(true);
	  pathEnd.addEventHandler(this, "textfield3_change1");
	  pathSearch = new GButton(this, 795, 258, 80, 30);
	  pathSearch.setText("Search");
	  pathSearch.addEventHandler(this, "button1_click2");
	  label5 = new GLabel(this, 602, 364, 144, 24);
	  label5.setText("Path-Temporal Query");
	  label5.setTextBold();
	  label5.setOpaque(false);
	  label6 = new GLabel(this, 604, 415, 80, 20);
	  label6.setText("Start Date");
	  label6.setOpaque(false);
	  stStart = new GTextField(this, 710, 411, 160, 30, GConstants.SCROLLBARS_NONE);
	  stStart.setOpaque(true);
	  stStart.addEventHandler(this, "textfield4_change1");
	  label7 = new GLabel(this, 603, 462, 80, 20);
	  label7.setText("End Date");
	  label7.setOpaque(false);
	  stEnd = new GTextField(this, 710, 458, 160, 30, GConstants.SCROLLBARS_NONE);
	  stEnd.setOpaque(true);
	  stEnd.addEventHandler(this, "textfield5_change1");
	  stSearch = new GButton(this, 792, 504, 80, 30);
	  stSearch.setText("Search");
	  stSearch.addEventHandler(this, "button4_click1");
	  trajectoryList = new GTextArea(this, 8, 507, 589, 80, GConstants.SCROLLBARS_NONE);
	  trajectoryList.setOpaque(true);
	  trajectoryList.addEventHandler(this, "textarea1_change1");
	  taxiId = new GTextField(this, 712, 121, 160, 30, GConstants.SCROLLBARS_NONE);
	  taxiId.setOpaque(true);
	  taxiId.addEventHandler(this, "textfield1_change2");
	}

	// Variable declarations 
	// autogenerated do not edit
	GLabel fileLabel; 
	GTextField fileLocation; 
	GButton loadButton; 
	GButton clearButton; 
	GLabel label1; 
	GLabel label2; 
	GLabel label3; 
	GLabel label4; 
	GTextField pathStart; 
	GTextField pathEnd; 
	GButton pathSearch; 
	GLabel label5; 
	GLabel label6; 
	GTextField stStart; 
	GLabel label7; 
	GTextField stEnd; 
	GButton stSearch; 
	GTextArea trajectoryList; 
	GTextField taxiId; 
}
