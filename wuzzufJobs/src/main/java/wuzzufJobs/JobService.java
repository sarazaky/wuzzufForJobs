package wuzzufJobs;

import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Encoder;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;


@Path("/JobService") 
public class JobService {
	
	JobDao jobdao = new JobDao("C:/Users/saraz/eclipse-workspace0/wuzzufJobs/src/main/resources"); 
	
	@GET 
    @Path("/get_jobs") 
    @Produces(MediaType.APPLICATION_JSON) 
    public List<Job> getJobs(){ 
		List<Job> job_lst = jobdao.getJobs(10);
        return job_lst; 
    }  
	
	
	@GET 
    @Path("/summary") 
    @Produces(MediaType.APPLICATION_JSON) 
    public StructType summary(){ 
	    StructType schema = jobdao.summary();
        return schema; 
    }  
	
    @GET 
    @Path("/company_job_count") 
    @Produces(MediaType.APPLICATION_JSON) 
    public Object company_job_count(){ 
	    Object obj = jobdao.company_job_count();
        return obj; 
    }  
   
    @GET 
    @Path("/get_popular_title") 
    @Produces(MediaType.APPLICATION_JSON) 
    public Object getPopularTitle(){ 
	    Object obj = jobdao.getPopularTitle();
        return obj; 
    } 
   
    @GET 
    @Path("/get_popular_area") 
    @Produces(MediaType.APPLICATION_JSON) 
    public Object getPopularArea(){ 
	    Object obj = jobdao.getPopularArea();
        return obj; 
    }
    
    @GET 
    @Path("/count_skill") 
    @Produces(MediaType.APPLICATION_JSON) 
    public Object countSkill(){ 
	    Object obj = jobdao.countSkill();
        return obj; 
    }
    
    @GET 
    @Path("/label_encoding") 
    @Produces(MediaType.APPLICATION_JSON) 
    public Object label_encoding(){ 
    	Object obj = jobdao.label_encoding();
        return obj; 
    }
    
    
}