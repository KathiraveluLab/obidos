/*
 * Copyright (c) 2015-2016, Pradeeban Kathiravelu and others. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.bmi.datarepl.rest;

import edu.emory.bmi.datarepl.constants.CommonConstants;
import edu.emory.bmi.datarepl.interfacing.TciaInvoker;
import edu.emory.bmi.datarepl.tcia.TciaLogInInitiator;
import edu.emory.bmi.datarepl.tcia.TciaReplicaSetAPI;
import edu.emory.bmi.datarepl.ui.DataRetriever;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.*;

public class TciaReplicaSetInvoker {
    private static Logger logger = LogManager.getLogger(DataRetriever.class.getName());

    public static void main(String[] args) {

        port(CommonConstants.REST_PORT);

        TciaLogInInitiator logInInitiator = new TciaLogInInitiator();
        logInInitiator.init();

        TciaReplicaSetAPI tciaReplicaSetAPI = TciaLogInInitiator.getTciaReplicaSetAPI();

        /**
         * Create Replica Set:
         /POST
         http://localhost:9090/replicasets?iUserID=12&iCollection=TCGA-GBM&iPatientID=TCGA-06-6701%2CTCGA-08-0831&iStudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.151679082681232740021018262895&iSeriesInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.179004339156422100336233996679

         Response:
         (replicaSetID).
         -4764762120292626164

         or

         <html>
         <body>
         <h2>500 Internal Error</h2>
         </body>
         </html>
         */
        post("/replicasets", (request, response) -> {
            String userId = request.queryParams("iUserID");
            String[] collection = (request.queryParams("iCollection") != null) ? request.queryParams("iCollection").split(",") : new String[0];
            String[] patientId = (request.queryParams("iPatientID") != null) ? request.queryParams("iPatientID").split(",") : new String[0];
            String[] studyInstanceUID = (request.queryParams("iStudyInstanceUID") != null) ? request.queryParams("iStudyInstanceUID").split(",") : new String[0];
            String[] seriesInstanceUID = (request.queryParams("iSeriesInstanceUID") != null) ? request.queryParams("iSeriesInstanceUID").split(",") : new String[0];
            long id = tciaReplicaSetAPI.createNewReplicaSet(userId, collection, patientId, studyInstanceUID, seriesInstanceUID);
            response.status(201); // 201 Created
            return id;
        });


        /**
         Retrieve Replica Sets of the user:
         /GET
         http://localhost:9090/replicasets/12

         Response:
         [-7466653342708752832, -7059417815353339196, -6908825180316283930, -6365519002970140943]

         or

         Replicasets not found for the user: 123
         */
        get("/replicasets/:id", (request, response) -> {
            Long[] replicaSets = tciaReplicaSetAPI.getUserReplicaSets(request.params(":id"));

            String out = Arrays.toString(replicaSets);

            if (replicaSets != null) {
                return out;
            } else {
                response.status(404); // 404 Not found
                return "Replicasets not found for the user: " + request.params(":id");
            }
        });


        /**
         *
         Retrieve Replica Set:
         /GET
         http://localhost:9090/replicaset/-5760861907871124991

         Response:
         Collection Names: [TCGA-GBM]. Patient IDs: [TCGA-06-6701, TCGA-08-0831]. StudyInstanceUIDs: [1.3.6.1.4.1.14519.5.2.1.4591.4001.151679082681232740021018262895]. SeriesInstanceUIDs: [1.3.6.1.4.1.14519.5.2.1.4591.4001.179004339156422100336233996679]

         or

         <html>
         <body>
         <h2>500 Internal Error</h2>
         </body>
         </html>


         */
        get("/replicaset/:id", (request, response) -> {
            long replicaSetID = Long.parseLong(request.params(":id"));
            String replicaSet = tciaReplicaSetAPI.getReplicaSet(replicaSetID);
            if (replicaSet != null) {
                return replicaSet;
            } else {
                response.status(404); // 404 Not found
                return "Replicaset not found: " + request.params(":id");
            }
        });


        /**
         *
         Delete Replica Set:
         /DELETE
         http://localhost:9090/replicaset/12?replicaSetID=-5722101370224504108

         Response:
         true

         or

         false

         or

         <html>
         <body>
         <h2>500 Internal Error</h2>
         </body>
         </html>
         */
        delete("/replicaset/:id", (request, response) -> {
            String userId = request.params(":id");
            long replicaSetID = Long.parseLong(request.queryParams("replicaSetID"));

            return tciaReplicaSetAPI.deleteReplicaSet(userId, replicaSetID);
        });


        /**
         *
         Replace Replica Set:
         /POST
         http://localhost:9090/replicaset/-5841894688098285105?iStudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.151679082681232740021018262895&iSeriesInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.179004339156422100336233996679
         */
        post("/replicaset/:id", (request, response) -> {
            long replicaSetId = Long.parseLong(request.params(":id"));

            String[] collection = (request.queryParams("iCollection") != null) ? request.queryParams("iCollection").split(",") : new String[0];
            String[] patientId = (request.queryParams("iPatientID") != null) ? request.queryParams("iPatientID").split(",") : new String[0];
            String[] studyInstanceUID = (request.queryParams("iStudyInstanceUID") != null) ? request.queryParams("iStudyInstanceUID").split(",") : new String[0];
            String[] seriesInstanceUID = (request.queryParams("iSeriesInstanceUID") != null) ? request.queryParams("iSeriesInstanceUID").split(",") : new String[0];

            Boolean out = tciaReplicaSetAPI.replaceReplicaSet(replicaSetId, collection, patientId, studyInstanceUID, seriesInstanceUID);
            response.status(201); // 201 Created
            return out;
        });

        /**
         *
         Append Replica Set:
         /PUT
         http://localhost:9090/replicaset/-5841894688098285105?iCollection=TCGA-GBM
         */
        put("/replicaset/:id", (request, response) -> {
            long replicaSetId = Long.parseLong(request.params(":id"));

            String[] collection = (request.queryParams("iCollection") != null) ? request.queryParams("iCollection").split(",") : new String[0];
            String[] patientId = (request.queryParams("iPatientID") != null) ? request.queryParams("iPatientID").split(",") : new String[0];
            String[] studyInstanceUID = (request.queryParams("iStudyInstanceUID") != null) ? request.queryParams("iStudyInstanceUID").split(",") : new String[0];
            String[] seriesInstanceUID = (request.queryParams("iSeriesInstanceUID") != null) ? request.queryParams("iSeriesInstanceUID").split(",") : new String[0];

            Boolean out = tciaReplicaSetAPI.addToReplicaSet(replicaSetId, collection, patientId, studyInstanceUID, seriesInstanceUID);
            response.status(201); // 201 Created
            return out;
        });

        /**
         Duplicate Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/duplicateRs?dUserID=123&replicaSetID=-8818562079351590113"
         */
    }
}
