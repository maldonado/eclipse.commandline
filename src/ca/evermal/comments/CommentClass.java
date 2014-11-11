package ca.evermal.comments;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

import org.eclipse.jdt.core.IJavaProject;

import ca.evermal.util.ConnectionFactory;

public class CommentClass {


	private static final int LINE_CORRECTION = 1;
	
	private long id;
	private boolean _abstract;
	private boolean _enum;
	private boolean _interface;
	private Access access;
	private String projectName;
	private String fileName;
	private String className;
	private HashSet<Comment> commentList;
	private int startLine;
	private int endLine;

	public CommentClass(){};
	
	public CommentClass(ClassObject classObject){
		IJavaProject examinedProject = ASTReader.getExaminedProject();
		this.projectName = examinedProject.getElementName();

		this._abstract = classObject.isAbstract();
		this._interface = classObject.isInterface();
		this._enum = classObject.isEnum();
		this.access = classObject.getAccess();
		this.fileName = classObject.getIFile().getName();
		this.className = classObject.getName();
		this.startLine =  classObject.getStartLine();
		this.endLine = classObject.getEndLine();
		this.commentList = setCommentList(classObject.getCommentIterator(), classObject.getFieldIterator(), classObject.getMethodIterator(), classObject.getConstructorIterator());
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public void set_abstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public void set_enum(boolean _enum) {
		this._enum = _enum;
	}

	public void set_interface(boolean _interface) {
		this._interface = _interface;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setCommentList(HashSet<Comment> commentList) {
		this.commentList = commentList;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public long getId() {
		return id;
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
					+ "fileName, className, access, isAbstract, isEnum, isInterface, startLine, endLine) values (?,?,?,?,?,?,?,?,?)");

			preparedStatement.setString(1, this.projectName);
			preparedStatement.setString(2, this.fileName);
			preparedStatement.setString(3, this.className);
			preparedStatement.setString(4, this.access.toString());
			preparedStatement.setString(5, _abstract ? "true" : "false");
			preparedStatement.setString(6, _enum ? "true" : "false");
			preparedStatement.setString(7, _interface ? "true" : "false");
			preparedStatement.setInt(8, startLine + LINE_CORRECTION);
			preparedStatement.setInt(9, endLine + LINE_CORRECTION);
			preparedStatement.execute();
			this.id = preparedStatement.getGeneratedKeys().getLong(1);
			dataBaseConnection.close();
			for (Comment comment : commentList) {
				comment.insert(this.id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<CommentClass> getAll() {
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		ArrayList<CommentClass> result = new ArrayList<CommentClass>();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("SELECT * FROM comment_class");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CommentClass commentClass = new CommentClass();
				commentClass.setId(resultSet.getLong("id"));
				commentClass.set_abstract(resultSet.getString("isAbstract").equals("true") ? true : false);
				commentClass.set_enum(resultSet.getString("isEnum").equals("true") ? true : false);
				commentClass.set_interface(resultSet.getString("isInterface").equals("true") ? true : false);
				commentClass.setAccess(Access.valueOf(resultSet.getString("access")));
				commentClass.setProjectName(resultSet.getString("projectName"));
				commentClass.setFileName(resultSet.getString("fileName"));
				commentClass.setClassName(resultSet.getString("className"));
				commentClass.setStartLine(resultSet.getInt("startLine")); 
				commentClass.setEndLine(resultSet.getInt("endLine")); 
				commentClass.setCommentList(Comment.findByCommentClassId(commentClass.getId()));
				result.add(commentClass);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}
}