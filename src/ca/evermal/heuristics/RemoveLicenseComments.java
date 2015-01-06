package ca.evermal.heuristics;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveLicenseComments implements Heuristic{
	
	private static final String REGEX_TO_KEEP = 
			"TODO:|"
			+ "XXX|"
			+ "FIXME";
	
	private static final String REGEX_TO_ELININATE = 
			"copyright";
	
	public RemoveLicenseComments(){
		System.out.println("Remove License comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove License comments heuristic");
		for (CommentClass commentClass : commentClasses) {
			int classStartLine = commentClass.getStartLine();
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				if(comment.getEndLine() > classStartLine){
					Pattern pattern = Pattern.compile(REGEX_TO_ELININATE ,Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(comment.getText());
					if(!matcher.find())
						filtered.add(comment);
				}else{
					Pattern pattern = Pattern.compile(REGEX_TO_KEEP);
					Matcher matcher = pattern.matcher(comment.getText());
					if(matcher.find())
						filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		System.out.println("Done...");
		return commentClasses;
	}
}