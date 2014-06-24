/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.ui;

import com.mashape.unirest.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Generates HTML pages using the velocity templates.
 */
public class UIGenerator {

    private static VelocityEngine velocityEngine = new VelocityEngine();
    private static VelocityContext context;
    private static Logger logger = LogManager.getLogger(UIGenerator.class.getName());

    /**
     * Initialize the velocity engine
     */
    public static void initVelocityEngine() {
        velocityEngine.init();
        context = new VelocityContext();
    }

    /**
     * Puts the context.
     * @param response, the response
     */
    public static void putSeriesContext(HttpResponse response) {
        initVelocityEngine();
        JSONArray jsonMainArr = new JSONArray(response.getBody().toString());

        ArrayList list = new ArrayList();
        for (int i = 0; i < jsonMainArr.length(); i++) {
            JSONObject childJSONObject = jsonMainArr.getJSONObject(i);
            list.add(childJSONObject);
        }

        context.put("seriesList", list);

        printToFile("series.vm", "series.html");
    }

    private static void printToFile(String templateName, String outputFile) {
        Template template = velocityEngine.getTemplate(templateName);
        StringWriter writer = new StringWriter();

        template.merge(context, writer);

        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(outputFile, "UTF-8");
            printWriter.println(writer.toString());
            printWriter.close();
        } catch (FileNotFoundException e) {
            logger.error("File, " + outputFile + ", could not be written", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported encoding", e);
        }
    }
}