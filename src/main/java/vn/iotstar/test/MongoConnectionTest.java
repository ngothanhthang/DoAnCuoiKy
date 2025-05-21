/*
 * package vn.iotstar.test;
 * 
 * import com.mongodb.client.MongoClient; import
 * com.mongodb.client.MongoClients; import com.mongodb.client.MongoDatabase;
 * 
 * public class MongoConnectionTest { public static void main(String[] args) {
 * String connectionString = "mongodb://localhost:27017"; String databaseName =
 * "doancuoiky";
 * 
 * System.out.println("Attempting to connect to MongoDB...");
 * 
 * try (MongoClient mongoClient = MongoClients.create(connectionString)) { //
 * Thử lấy database MongoDatabase database =
 * mongoClient.getDatabase(databaseName);
 * 
 * // Liệt kê các collections System.out.println("Collections in database " +
 * databaseName + ":"); database.listCollectionNames().forEach(name ->
 * System.out.println("- " + name));
 * 
 * System.out.println("MongoDB connection test successful!"); } catch (Exception
 * e) { System.err.println("MongoDB connection failed: " + e.getMessage());
 * e.printStackTrace(); } } }
 */