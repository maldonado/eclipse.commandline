package ca.evermal.filters;

import java.util.ArrayList;

import ca.evermal.comments.CommentClass;

public interface Heuristic {
	
	ArrayList<CommentClass> process(ArrayList<CommentClass> commentClasses);

}