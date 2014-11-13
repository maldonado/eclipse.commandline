package ca.evermal.heuristics;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class FilterTaskComments implements Heuristic{

	private static final String SOURCE_CODE_REGEX = "TODO:"; 

	public FilterTaskComments(){
		System.out.println("Filter task comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Filter task comments heuristic");
		Pattern pattern = Pattern.compile(SOURCE_CODE_REGEX);
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				Matcher matcher = pattern.matcher(comment.getText());
				if(matcher.find()){
					filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		System.out.println("Done...");
		return commentClasses;
	}
}