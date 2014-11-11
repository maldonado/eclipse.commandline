package ca.evermal.heuristics;

import java.util.ArrayList;

import ca.evermal.comments.CommentClass;

public interface Heuristic {
	
	ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses);

}