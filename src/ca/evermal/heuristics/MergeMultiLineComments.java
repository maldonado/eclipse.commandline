package ca.evermal.heuristics;

import gr.uom.java.ast.CommentType;

import java.util.ArrayList;
import java.util.ListIterator;

import ca.evermal.comments.Comment;
import ca.evermal.comments.CommentClass;

public class MergeMultiLineComments implements Heuristic{

	public MergeMultiLineComments(){
		System.out.println("Merge Mult-line comments selected");
	}
	
	@Override
	public ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses) {
		System.out.println("Merge Mult-line comments selected");
		for (CommentClass commentClass : commentClasses) {
			ArrayList<Comment> commentList = commentClass.getCommentList();
			mergeComments(commentList, commentClass.getId());
		}
		System.out.println("Done...");
		return commentClasses;
	}

	private void mergeComments(ArrayList<Comment> commentList, long commentClassId) {
		ListIterator<Comment> iterator = commentList.listIterator();
		while(iterator.hasNext()){
			Comment comment = iterator.next();
			if(!iterator.hasNext())
				break;
			Comment nextComment = iterator.next();
//			iterator.previous();
			if((comment.getEndLine() - nextComment.getEndLine()) == -1 || (comment.getEndLine() - nextComment.getStartLine()) == 0  ){
				comment.setText(comment.getText().concat(nextComment.getText()));
				comment.setEndLine(nextComment.getEndLine());
				comment.setType(CommentType.MULTLINE);
				comment.update();
				nextComment.delete();
				mergeComments(Comment.findByCommentClassId(commentClassId), commentClassId);
			}
		}
	}
}