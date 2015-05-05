package ca.evermal.comments;

import java.sql.Connection;
import java.util.ArrayList;

import ca.evermal.heuristics.Heuristic;
import ca.evermal.heuristics.RemoveJavaDocComments;
import ca.evermal.heuristics.RemoveLicenseComments;
import ca.evermal.heuristics.RemoveSourceCodeComments;
import ca.evermal.util.ConnectionFactory;

public class CommentProcessor {
	
	private String projectName;
	
	public CommentProcessor(String projectName) {
		this.projectName =  projectName;
	}

	public void execute(){
		Connection connection = ConnectionFactory.getPostgresql();
		ArrayList<CommentClass> commentClasses = CommentClass.getAll(connection, projectName);
		processHeuristics(selectHeuristics(), commentClasses);
		insertProcessedComments(connection, commentClasses);
		new MergeMultiLineComments().start(projectName, connection);
		new RemoveSourceCodeComments(projectName, connection).process();
	}
	
	public void matcheExpressionDictionary(){
		Comment.MatchDictionary();
	}
	
	private void insertProcessedComments(Connection connection, ArrayList<CommentClass> commentClasses) {
		int totalNumberClasses = commentClasses.size(); 
		int counter = 0;
		for (CommentClass commentClass : commentClasses) {
			for(Comment comment : commentClass.getCommentList()){
				comment.insertProcessed(connection);
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
		return selection;
	}

//	public void executeMergeMultiLines() {
//		MergeMultiLineComments.Start();	
//	}
}