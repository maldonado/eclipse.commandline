package ca.evermal.comments;

import java.sql.Connection;

import ca.evermal.util.ConnectionFactory;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;

public class CommentExtractor {
	
	public static boolean extractFrom(SystemObject systemObject){
		Connection connection = ConnectionFactory.createPostgresqlTables();
		int totalNumberClasses = systemObject.getClassNumber(); 
		int counter = 0;
		
		for (ClassObject classObject : systemObject.getClassObjects()) {
			CommentClass commentClass =  new CommentClass(classObject, connection);
			commentClass.insert();
			counter ++;
			System.out.println(counter + " out of: " + totalNumberClasses );
		}
		return true;
	}
}