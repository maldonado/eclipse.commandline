package ca.evermal.comments;

import ca.evermal.util.ConnectionFactory;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.SystemObject;

public class CommentsExtractor {
	
	public static boolean extractFrom(SystemObject systemObject){
		ConnectionFactory.verifyDataBase();
		int totalNumberClasses = systemObject.getClassNumber(); 
		int counter = 0;
		
//		for (ClassObject classObject : systemObject.getClassObjects()) {
//			CommentClass commentClass =  new CommentClass(classObject);
//			commentClass.insert();
//			counter ++;
//			System.out.println(counter + "out of:" + totalNumberClasses );
//		}
		
		CommentProcessor processor = new CommentProcessor();
		processor.process();
		return true;
	}
}