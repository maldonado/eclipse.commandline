package ca.evermal.comments;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.Standalone;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.distance.MyMethod;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckElimination;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckEliminationGroup;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

public class CommentAnalyzer {

	private static final int TRUE = 1;

	public void start(SystemObject systemObject, IJavaProject jproject, CommentClass commentClass ) {
		System.out.println("processing:" + commentClass.getClassName());
		ClassObject classObject = systemObject.getClassObject(commentClass.getClassName());
		for(Comment comment : commentClass.getCommentList()){
			if("CONSTRUCTOR".equals(comment.getLocation())){
				System.out.println("found comment located in the constructor level: " + comment.getDescription());
				checkTypeCheckingRefactorings(jproject, classObject, comment);
				checkForExtractClassRefactorings(jproject, classObject, comment);
			}
			else{
				System.out.println("found comment located in the method level: " + comment.getDescription());
				//checkExtractMethodsRefactorings(jproject, comment, classObject);
				checkMoveMethodRefactorings(jproject, classObject, comment);
				checkTypeCheckingRefactorings(jproject, classObject, comment);
				checkForExtractClassRefactorings(jproject, classObject, comment);
			}
			System.out.println("processed.");
		}
	}

	private void checkForExtractClassRefactorings(IJavaProject jproject, ClassObject classObject, Comment comment) {
		System.out.println("searching move method type checking opportunities");
		Set<ExtractClassCandidateGroup> extractClassRefactoringOpportunities = Standalone.getExtractClassRefactoringOpportunities(jproject, classObject);
		for(ExtractClassCandidateGroup group : extractClassRefactoringOpportunities){
			for(ExtractClassCandidateRefactoring candidate : group.getCandidates()){
				for(MyMethod oldMethods : candidate.getMyMethodsFromOldEntities()){
					String signature = oldMethods.getMethodObject().getSignature();
					if(comment.getDescription().equals(signature)){
						comment.setJdeodorantHit(TRUE);
						comment.setRefactoringListName("CHANGED METHOD IN GOD CLASS");
						comment.updateProcessed();	
					}
				}
			}
		}
		extractClassRefactoringOpportunities = null;
	}

	//	TODO: COMPARISON IS NOT PERFECT YET. METHODS CAN HAVE THE SAME NAME, HAVE TO TAKE IN CONSIDERATION THE PARAMETERS ALSO
	private void checkTypeCheckingRefactorings(IJavaProject jproject, ClassObject classObject, Comment comment) {
		System.out.println("searching move method type checking opportunities");
		Set<TypeCheckEliminationGroup> typeCheckEliminationRefactoringOpportunities = Standalone.getTypeCheckEliminationRefactoringOpportunities(jproject, classObject);
		for(TypeCheckEliminationGroup eliminationGroup : typeCheckEliminationRefactoringOpportunities){
			for(TypeCheckElimination candidate : eliminationGroup.getCandidates()){
				if(comment.getDescription().contains(candidate.getTypeCheckMethod().getName().toString())){
					comment.setJdeodorantHit(TRUE);
					comment.setRefactoringListName("MOVE METHOD");
					comment.updateProcessed();
				}	
			}
		}
		typeCheckEliminationRefactoringOpportunities = null;
	}

	private void checkExtractMethodsRefactorings(IJavaProject jproject, Comment comment, ClassObject classObject) {
		System.out.println("searching extract method opportunities");
		Set<ASTSliceGroup> extractMethodRefactoringOpportunities = Standalone.getExtractMethodRefactoringOpportunities(jproject, classObject);
		for (ASTSliceGroup sliceGroup : extractMethodRefactoringOpportunities){
			for(ASTSlice candidate : sliceGroup.getCandidates()){
				if(comment.getDescription().equals(candidate.getExtractedMethodSignature())){
					comment.setJdeodorantHit(TRUE);
					comment.setRefactoringListName("EXTRACT METHOD");
					comment.updateProcessed();
				}
			}
		}
		extractMethodRefactoringOpportunities = null;
	}

	private void checkMoveMethodRefactorings(IJavaProject jproject, ClassObject classObject, Comment comment) {
		System.out.println("searching move method opportunities");
		List<MoveMethodCandidateRefactoring> moveMethodRefactoringOpportunities = Standalone.getMoveMethodRefactoringOpportunities(jproject, classObject);
		for (MoveMethodCandidateRefactoring candidate : moveMethodRefactoringOpportunities){
			String signature = candidate.getSourceMethod().getMethodObject().getSignature();
			if(comment.getDescription().equals(signature)){
				comment.setJdeodorantHit(TRUE);
				comment.setRefactoringListName("MOVE METHOD");
				comment.updateProcessed();	
			}
		}
		moveMethodRefactoringOpportunities = null;
	}
}
