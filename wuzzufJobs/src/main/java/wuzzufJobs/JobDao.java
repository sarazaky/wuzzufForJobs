package wuzzufJobs;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Encoders;

import org.knowm.xchart.style.Styler;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import static org.apache.spark.sql.functions.*;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.IOException;
import java.util.Arrays;

import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Encoder;

public class JobDao {
	
	final SparkSession sparkSession = SparkSession.builder ().appName ("wuzzufJobs").master ("local[4]").getOrCreate ();
	private Dataset<Row> jobs_df;
	
	
	public JobDao(String path) {
		this.read_file(path);
		this.summary();
		this.clean_data();
	}
	
	
	public Dataset<Row> read_file(String path) {
		// Create Spark Session , DataFrameReader
		DataFrameReader dataFrameReader = sparkSession.read ().option ("header", "true");
		
		//Load Dataset
		this.jobs_df = dataFrameReader.csv(path);
		
		System.out.println("Some Of The Data");
        this.jobs_df.show(10);
        
        // Return Data as Dataset
        return this.jobs_df;       
    }
	
	public StructType summary() {
		System.out.println("\nSummary And Structure Of The Data");
		System.out.println("Number of rows = " + this.jobs_df.count());
		this.jobs_df.printSchema();
		return this.jobs_df.schema();
    }
		
	
	public Dataset<Row> clean_data(){
		System.out.println("Number of rows Before Cleaning Data: " + jobs_df.count());
    	//Removing The Duplicates
		this.jobs_df = this.jobs_df.distinct();
		// Removing Null Values
		this.jobs_df = this.jobs_df.filter((FilterFunction<Row>) row  -> !row.anyNull());
		System.out.println("Number of rows After Cleaning Data: " + jobs_df.count());
		
		// Clean Data Values
		JavaRDD<Row> Jobs_lst  = this.jobs_df.javaRDD().map(row -> {
		// Clean Title
		String title = row.getString(0).trim().toLowerCase ().replaceAll("\\s+\\W\\s*.*","");
		
		// Remove "Yrs of Exp" Redundancy From YearsExp
		String yr_exp = row.getString(5).trim().split(" ")[0] ;
		yr_exp = yr_exp.equals("null") ? "0" : yr_exp;
		
		// Trim The Data And Create Job Object
		return RowFactory.create(title,row.getString(1).trim(),row.getString(2).trim(),row.getString(3).trim(),
					row.getString(4).trim(),yr_exp,row.getString(6).trim(),row.getString(7).trim());
		});
		
		// Create Dataset From The Cleaned Data
		this.jobs_df = sparkSession.createDataFrame(Jobs_lst, this.jobs_df.schema());
    	return this.jobs_df;
    }
	
	
	public Object company_job_count(){
		System.out.println("\nThe Most Demanding Companies For Jobs");
		Dataset<Row> job_count = this.jobs_df.groupBy("company").count().sort(desc("count"));
		job_count.show();
		this.drawPieChart("The Most Demanding Companies For Jobs", job_count.limit(5));
		return job_count.limit(10).toJSON().collect().toString();
	}
	
	public Object getPopularTitle() {
		System.out.println("\nThe Most Popular Job Titles");
		Dataset<Row> pop_title = this.jobs_df.groupBy("title").count().sort(desc("count"));
		pop_title.show();
		this.drawBarChart("The Most Popular Job Titles", pop_title.limit(5));
		return pop_title.limit(10).toJSON().collect();
	}
	
	
	public Object getPopularArea(){
		System.out.println("\nThe Most Popular Areas");
		Dataset<Row> pop_area = this.jobs_df.groupBy("location").count().sort(desc("count"));
		pop_area.show();
		this.drawBarChart("The Most Popular Areas", pop_area.limit(5));
		return pop_area.limit(10).toJSON().collect();
	}
	
	
	public Object countSkill() {
		System.out.println("\nThe Most Important Skills Required");
		Dataset<String> skills_lst = this.jobs_df.flatMap((FlatMapFunction<Row, String>) row -> {
			return Arrays.asList (row.getString(7).split (", ")).iterator ();
			 }, Encoders.STRING());
		
		Dataset<Row> skills_df = skills_lst.groupBy("value").count().sort(desc("count"));
		skills_df.show();
		this.drawBarChart("The Most Important Skills Required", skills_df.limit(5));
		return skills_df.limit(10).toJSON().collect();
	}
	
	
	public void drawPieChart(String title, Dataset<Row> df) {
		PieChart chart = new PieChartBuilder ().width (1024).height (768).title (title).build ();
		
		List<String> value = df.map((MapFunction<Row, String>) row -> row.getString(0), 
				Encoders.STRING()).collectAsList();
		List<Integer> count = df.map((MapFunction<Row, Integer>) row -> (int) row.getLong(1),
				Encoders.INT()).collectAsList();
		
		for(int i=0;i<5;i++) chart.addSeries(value.get(i), count.get(i));
		new SwingWrapper<> (chart).displayChart ();
	}
	
	
	public void drawBarChart(String title, Dataset<Row> df){
		CategoryChart chart = new CategoryChartBuilder ().width (1024).height (768).title (title).xAxisTitle ("Data").yAxisTitle ("Count").build (); 
        // Customize Chart 
        chart.getStyler ().setLegendPosition (Styler.LegendPosition.InsideNW); 
        chart.getStyler ().setHasAnnotations (true); 
        chart.getStyler ().setStacked (true); 
        
		List<String> value = df.map((MapFunction<Row, String>) row -> row.getString(0), 
				Encoders.STRING()).collectAsList();
		List<Integer> count = df.map((MapFunction<Row, Integer>) row -> (int) row.getLong(1),
				Encoders.INT()).collectAsList();
		
		chart.addSeries (title, value,count);
        new SwingWrapper<> (chart).displayChart ();
	}
	
	public List<Job> getJobs(int n){
		Encoder<Job> jobEncoder = Encoders.bean(Job.class);
		
		return this.jobs_df.limit(n).toJavaRDD().map(row -> new Job(row.getString(0),row.getString(1),row.getString(2),row.getString(3),
				row.getString(4),row.getString(5),row.getString(6),row.getString(7))).collect();
		
	}
	
	
	public Object label_encoding(){
		Dataset<Row> jobs_encoded = this.jobs_df.withColumn("label encoding",regexp_replace(col("yearsExp"), "[-+].*",""));
		jobs_encoded.show();
		return jobs_encoded.toJSON().collect();
	}
	
}


//jobs_df.take(10).foreach((ForeachFunction<Row>) row -> System.out.println(row.length()));
//Encoder<Row> jobEncoder = Encoders.bean(Row.class);
//JavaRDD<Row> jobs_clean = jobs_df.map((MapFunction<Row,Row>) row ->{
//	String[] skills = row.getString(7).split(", ");
//	
//	String yr_exp = row.getString(5).trim().split(" ")[0] ;
//	yr_exp = yr_exp == "null" ? "0" : yr_exp;
//	
//	return  RowFactory.create(row.getString(0).trim(),row.getString(1).trim(),row.getString(2).trim(),row.getString(3).trim(),
//			row.getString(4).trim(),row.getString(5).trim().split(" ")[0],row.getString(6).trim(),row.getString(7).trim());
//}, jobEncoder).toJavaRDD();
//
//Dataset<Row> jobs_clean_df = sparkSession.createDataFrame(jobs_clean,jobs_df.schema());

//StructType x = jobs_df.schema().add("Skills", DataTypes.createArrayType(DataTypes.StringType));
//jobs_clean_df = jobs_clean_df.withColumn("Skills" , jobs_clean_df.col("Skills").cast(DataTypes.createArrayType(DataTypes.StringType)));

//SQLContext sqlContext = new SQLContext(sparkContext);
//Dataset<Row> fileDF = sqlContext.createDataFrame(jobs, Job.class).toDF();

//
//// Query raw data
//Dataset<Row> student = spark.sql("select * from `event`.`student`");
//// Generate schema
//List<StructField> fields = new ArrayList<>();
//fields.add(DataTypes.createStructField("id", DataTypes.StringType, true));
//fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
//fields.add(DataTypes.createStructField("major", DataTypes.StringType, true));
//StructType schema = DataTypes.createStructType(fields);
//
//// Convert the query result to POJO List
//List<Student> students = spark.createDataFrame(student.toJavaRDD(), schema)
//.as(Encoders.bean(Student.class))
//.collectAsList();


//this.jobs_lst = this.jobs_df.map((MapFunction<Row,Job>) row -> {
//	// Clean Title
//	String title = row.getString(0).trim().toLowerCase ().replaceAll("\\s+\\W\\s*.*","");
//	
//	// Remove "Yrs of Exp" Redundancy From YearsExp
//	String yr_exp = row.getString(5).trim().split(" ")[0] ;
//	yr_exp = yr_exp == "null" ? "0" : yr_exp;
//	
//	//Convert Skills To Array Of String
//	String[] skills = row.getString(7).split(", |/");
//	
//	// Trim The Data
//	return new Job(title,row.getString(1).trim(),row.getString(2).trim(),row.getString(3).trim(),
//			row.getString(4).trim(),yr_exp,row.getString(6).trim(),skills);
//}, jobEncoder ).collectAsList();