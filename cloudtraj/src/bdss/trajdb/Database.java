package bdss.trajdb;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.TabExpander;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableBatchOperation;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.Operators;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

import bdss.model.PathTrip;
import bdss.model.TaxiCheckPoint;
import bdss.model.TaxiTrip;
import bdss.model.coordinates;
import bdss.model.Marker;

public class Database {	
	String connectionString = "DefaultEndpointsProtocol=https;AccountName=master-bdss;"
			+ "AccountKey=4geOgLkJwa1HQrAptqgopsnKnFTRCKrUnVpEcuAE7WIJx9jzzZcNnUZPp76KZPzjxjBYdaxUx6ctAjSmMQgxVA==;"
			+ "TableEndpoint=https://master-bdss.table.cosmos.azure.com:443/;";
	CloudStorageAccount storageAccount;
	CloudTableClient tableClient;
	CloudTable cloudTable, taxiTable;
	
    String pathTableName = "trajectory"; // table for path query
    String taxiTableName = "taxi-trip-"; // table for id query
	
	public void connect(){
		try
		{
		    // Retrieve storage account from connection-string.
			storageAccount = CloudStorageAccount.parse(connectionString);

		    // Create the table client.
		    tableClient = storageAccount.createCloudTableClient();
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
	}
	
	public void taxiIndexing(List<PathTrip> trips) {
		try
		{
			// Create distinct for table creation
			// Handle request issues
			List<String> oldTaxiId = new ArrayList<String>();
			for (PathTrip pathTrip : trips) {
			    // Create the table if it doesn't exist.
				oldTaxiId.add(pathTrip.getRowKey());
			}
			
			List<String> newTaxiId = oldTaxiId.stream()
					.distinct() 
                    .collect(Collectors.toList()); 
			
			System.out.println(newTaxiId.size());

			int k = 1;
			for (String taxi : newTaxiId) {
			    // Create the table if it doesn't exist.
				System.out.println("Itteration "+k);
			    cloudTable = tableClient.getTableReference(taxiTableName+taxi);
			    cloudTable.createIfNotExists();		
			    k++;
			}
			
			int i = 1;
		    for (PathTrip t : trips) {
				System.out.println("Taxi Trip "+i);
			    taxiTable = tableClient.getTableReference(taxiTableName+t.getRowKey());

		    	int time = Integer.parseInt(t.getPartitionKey());
		    	String[] latlon = t.getPOLYLINE().replaceAll("[\\[\\]\"]", "").split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
	    		double lat = 0, lon = 0;
		    	
	    		// Add all trip
	    		for (int j = 0; j < latlon.length; j++) {
					if (j % 2 == 0) {
						lat = Double.parseDouble(latlon[j]);
					} else {
						lon = Double.parseDouble(latlon[j]);
				 
						// Create an operation to add the new taxi to the trajectory table.
				        TableOperation addTrip = TableOperation.insertOrReplace(new TaxiCheckPoint(t.getPartitionKey(), time, lat, lon));

				        // Execute the batch of operations on the "people" table.
				        taxiTable.execute(addTrip);
						time+=15;
					}
				}
			    i++;
			}
		    
		    // Return Message
		    System.out.println("Taxi Indexing Success!!!");
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}		
	}
	
	public void tripIndexing(List<PathTrip> trips) {
		try
		{
		    // Create the table if it doesn't exist.
		    cloudTable = tableClient.getTableReference(pathTableName);
		    cloudTable.createIfNotExists();
			
		    // Add trajectory entity to the table.
		    for (PathTrip t : trips) {
		    	
		        // Create an operation to add the new taxi to the trajectory table.
		        TableOperation addTrip = TableOperation.insertOrReplace(t);

		        // Execute the batch of operations on the "people" table.
		        cloudTable.execute(addTrip);

			}
		    
		    // Return Message
		    System.out.println("Trip Indexing Success!!!");
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
	}
			
	public List<TaxiCheckPoint> taxiQuery(String taxiId, String startDate, String endDate) {
		List<TaxiCheckPoint> tempTaxi = new ArrayList<TaxiCheckPoint>();
		try {
		    // Create the table if it doesn't exist.
		    cloudTable = tableClient.getTableReference(taxiTableName+taxiId);
	    	
		    // Define constants for filters.
		    final String ROW_KEY = "RowKey";
		    		    
		    // Create a filter condition where the row key is less than the letter "E".
		    String startFilter = TableQuery.generateFilterCondition(
		    		ROW_KEY,
		        QueryComparisons.GREATER_THAN_OR_EQUAL,
		        startDate);
		    
		    String endFilter = TableQuery.generateFilterCondition(
		    		ROW_KEY,
			        QueryComparisons.LESS_THAN,
			        endDate);

		    // Combine the two conditions into a filter expression.
		    String combinedFilter = TableQuery.combineFilters(startFilter,
		        Operators.AND, endFilter);
		    		    
	    	// with the row key being up to the letter "E".
		    TableQuery<TaxiCheckPoint> rangeQuery =
		        TableQuery.from(TaxiCheckPoint.class).where(combinedFilter);

		    // Loop through the results, displaying information about the entity

		    for (TaxiCheckPoint entity : cloudTable.execute(rangeQuery)) {
		    	tempTaxi.add(new TaxiCheckPoint(entity.getRowKey(), entity.getLat(), entity.getLng()));
	    				    	
		    }
		    
//		    System.out.println(tempTrip.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return tempTaxi;
	}
	
	public List<TaxiTrip> tripQuery(String startDate, String endDate) {
		System.out.println(Integer.parseInt(startDate));
		System.out.println(Integer.parseInt(endDate));
    	List<TaxiTrip> tempTrip = new ArrayList<TaxiTrip>();
		try
		{
		    cloudTable = tableClient.getTableReference(pathTableName);
			System.out.println(cloudTable);
		    // Define constants for filters.
		    final String PARTITION_KEY = "PartitionKey";

		    // Create a filter condition where the row key is less than the letter "E".
		    String startFilter = TableQuery.generateFilterCondition(
		        PARTITION_KEY,
		        QueryComparisons.GREATER_THAN_OR_EQUAL,
		      startDate);
		    
		    String endFilter = TableQuery.generateFilterCondition(
			        PARTITION_KEY,
			        QueryComparisons.LESS_THAN,
			        endDate);

		    // Combine the two conditions into a filter expression.
		    String combinedFilter = TableQuery.combineFilters(startFilter,
		        Operators.AND, endFilter);
		    // Specify a range query, using "Smith" as the partition key,
		    // with the row key being up to the letter "E".
		    TableQuery<PathTrip> rangeQuery =
		        TableQuery.from(PathTrip.class)
		        .where(combinedFilter);
		    // Loop through the results, displaying information about the entity
		    int i = 1;
		    for (PathTrip entity : cloudTable.execute(rangeQuery)) {
		    	System.out.println("Trip "+ i);
		    	int time = Integer.parseInt(entity.getPartitionKey());
		    	String[] latlon = entity.getPOLYLINE().replaceAll("[\\[\\]\"]", "").split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
	    		double lat = 0, lon = 0;
	    		List<Marker> tempPoly = new ArrayList<Marker>();
		    	
	    		// Add all trip
	    		for (int j = 0; j < latlon.length; j++) {
					if (j % 2 == 0) {
						lat = Double.parseDouble(latlon[j]);
					} else {
						lon = Double.parseDouble(latlon[j]);
						tempPoly.add(new Marker(time, lat, lon));
						time+=15;
					}
				}
		    	
	    		// Push all trip into one record
	    		tempTrip.add(new TaxiTrip(entity.getRowKey(), tempPoly));
	    		i++;
		    }
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
		
		return tempTrip;
	}
}
