package wuzzufJobs;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.types.StructType;

//import weka.clusterers.ClusterEvaluation;
//import weka.clusterers.SimpleKMeans;
//import weka.core.Instances;
//import weka.core.converters.ConverterUtils.DataSource;

public class App {
	// **********  We Used(( Java 8 / JDK11 / Tomcat 8.5 ))  ****************
	// afrer adding weka dependancy for kmeans, war file became to large and we couldn't upload the project,
	// so we left the dependancy as comment in pom.xml file
	private Client client;
	private String REST_SERVICE_URL = "http://localhost:8080/wuzzufJobs/rest/JobService";
	
	private void init(){
		   this.client = ClientBuilder.newClient();
		}
	
	public static void main(String[] args) throws Exception{
		Logger.getLogger("org").setLevel(Level.ERROR);	
		
		// ********************* Test Project Methods *************************
	    JobDao jobdao = new JobDao("src/main/resources/Wuzzuf_Jobs.csv");
		jobdao.company_job_count();
//		jobdao.getPopularTitle();
//		jobdao.getPopularArea();
//		jobdao.countSkill();
//		jobdao.label_encoding();
//		List<Job> jobs = jobdao.getJobs(10);
		
		
		// ********************* Client Tester *************************
//		App tester = new App();
//	    tester.init();
//	    tester.testgetJobs();
//	    tester.testSummary();
//	    tester.testCompany_job_count();
//	    tester.testGetPopularTitle();
//	    tester.testGetPopularArea();
//	    tester.testCountSkill();
//	    tester.testLabel_encoding();
		
		
	}
	
	//Client Tester Methods
	private void testgetJobs(){
		GenericType<List<Job>> jobTst = new GenericType<List<Job>>() {};
		List<Job> jobs = client
		      .target(REST_SERVICE_URL+"/get_jobs")
		      .request(MediaType.APPLICATION_JSON)
		      .get(jobTst);
		   String result = "PASS";
		   if(jobs.isEmpty()){
		      result = "FAIL";
		   }
		   System.out.println("Test case name: testgetJobs, Result: " + result );
	}
		
	private void testSummary(){ 
        StructType schema = client
	       .target(REST_SERVICE_URL+"/summary")
	       .request(MediaType.APPLICATION_JSON)
	       .get(StructType.class);
	    String result = "PASS";
	    if(schema == null){
	       result = "FAIL";
	    }
	    System.out.println("Test case name: testSummary, Result: " + result );
    }  
	 
    private void testCompany_job_count(){ 
		Object comp_job_cnt = client
	       .target(REST_SERVICE_URL+"/company_job_count")
	       .request(MediaType.APPLICATION_JSON)
	       .get(Object.class);
	    String result = "PASS";
	    if(comp_job_cnt == null){
	       result = "FAIL";
	    }
	    System.out.println("Test case name: testCompany_job_count, Result: " + result );
    }  
     
    private void testGetPopularTitle(){ 
        Object pop_title = client
 	       .target(REST_SERVICE_URL+"/get_popular_title")
 	       .request(MediaType.APPLICATION_JSON)
 	       .get(Object.class);
 	    String result = "PASS";
 	    if(pop_title == null){
 	       result = "FAIL";
 	    }
 	    System.out.println("Test case name: testGetPopularTitle, Result: " + result );       
    } 
   
    private void testGetPopularArea(){ 
    	Object pop_area = client
 	       .target(REST_SERVICE_URL+"/get_popular_area")
 	       .request(MediaType.APPLICATION_JSON)
 	       .get(Object.class);
 	    String result = "PASS";
 	    if(pop_area == null){
 	       result = "FAIL";
 	    }
 	    System.out.println("Test case name: testGetPopularArea, Result: " + result );
    }
    
    private void testCountSkill(){ 
    	Object pop_skills = client
 	       .target(REST_SERVICE_URL+"/count_skill")
 	       .request(MediaType.APPLICATION_JSON)
 	       .get(Object.class);
 	    String result = "PASS";
 	    if(pop_skills == null){
 	       result = "FAIL";
 	    }
 	    System.out.println("Test case name: testCountSkill, Result: " + result ); 
    }
     
    private void testLabel_encoding(){ 
    	Object encoded_data = client
 	       .target(REST_SERVICE_URL+"/label_encoding")
 	       .request(MediaType.APPLICATION_JSON)
 	       .get(Object.class);
 	    String result = "PASS";
 	    if(encoded_data == null){
 	       result = "FAIL";
 	    }
 	    System.out.println("Test case name: testLabel_encoding, Result: " + result ); 
    }
	
}
