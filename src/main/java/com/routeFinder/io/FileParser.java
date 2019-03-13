package com.routeFinder.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.routeFinder.model.Graph;

@Component
public class FileParser {
	public static final String DELIMITER = ",";
	
	public static final String DEFAULT_GRAPH_FILE = "city.txt";

	@Autowired
	private Graph mapService;

	public void parseFile(String fileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		try(InputStream iStream = classPathResource.getInputStream(); 
			InputStreamReader iReader = new InputStreamReader(iStream);
			BufferedReader bReader= new BufferedReader(iReader)) {
			String line;
			while((line = bReader.readLine()) != null) {
				parseLine(line);
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

	public Graph getMapService() {
		return mapService;
	}
}
