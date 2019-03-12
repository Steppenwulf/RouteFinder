package com.routeFinder.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileParser {
	public static final String DELIMITER = ",";
	
	public static final String DEFAULT_MAP_FILE = "city.txt";

	@Autowired
	private MapService mapService;

	public void parseFile(String fileName) throws FileNotFoundException {
		try(Scanner mapScanner = new Scanner(getResourceFile(fileName))) {
			while(mapScanner.hasNextLine()) {
				parseLine(mapScanner.nextLine());
			}
		}
	}
	
	public void parseLine(String line) {
		if(line != null) {
			String[] locations = line.split(DELIMITER);
			if (locations.length == 2) {//requirements say every line is a road between 2 cities, so assume any other line without 2 locations is an error
				getMapService().addAdjacents(locations[0], locations[1]);
			}
		}
	}

	public MapService getMapService() {
		return mapService;
	}
	
	protected File getResourceFile(String fileName) {
		return new File(getClass().getClassLoader().getResource(fileName).getFile());
	}
}
