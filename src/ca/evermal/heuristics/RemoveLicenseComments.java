package ca.evermal.heuristics;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class RemoveLicenseComments implements Heuristic{

	public RemoveLicenseComments(){
		System.out.println("Remove License comments selected.");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove License comments heuristic");
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = commentClass.getCommentList();
			ArrayList<Comment> filtered = new ArrayList<Comment>();
			for (Comment comment : commentList) {
				if(!CommentType.JAVADOC.equals(comment.getType())){
					filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		System.out.println("Done...");
		return commentClasses;
	}
}