package ca.evermal.comments;

import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.MethodObject;
import gr.uom.java.ast.Standalone;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckEliminationGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;

public class CommentAnalyzer {

	public void start(SystemObject systemObject, IJavaProject jproject, String projectName) {
		ArrayList<CommentClass> commentClassesWithDictionaryMatches = CommentClass.getDictionaryMatchedByProject(projectName);
		for (CommentClass commentClass : commentClassesWithDictionaryMatches) {
			ClassObject classObject = systemObject.getClassObject(commentClass.getClassName());
			Set<ExtractClassCandidateGroup> extractClassRefactoringOpportunities = Standalone.getExtractClassRefactoringOpportunities(jproject, classObject);
			Set<ASTSliceGroup> extractMethodRefactoringOpportunities = Standalone.getExtractMethodRefactoringOpportunities(jproject, classObject);
			Set<TypeCheckEliminationGroup> typeCheckEliminationRefactoringOpportunities = Standalone.getTypeCheckEliminationRefactoringOpportunities(jproject, classObject);
			List<MoveMethodCandidateRefactoring> moveMethodRefactoringOpportunities = Standalone.getMoveMethodRefactoringOpportunities(jproject, classObject);
			
			
			
			
//			ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();
//			MethodObject foundMethod = null;			
//			while(methodIterator.hasNext() ){
//				MethodObject method = methodIterator.next();
//				//pass the method signature field here !
//				if(method.getSignature().equals("")){
//					foundMethod = method;
//					break;
//				}
//			}
		}

		
		
	}

}
