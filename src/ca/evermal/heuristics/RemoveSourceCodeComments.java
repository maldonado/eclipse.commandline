package ca.evermal.heuristics;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveSourceCodeComments implements Heuristic{

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
			+ "catch\\s*\\("; 

	public RemoveSourceCodeComments(){
		System.out.println("Remove SourceCode comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove SourceCode comments heuristic");
		Pattern pattern = Pattern.compile(SOURCE_CODE_REGEX);
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				Matcher matcher = pattern.matcher(comment.getText());
				if(!matcher.find()){
					filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		System.out.println("Done...");
		return commentClasses;
	}
}