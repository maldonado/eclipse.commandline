package ca.evermal.heuristics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveSourceCodeComments implements Heuristic{

	private static final String SOURCE_CODE_REGEX = "\\b(abstract|continue|for|new|switch|assert|default|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|float|native|super|whil)\\b"; 
	
	public RemoveSourceCodeComments(){
		System.out.println("Remove SourceCode comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove SourceCode comments heuristic");
		Pattern pattern = Pattern.compile(SOURCE_CODE_REGEX);
		for (CommentClass commentClass : commentClasses) {
			HashSet<Comment> commentList = commentClass.getCommentList();
			HashSet<Comment> filtered = new HashSet<Comment>();
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