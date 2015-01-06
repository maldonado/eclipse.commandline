package ca.evermal.comments;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;
import java.util.ListIterator;

public class MergeMultiLineComments{

	private static boolean hasToMergeAgain = true;

	public static void Start() {
		System.out.println("Starting Merge Mult-line comments post-heuristc");
		ArrayList<CommentClass> commentClasses = CommentClass.getAllThatHasProcessedComments();
		while(hasToMergeAgain){
			hasToMergeAgain  = false;
			for (CommentClass commentClass : commentClasses) {
				ArrayList<Comment> commentList = Comment.findProcessedByCommentClassId(commentClass.getId());
				mergeComments(commentList);
			}
		}
		System.out.println("Done...");
	}

	private static void mergeComments(ArrayList<Comment> commentList) {
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
				comment.setText(comment.getText().concat(nextComment.getText()));
				comment.setEndLine(nextComment.getEndLine());
				comment.setType(CommentType.MULTLINE);
				comment.updateProcessed();
				nextComment.deleteProcessed();
			}
		}
	}
	
	private static ArrayList<Comment> sortLineComments(ArrayList<Comment> commentList){
		ArrayList<Comment> sortedComments =  new ArrayList<Comment>();
		for (Comment comment : commentList) {	
			if(CommentType.LINE.equals(comment.getType()) || CommentType.MULTLINE.equals(comment.getType()))
				sortedComments.add(comment);
		}
		return sortedComments;
	}
}