package ca.evermal.filters;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveSourceCodeComments {
	
	private String projectName;
	private Connection connection;
	
	public RemoveSourceCodeComments(String projecName, Connection connection){
		this.projectName = projecName;
		this.connection = connection;
	}
	
	private static final String SOURCE_CODE_REGEX = 
			"else\\s*\\{|"
			+ "try\\s*\\{|"
			+ "do\\s*\\{|"
			+ "finally\\s*\\{|"
			+ "if\\s*\\(|"
			+ "for\\s*\\(|"
			+ "while\\s*\\(|"
			+ "switch\\s*\\(|"
			+ "Long\\s*\\(|"
			+ "Byte\\s*\\(|"
			+ "Double\\s*\\(|"
			+ "Float\\s*\\(|"
			+ "Integer\\s*\\(|"
			+ "Short\\s*\\(|"
			+ "BigDecimal\\s*\\(|"
			+ "BigInteger\\s*\\(|"
			+ "Character\\s*\\(|"
			+ "Boolean\\s*\\(|"
			+ "String\\s*\\(|"
			+ "assert\\s*\\(|"
			+ "System.out.|"
			+ "public\\s*void|"
			+ "private\\s*static\\*final|"
			+ "catch\\s*\\("; 

	public void process() {
		System.out.println("Starting Remove SourceCode comments heuristic");
		Pattern pattern = Pattern.compile(SOURCE_CODE_REGEX);
		ArrayList<CommentClass> commentClasses = CommentClass.getAllThatHasProcessedComments(connection, projectName);
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = Comment.findProcessedByCommentClassId(connection, commentClass.getId());
			for (Comment comment : commentList) {
				Matcher matcher = pattern.matcher(comment.getText());
				if(matcher.find()){
					comment.deleteProcessed(connection);
				}
			}
		}
		System.out.println("Done...");
	}
}