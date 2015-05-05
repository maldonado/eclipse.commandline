package ca.evermal.comments;

import java.util.ArrayList;

import ca.evermal.heuristics.Heuristic;
import ca.evermal.heuristics.RemoveJavaDocComments;
import ca.evermal.heuristics.RemoveLicenseComments;
import ca.evermal.heuristics.RemoveSourceCodeComments;
import ca.evermal.util.ConnectionFactory;

public class CommentProcessor {
	
	public void execute(){
		ArrayList<CommentClass> commentClasses = CommentClass.getAll(ConnectionFactory.getPostgresql());
		processHeuristics(selectHeuristics(), commentClasses);
		insertProcessedComments(commentClasses);
//		matcheExpressionDictionary();
	}
	
	public void matcheExpressionDictionary(){
		Comment.MatchDictionary();
	}
	
	private void insertProcessedComments(ArrayList<CommentClass> commentClasses) {
		int totalNumberClasses = commentClasses.size(); 
		int counter = 0;
		for (CommentClass commentClass : commentClasses) {
			for(Comment comment : commentClass.getCommentList()){
				comment.insertProcessed();
			}
			counter ++;
			System.out.println(counter + " out of: " + totalNumberClasses );
		}
	}
	
	private void processHeuristics(ArrayList<Heuristic> selectHeuristics, ArrayList<CommentClass> commentClasses) {
		for (Heuristic heuristic : selectHeuristics) {
			heuristic.process(commentClasses);
		}
	}

	private ArrayList<Heuristic> selectHeuristics() {
		ArrayList<Heuristic> selection = new ArrayList<Heuristic>();
//		selection.add(new FilterTaskComments());
		selection.add(new RemoveJavaDocComments());
		selection.add(new RemoveLicenseComments());
		selection.add(new RemoveSourceCodeComments());
		return selection;
	}

	public void executeMergeMultiLines() {
		MergeMultiLineComments.Start();	
	}
}