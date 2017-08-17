# Project Overview

Welcome to the wiki of the MEDIator - Data Replication System project!

Please visit the website to learn more - https://bitbucket.org/BMI/datareplicationsystem


## Setting the Environment Variables.
Set the API_KEY, MASHAPE_AUTHORIZATION, and BASE_UR as environment variables.

For example, in Linux.

    export API_KEY=your-api-key
    
    export MASHAPE_AUTHORIZATION=your-mashape-authorization
    
    export BASE_URL=services.cancerimagingarchive.net/services/v4/TCIA/query


## Building and Executing Using Apache Maven 3.x.x
Building
--------
     mvn package

It is expected to have the TCIA API_KEY set in the environment variables to build with tests.

If you do not have a TCIA API_KEY yet, please build with skipping the tests.

    mvn package -DskipTests


Executing
---------
[1] MEDIator REST APIs.

If you are hosting MEDIator for public access, you need to start it and expose its RESTful APIs. Execute the
MEDIatorEngine class.

$ java -classpath lib/repl-server-1.0-SNAPSHOT.jar:lib/*:conf/ edu.emory.bmi.datarepl.core.MEDIatorEngine

You may extend or leverage the exposed APIs. To begin with, you may consume the MEDIator RESTful APIs through a REST
client such as the Postman plugin of the Chrome browser.

To add more instances to the cluster, start the instances of Initiator class.
$ java -classpath lib/repl-server-1.0-SNAPSHOT.jar:lib/*:conf/ edu.emory.bmi.datarepl.core.Initiator

The implementation of the RESTful invocations can be found at TciaReplicaSetManager.


[2] MEDIator Web Application
   
     sh modules/repl-server/target/bin/webapp

Go to http://localhost:<EMBEDDED_TOMCAT_PORT>/mediator/ using your browser to access the Web Application.



You may run the Initiator class in parallel, to create a MEDIator cluster in both cases.


Logging
-------
Make sure to include log4j2-test.xml into your class path to be able to configure and view the logs. Default log level is [WARN].



## Dependencies
This project depends on the below major projects.

* Infinispan
* Apache Tomcat (Embedded)
* Apache HTTP Client
* Apache Velocity
* Apache Log4j2
* Mashape Unirest
* SparkJava



## Interfaces

[1] Data Source Management
--------------------------
Relevant classes can be found in the package: ds_mgmt.

Data source management module manages the data sources themselves. 

The relevant interfaces and implementations are often provided by the data sources or the data providers, and hence 
orthogonal to MEDIator. However, a TCIA data source management RESTful interface and implementation are included.


[2] Replica Set Management
--------------------------
Relevant classes can be found in the package: rs_mgmt.

Replicaset management module manages the replica sets pointing to each of the data sources.

ReplicaSetHandlers are implemented for each of the data sources that are federated by MEDIator. The ReplicaSetHandlers
offer an internal implementation of the replica sets management.

ReplicaSetManagers are the core REST APIs of MEDIator. They leverage the ReplicaSetHandlers to manage the replica sets 
of each of the data sources. There is a one-to-one mapping between the ReplicaSetManager api implementations and the 
data sources.

TciaReplicaSetManager provides the REST API for managing the TCIA replica sets. Relevant documentation can be found in 
the class as the method-level comments.


[3] Integrator
---------------
Relevant classes can be found in the package: integrator.

ReplicaSetsIntegrator is a singleton that manages integration of all the data sources for the replica set management.

Hence, ReplicaSetsIntegrator is a single entity that has a one-to-many relationship with the data sources of MEDIator
for the replicaset management.



## Connect to An EC2 Instance
If you prefer to use an EC2 Instance to upload clinical files to S3, you may follow the below steps.

Otherwise, you may directly upload the clinical files which were downloaded to your local computer.

* Connect to the instance

$ ssh -i pradeeban.pem ubuntu@ec2-54-237-35-248.compute-1.amazonaws.com

* Make a directory and change to the directory.

$ mkdir gsoc2014

$ cd gsoc2014

* Download and extract the meta data from the online repository (link sent to email. Given below is an example).

$ nohup wget https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/userCreatedArchives/652ccf44-cfda-4e99-81ac-d8f4c0eca6be.tar > nohup.out &

$ tar -zxvf 652ccf44-cfda-4e99-81ac-d8f4c0eca6be.tar



## Further customizations
If you wish to reproduce the system in another deployment, please refer to documentation/HOW-TO.



## Citing MEDIator
The bibtex to cite MEDIator is the following:

@misc{pradeeban_kathiravelu_2017_844842,
  author       = {Pradeeban Kathiravelu and
                  Ashish Sharma},
  title        = {{MEDIator: A Data Sharing Synchronization Platform
                   for Heterogeneous Medical Image Archives}},
  month        = aug,
  year         = 2017,
  doi          = {10.5281/zenodo.844842},
  url          = {https://doi.org/10.5281/zenodo.844842}
}

If you have used MEDIator in your research, please cite the below papers:

[1] Kathiravelu, P. & Sharma, A. (2015). MEDIator: A Data Sharing Synchronization Platform for Heterogeneous Medical Image Archives.
In Workshop on Connected Health at Big Data Era (BigCHat'15), co-located with 21st ACM SIGKDD Conference on Knowledge Discovery and Data Mining (KDD 2015).
Aug. 2015. ACM. 6 pages. http://doi.org/10.13140/RG.2.1.3709.4248


[2] Kathiravelu, P. & Sharma, A. (2016). SPREAD - System for Sharing and Publishing Research Data. In Society for Imaging
Informatics in Medicine Annual Meeting (SIIM 2016). June 2016.
http://c.ymcdn.com/sites/siim.org/resource/resmgr/siim2016abstracts/Research_Kathiravelu.pdf

