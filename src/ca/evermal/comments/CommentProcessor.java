package ca.evermal.comments;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;
import java.util.HashSet;

public class CommentProcessor {
	
	public void process(){
		
		System.out.println("Loading inserted files");
		ArrayList<CommentClass> commentClasses = CommentClass.getAll();
		System.out.println("classes load");
		removeLicenseComments(commentClasses);
		System.out.println("heuristic one done");
		removeJavaDocComments(commentClasses);
		System.out.println("heuristic two done");
		
		int totalNumberClasses = commentClasses.size(); 
		int counter = 0;
		for (CommentClass commentClass : commentClasses) {
			for(Comment comment : commentClass.getCommentList()){
				comment.insertProcessed();
			}
			counter ++;
			System.out.println(counter + "out of:" + totalNumberClasses );
		}
	}
	
	private ArrayList<CommentClass> removeLicenseComments(ArrayList<CommentClass> commentClasses){
		for (CommentClass commentClass : commentClasses) {
			int classStartLine = commentClass.getStartLine();
			HashSet<Comment> commentList = commentClass.getCommentList();
			HashSet<Comment> filtered = new HashSet<Comment>();
			for (Comment comment : commentList) {
				if(comment.getEndLine() > classStartLine){
					filtered.add(comment);
				}
			}
			commentClass.setCommentList(filtered);
		}
		return commentClasses;
	}
	
	private ArrayList<CommentClass> removeJavaDocComments(ArrayList<CommentClass> commentClasses){
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
		return commentClasses;
	}
}