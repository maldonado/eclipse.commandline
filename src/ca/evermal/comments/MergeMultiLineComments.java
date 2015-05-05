package ca.evermal.comments;

import gr.uom.java.ast.CommentType;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.ListIterator;

public class MergeMultiLineComments{

	private boolean hasToMergeAgain = true;

	public void start(String projectName, Connection connection) {
		System.out.println("Starting Merge Mult-line comments post-heuristc");
		
		while(hasToMergeAgain){
			hasToMergeAgain  = false;
			ArrayList<CommentClass> commentClasses = CommentClass.getAllThatHasProcessedComments(connection, projectName);
			for (CommentClass commentClass : commentClasses) {
				ArrayList<Comment> commentList = Comment.findProcessedByCommentClassId(connection, commentClass.getId());
				mergeComments(commentList, connection);
			}
		}
		System.out.println("Done...");
	}

	private void mergeComments(ArrayList<Comment> commentList, Connection connection) {
		ArrayList<Comment> sortedList = sortLineComments(commentList);
		ListIterator<Comment> iterator = sortedList.listIterator();
		while(iterator.hasNext()){
			Comment comment = iterator.next();
			if(!iterator.hasNext())
				break;
			Comment nextComment = iterator.next();
			iterator.previous();
			if((comment.getEndLine() - nextComment.getStartLine()) == -1){
				hasToMergeAgain = true;
				comment.setText(comment.getText().concat(" ").concat(nextComment.getText()));
				comment.setEndLine(nextComment.getEndLine());
				comment.setType(CommentType.MULTLINE);
				comment.updateProcessed(connection);
				nextComment.deleteProcessed(connection);
			}
		}
	}
	
	private ArrayList<Comment> sortLineComments(ArrayList<Comment> commentList){
		ArrayList<Comment> sortedComments =  new ArrayList<Comment>();
		for (Comment comment : commentList) {	
			if(CommentType.LINE.equals(comment.getType()) || CommentType.MULTLINE.equals(comment.getType()))
				sortedComments.add(comment);
		}
		return sortedComments;
	}
}