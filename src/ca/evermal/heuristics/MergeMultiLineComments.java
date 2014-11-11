package ca.evermal.heuristics;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;
import java.util.HashSet;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class MergeMultiLineComments implements Heuristic{

	public MergeMultiLineComments(){
		System.out.println("Merge Mult-line comments selected");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Starting Remove License comments heuristic");
		for (CommentClass commentClass : commentClasses) {
			HashSet<Comment> commentList = commentClass.getCommentList();
			HashSet<Comment> filtered = new HashSet<Comment>();
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