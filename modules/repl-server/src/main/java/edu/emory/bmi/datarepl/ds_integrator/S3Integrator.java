/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.ds_integrator;

import edu.emory.bmi.datarepl.ds_impl.CSVInfDai;
import edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Integrates with S3
 */
public class S3Integrator extends DataSourcesIntegrator {
    private static Logger logger = LogManager.getLogger(S3Integrator.class.getName());


    /**
     * Get patient studies from S3
     *
     * @param patientID, id of the patient
     */
    public static String getPatientStudies(String patientID) {
        String url = retrieveUrl(patientID);
        updateExistenceInDataSource(patientID, DataSourcesConstants.S3_META_POSITION, true);
        return url;
    }

    /**
     * Update meta data with CSV entry
     *
     * @param longKey,   contains patient ID
     * @param metaArray, meta array to be stored
     */
    public static void updateMetaData(String longKey, String[] metaArray) {
        String patientID = longKey.substring(0, 12);
        CSVInfDai.getS3MetaMap().put(patientID, metaArray[0]);
        updateExistenceInDataSource(patientID, DataSourcesConstants.S3_META_POSITION, true);
    }

    /**
     * Gets the S3 storage url for the given patient
     * @param patientID, the patient ID
     * @return url.
     */
    public static String retrieveUrl(String patientID) {
        String fileName = CSVInfDai.getS3MetaMap().get(patientID);
        String url;
        if (fileName.contains(DataSourcesConstants.S3_LEVEL2)) {
            url = DataSourcesConstants.S3_BASE_URL + DataSourcesConstants.S3_LEVEL2 +
                    DataSourcesConstants.URL_SEPARATOR + fileName;
        } else if (fileName.contains(DataSourcesConstants.S3_LEVEL3)) {
            url = DataSourcesConstants.S3_BASE_URL + DataSourcesConstants.S3_LEVEL3 +
                    DataSourcesConstants.URL_SEPARATOR + fileName;
        } else {
            url = DataSourcesConstants.S3_BASE_URL + DataSourcesConstants.S3_LEVEL1 +
                    DataSourcesConstants.URL_SEPARATOR + fileName;
        }
        return url;
    }
}
