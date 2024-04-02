/*
 * Copyright 2015 Jordi Casas-Roma, Alexandre Dotor Casals
 *
 * This file is part of DGA.
 *
 * DGA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DGA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DGA.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.uoc.kison;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.uoc.kison.DGA.DGA;
import org.uoc.kison.exporters.GmlExporter;
import org.uoc.kison.objects.SimpleIntGraph;
import org.uoc.kison.parsers.GmlParser;
import org.uoc.kison.parsers.TxtParser;

import com.opencsv.CSVReader;
import org.uoc.kison.exporters.TxtExporter;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);
    private static final String version = "1.0.1";

    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        String inputFileName = null;
        String extension = null;
        String outputFileName = null;
        String inOutDegreeK = null;

        if (args.length >= 2) {
            inputFileName = args[0];
            inOutDegreeK = args[1];

            String fullPath = FilenameUtils.getFullPath(inputFileName);
            String baseName = FilenameUtils.getBaseName(inOutDegreeK);
            extension = FilenameUtils.getExtension(inputFileName);
            outputFileName = fullPath + baseName + "." + extension;

            logger.info("***************************************************************");
            logger.info("* DGA - Directed Graph Anonymization                          *");
            logger.info("* Jordi Casas-Roma (jcasasr@uoc.edu)                          *");
            logger.info("* Alexandre Dotor Casals (adotorc@uoc.edu)                    *");
            logger.info("* Universitat Oberta de Catalunya (www.uoc.edu)               *");
            logger.info("*                                                             *");
            logger.info("***************************************************************");
            logger.info("");
            logger.info(String.format("Version %s", version));
            logger.info(String.format("Input filname          : %s", inputFileName));
            logger.info(String.format("in/out degree filename : %s", inOutDegreeK));
            logger.info(String.format("Output filename : %s", outputFileName));
            logger.info("");
            logger.info("---------------------------------------------------------------");

        } else {
            System.out.println("DGA Version " + version);
            System.out.println("Usage: java DGA <input filename> <in/out degree vectors filename>");
            System.exit(-1);
        }

        // import GML
        SimpleIntGraph graph = null;

        if (extension.compareToIgnoreCase("GML") == 0) {
            GmlParser gmlParser = new GmlParser();
            graph = gmlParser.parseFileToSimpleIntGraph(inputFileName);

        } else if (extension.compareToIgnoreCase("TXT") == 0) {
            TxtParser txtParser = new TxtParser();
            graph = txtParser.parseFileToSimpleIntGraph(inputFileName);

        } else {
            logger.error(String.format("Unknown filetype (extension %s)!", extension));
            System.exit(0);
        }
        // Parse in / out degree K-anonymous vector
        try {
            CSVReader reader = new CSVReader(new FileReader(inOutDegreeK));
            String[] dinkString = reader.readNext();
            String[] doutkString = reader.readNext();
            reader.close();

            int[] dink = new int[dinkString.length];
            int[] doutk = new int[doutkString.length];
            for (int i = 0; i < dinkString.length; i++) {
                dink[i] = Integer.parseInt(dinkString[i]);
            }
            for (int i = 0; i < doutkString.length; i++) {
                doutk[i] = Integer.parseInt(doutkString[i]);
            }

            // apply UMGA algorithm
            DGA dga = new DGA();
            SimpleIntGraph gk = dga.dga(graph, dink, doutk);

            // export result 
            logger.info(String.format("Saving anonymized graph to: %s", outputFileName));
            
            if (extension.compareToIgnoreCase("TXT") == 0) {
                TxtExporter txtExporter = new TxtExporter();
                txtExporter.exportToFile(gk, outputFileName, true);
                
            } else {
                // defualt is GML
                GmlExporter gmlExporter = new GmlExporter();
                gmlExporter.exportToFile(gk, outputFileName, true);
            }

        }catch (IOException e){
        	logger.error("Error trying to read file "+inOutDegreeK);
        	e.printStackTrace();
        }
    }
}
