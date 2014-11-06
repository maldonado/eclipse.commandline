package eclipse.commandline;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ListIterator;

import org.eclipse.jdt.core.IJavaProject;

public class CommentClass {


	private long id;
	private boolean _abstract;
	private boolean _enum;
	private boolean _interface;
	private Access access;
	private String projectName;
	private String fileName;
	private String className;
	private HashSet<Comment> commentList;

	public CommentClass(ClassObject classObject){
		IJavaProject examinedProject = ASTReader.getExaminedProject();
		this.projectName = examinedProject.getElementName();

		this._abstract = classObject.isAbstract();
		this._interface = classObject.isInterface();
		this._enum = classObject.isEnum();
		this.access = classObject.getAccess();
		this.fileName = classObject.getIFile().getName();
		this.className = classObject.getName();
		this.commentList = setCommentList(classObject.getCommentIterator(), classObject.getFieldIterator(), classObject.getMethodIterator(), classObject.getConstructorIterator());
	}

	private HashSet<Comment> setCommentList(ListIterator<CommentObject> allComments, ListIterator<FieldObject> allFields, ListIterator<MethodObject> allMethods, ListIterator<ConstructorObject> allConstructors) {
		HashSet<Comment> commentList = new HashSet<Comment>();
		populateFieldComments(allComments, allFields, commentList);
		populateConstructorComments(allComments, allConstructors, commentList);
		populateMethodsCommients(allComments, allMethods, commentList);
		populateClassComments(allComments, commentList);
		return commentList;
	}

	private void populateClassComments(ListIterator<CommentObject> allComments, HashSet<Comment> commentList) {
		while(allComments.hasNext()){
			Comment comment = new Comment(this.className, allComments.next());
			commentList.add(comment);
		}
	}

	private void populateMethodsCommients(ListIterator<CommentObject> allComments,
			ListIterator<MethodObject> allMethods, HashSet<Comment> commentList) {
		while(allMethods.hasNext()){
			MethodObject method = allMethods.next();
			ListIterator<CommentObject> methodComments = method.getCommentListIterator();
			while(methodComments.hasNext()){
				CommentObject methodCommentObject = methodComments.next();
				Comment comment = new Comment(method, methodCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(methodCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}

	private void populateConstructorComments(ListIterator<CommentObject> allComments,
			ListIterator<ConstructorObject> allConstructors, HashSet<Comment> commentList) {
		while(allConstructors.hasNext()){
			ConstructorObject constructor = allConstructors.next();
			ListIterator<CommentObject> constructorComments = constructor.getCommentListIterator();
			while(constructorComments.hasNext()){
				CommentObject fieldCommentObject = constructorComments.next();
				Comment comment = new Comment(constructor, fieldCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(fieldCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}

	private void populateFieldComments(ListIterator<CommentObject> allComments, ListIterator<FieldObject> allFields,
			HashSet<Comment> commentList) {
		while(allFields.hasNext()){
			FieldObject field = allFields.next();
			ListIterator<CommentObject> fieldComments = field.getCommentListIterator();
			while(fieldComments.hasNext()){
				CommentObject fieldCommentObject = fieldComments.next();
				Comment comment = new Comment(field, fieldCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(fieldCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}


	public void insert() {
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try {

			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("INSERT INTO comment_class (projectName, "
					+ "fileName, className, access, isAbstract, isEnum, isInterface) values (?,?,?,?,?,?,?)");

			preparedStatement.setString(1, this.projectName);
			preparedStatement.setString(2, this.fileName);
			preparedStatement.setString(3, this.className);
			preparedStatement.setString(4, this.access.toString());
			preparedStatement.setString(5, _abstract ? "true" : "false");
			preparedStatement.setString(6, _enum ? "true" : "false");
			preparedStatement.setString(7, _interface ? "true" : "false");
			preparedStatement.execute();
			this.id = preparedStatement.getGeneratedKeys().getLong(1);
			dataBaseConnection.close();
			for (Comment comment : commentList) {
				comment.insert(this.id);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}