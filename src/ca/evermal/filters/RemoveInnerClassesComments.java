package ca.evermal.filters;

import java.sql.Connection;
import java.util.ArrayList;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveInnerClassesComments {
	
	private String projectName;
	private Connection connection;
	
	public RemoveInnerClassesComments(String projectName, Connection connection){
		this.projectName = projectName;
		this.connection = connection;
	}

	public void process() {
		System.out.println("Starting Remove Inner classes comments ");
		int numberOFDeletedComments = 0;
		ArrayList<String> notUniqueClassFileName = CommentClass.getNotUniqueClassFileName(connection, projectName);
		for (String fileName : notUniqueClassFileName) {
			ArrayList<CommentClass> commentClasses = CommentClass.getAllWithSameFileName(connection, fileName);
			String parentClassPath = null;
			if(!commentClasses.isEmpty()){
				parentClassPath = commentClasses.get(0).getClassName();
				commentClasses.remove(0);
			}
				
			for (CommentClass commentClass : commentClasses) {
				if( commentClass.getClassName().contains(parentClassPath)){
					ArrayList<Comment> commentList = Comment.findProcessedByCommentClassId(connection, commentClass.getId());
					for (Comment comment : commentList) {
						comment.deleteProcessed(connection);
						numberOFDeletedComments++;
					}
				}
			}
		}
		
		System.out.println("Done... Inner classes comments deleted = " + numberOFDeletedComments);
	}
}