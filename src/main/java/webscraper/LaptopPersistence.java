package webscraper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import business.data.Laptop;
import persistence.jdbc.JdbcConnection;

public class LaptopPersistence {
	private static final String CREATE_LAPTOP_TABLE = "CREATE DATABASE IF NOT EXISTS `dealhunter`;"
			+ "USE `dealhunter`;"
			+ "CREATE TABLE IF NOT EXISTS `laptop` ("
			+ "    `laptop_id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
			+ "    `source` VARCHAR(255) NOT NULL,"
			+ "    `uri` VARCHAR(255) NOT NULL,"
			+ "    `name` VARCHAR(255) NOT NULL,"
			+ "    `reference` VARCHAR(255),"
			+ "    `image_uri` VARCHAR(255),"
			+ "    `screen_size` VARCHAR(255),"
			+ "    `screen_resolution` VARCHAR(255),"
			+ "    `cpu` VARCHAR(255),"
			+ "    `gpu` VARCHAR(255),"
			+ "    `ram` VARCHAR(255),"
			+ "    `storage` VARCHAR(255),"
			+ "    `os` VARCHAR(255),"
			+ "    `weight` VARCHAR(255),"
			+ "    `price` VARCHAR(255) NOT NULL,"
			+ "    `scraping_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
			+ ") ENGINE = InnoDB;";
	
	public static boolean createLaptopTable() {
		boolean success = false;
		
		Connection connection = null;
		Statement createStatement = null;
		try {
			connection = JdbcConnection.getConnection();
			createStatement = connection.createStatement();
			createStatement.execute(CREATE_LAPTOP_TABLE);
						
			System.out.println("Successfully created laptop table!");
			success = true;
		} catch (SQLException e) {
			System.err.println("Failed to create laptop table!\n" + e.getMessage());
			success = false;
	    } finally {
	    	try {
	    		if (createStatement != null) {
	    			createStatement.close();
	    		}
	    		if (connection != null) {
	    			connection.close();
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		
		return success;
	}
	
	public void bulkInsertLaptop(ArrayList<Laptop> laptops) {
		Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        connection = JdbcConnection.getConnection();
	        connection.setAutoCommit(false);

	        String compiledQuery = "INSERT INTO dealhunter.laptop(source, uri, name, reference, image_uri, screen_size, screen_resolution, cpu, gpu, ram, storage, os, weight, price)" +
	                " VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        preparedStatement = connection.prepareStatement(compiledQuery);

	        for(Laptop laptop : laptops) {
	        	preparedStatement.setString(1, laptop.getSource() == null ? null : laptop.getSource().substring(0, Math.min(laptop.getSource().length(), 255)));
	        	preparedStatement.setString(2, laptop.getUri() == null ? null : laptop.getUri().substring(0, Math.min(laptop.getUri().length(), 255)));
	        	preparedStatement.setString(3, laptop.getName() == null ? null : laptop.getName().substring(0, Math.min(laptop.getName().length(), 255)));
	        	preparedStatement.setString(4, laptop.getReference() == null ? null : laptop.getReference().substring(0, Math.min(laptop.getReference().length(), 255)));
	        	preparedStatement.setString(5, laptop.getImageUri() == null ? null : laptop.getImageUri().substring(0, Math.min(laptop.getImageUri().length(), 255)));
	        	preparedStatement.setString(6, laptop.getScreenSize() == null ? null : laptop.getScreenSize().substring(0, Math.min(laptop.getScreenSize().length(), 255)));
	        	preparedStatement.setString(7, laptop.getScreenResolution() == null ? null : laptop.getScreenResolution().substring(0, Math.min(laptop.getScreenResolution().length(), 255)));
	        	preparedStatement.setString(8, laptop.getCpu() == null ? null : laptop.getCpu().substring(0, Math.min(laptop.getCpu().length(), 255)));
	        	preparedStatement.setString(9, laptop.getGpu() == null ? null : laptop.getGpu().substring(0, Math.min(laptop.getGpu().length(), 255)));
	        	preparedStatement.setString(10, laptop.getRam() == null ? null : laptop.getRam().substring(0, Math.min(laptop.getRam().length(), 255)));
	        	preparedStatement.setString(11, laptop.getStorage() == null ? null : laptop.getStorage().substring(0, Math.min(laptop.getStorage().length(), 255)));
	        	preparedStatement.setString(12, laptop.getOperatingSystem() == null ? null : laptop.getOperatingSystem().substring(0, Math.min(laptop.getOperatingSystem().length(), 255)));
	        	preparedStatement.setString(13, laptop.getWeight() == null ? null : laptop.getWeight().substring(0, Math.min(laptop.getWeight().length(), 255)));
	        	preparedStatement.setString(14, laptop.getPrice() == null ? null : laptop.getPrice().substring(0, Math.min(laptop.getPrice().length(), 255)));
	            preparedStatement.addBatch();
	        }

	        long start = System.currentTimeMillis();
	        preparedStatement.executeBatch();
	        connection.commit();
	        long end = System.currentTimeMillis();

	        System.out.println("Total time taken to insert the batch = " + (end - start) + " ms");

	    } catch (SQLException ex) {
	        System.err.println("SQLException after bulk laptop insert");
	        while (ex != null) {
	            System.err.println("Error msg: " + ex.getMessage());
	            ex = ex.getNextException();
	        }
	        throw new RuntimeException("Error");
	    } finally {
	    	try {
	    		if (preparedStatement != null) {
	    			preparedStatement.close();
	    		}
	    		if (connection != null) {
	    			connection.close();
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	}

}
