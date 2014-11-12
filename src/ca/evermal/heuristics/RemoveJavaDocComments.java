package ca.evermal.heuristics;

import java.util.ArrayList;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveJavaDocComments implements Heuristic {

	public RemoveJavaDocComments(){
		System.out.println("Remove JAVADOC comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting heuristic: Remove JAVADOC comments");
		for (CommentClass commentClass : commentClasses) {
			int classStartLine = commentClass.getStartLine();
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				if(comment.getEndLine() > classStartLine){
					filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		System.out.println("Done...");
		return commentClasses;
	}
}