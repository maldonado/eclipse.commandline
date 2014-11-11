package ca.evermal.comments;

import java.util.ArrayList;
import java.util.HashSet;

import ca.evermal.heuristics.Heuristic;
import ca.evermal.heuristics.RemoveJavaDocComments;
import ca.evermal.heuristics.RemoveLicenseComments;
import ca.evermal.heuristics.RemoveSourceCodeComments;

public class CommentProcessor {
	
	public void execute(){
		ArrayList<CommentClass> commentClasses = CommentClass.getAll();
		processHeuristics(selectHeuristics(), commentClasses);
		insertProcessedComments(commentClasses);
	}

	private void insertProcessedComments(ArrayList<CommentClass> commentClasses) {
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
	
	private void processHeuristics(HashSet<Heuristic> selectHeuristics, ArrayList<CommentClass> commentClasses) {
		for (Heuristic heuristic : selectHeuristics) {
			heuristic.process(commentClasses);
		}
	}

	private HashSet<Heuristic> selectHeuristics() {
		HashSet<Heuristic> selection = new HashSet<Heuristic>();
		selection.add(new RemoveJavaDocComments());
		selection.add(new RemoveLicenseComments());
		selection.add(new RemoveSourceCodeComments());
		return selection;
	}
}