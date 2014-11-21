package ca.evermal.heuristics;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveJavaDocComments implements Heuristic {

	private static final String REGEX = 
			"TODO:|"
			+ "XXX|"
			+ "FIXME";

	public RemoveJavaDocComments(){
		System.out.println("Remove JAVADOC comments selected.");
	}

	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove JAVADOC comments heuristic");
		Pattern pattern = Pattern.compile(REGEX);
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				if(!CommentType.JAVADOC.equals(comment.getType())){
					filtered.add(comment);
				}else {
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